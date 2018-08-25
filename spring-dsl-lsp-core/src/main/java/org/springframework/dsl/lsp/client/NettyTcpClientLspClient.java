/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.dsl.lsp.client;

import java.io.IOException;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.jsonrpc.JsonRpcRequest;
import org.springframework.dsl.jsonrpc.JsonRpcResponse;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcRequest;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcResponse;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.tcp.TcpClient;

/**
 * {@link LspClient} connecting to a known {@code Language Server} address.
 *
 * @author Janne Valkealahti
 *
 */
public class NettyTcpClientLspClient implements LspClient {

	private static final Logger log = LoggerFactory.getLogger(NettyTcpClientLspClient.class);

	private EmitterProcessor<ByteBuf> requests = EmitterProcessor.create();
	private EmitterProcessor<String> responses = EmitterProcessor.create();
	private final String host;
	private final Integer port;
	public ClientReactorJsonRpcHandlerAdapter adapter;

	public NettyTcpClientLspClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public Mono<String> sendReceive(String request) {
		requests.onNext(Unpooled.copiedBuffer(request.getBytes()));
		return Mono.from(responses);
	}

	public void init() {

		TcpClient.create(host, port)
			.start(adapter);

//		TcpClient.create(host, port)
//			.newHandler((in, out) -> {
//
//				in.context().addHandlerLast(new LspJsonRpcDecoder());
//				out.context().addHandlerLast(new LspJsonRpcEncoder());
//
//				in.receiveObject()
//					.log()
//					.ofType(String.class)
//					.subscribe(responses);
//
//				requests.doOnNext(bb -> {
//					out.sendObject(bb).then().subscribe();
//				}).subscribe();
//
//				return out.neverComplete();
//			})
//			.block();

	}

	@Override
	public RequestSpec request() {
		return new DefaultRequestSpec(new DefaultExchangeFunction(requests, responses));
	}

	private static class DefaultRequestSpec implements RequestSpec {

		private Integer id;
		private String method;
		private Object params;
		private ExchangeFunction exchangeFunction;

		public DefaultRequestSpec(ExchangeFunction exchangeFunction) {
			this.exchangeFunction = exchangeFunction;
		}

		@Override
		public RequestSpec id(Integer id) {
			this.id = id;
			return this;
		}

		@Override
		public RequestSpec method(String method) {
			this.method = method;
			return this;
		}

		@Override
		public RequestSpec params(Object params) {
			this.params = params;
			return this;
		}

		@Override
		public Mono<JsonRpcResponse> exchange() {

			JsonRpcRequest request = new DefaultJsonRpcRequest(id, method, params);

			return exchangeFunction.exchange(request);
		}

	}

	private class DefaultExchangeFunction implements ExchangeFunction {

		Subscriber<ByteBuf> requests;
		Publisher<String> responses;
		ObjectMapper mapper;

		public DefaultExchangeFunction(Subscriber<ByteBuf> requests, Publisher<String> responses) {
			this.requests = requests;
			this.responses = responses;

			SimpleModule module = new SimpleModule();
			module.addDeserializer(DefaultJsonRpcResponse.class, new DefaultJsonRpcResponseJsonDeserializer());
			mapper = new ObjectMapper();
			mapper.registerModule(module);
		}

		@Override
		public Mono<JsonRpcResponse> exchange(JsonRpcRequest request) {

			return Mono.defer(() -> {
					String r = null;
					try {
						r = mapper.writeValueAsString(request);
					} catch (JsonProcessingException e) {
						log.error("Mapper error", e);
						throw new RuntimeException(e);
					}
//					requests.onNext(Unpooled.copiedBuffer(r.getBytes()));
					adapter.requests.onNext(Unpooled.copiedBuffer(r.getBytes()));
					return Mono.empty();
				})
				.then(Mono.from(Flux.from(adapter.responses).filter(r -> {
					return ObjectUtils.nullSafeEquals(r.getId(), request.getId());
				})));
//				.then(Mono.from(Flux.from(responses).map(r -> {
//						try {
//							return mapper.readValue(r, DefaultJsonRpcResponse.class);
//						} catch (Exception e) {
//							log.error("Mapper error", e);
//							throw new RuntimeException(e);
//						}
//					})
//					.filter(r -> {
//						return ObjectUtils.nullSafeEquals(request.getId(), r.getId());
//					})
//				));

		}

	}

	private static class DefaultJsonRpcResponseJsonDeserializer extends JsonDeserializer<DefaultJsonRpcResponse> {

		@Override
		public DefaultJsonRpcResponse deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			DefaultJsonRpcResponse response = new DefaultJsonRpcResponse();
			JsonNode node = p.getCodec().readTree(p);
			JsonNode jsonrpcNode = node.get("jsonrpc");
			response.setJsonrpc(jsonrpcNode.asText());
			JsonNode idNode = node.get("id");
			response.setId(idNode.asInt());
			JsonNode resultsNode = node.get("result");
			if (resultsNode != null) {
				response.setResult(resultsNode.asText());
			}
			JsonNode errorNode = node.get("error");
			if (errorNode != null) {
				response.setError(errorNode.asText());
			}
			return response;
		}
	}
}
