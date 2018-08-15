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

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.jsonrpc.JsonRpcRequest;
import org.springframework.dsl.jsonrpc.JsonRpcResponse;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcRequest;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcResponse;
import org.springframework.dsl.lsp.server.jsonrpc.LspJsonRpcDecoder;
import org.springframework.dsl.lsp.server.jsonrpc.LspJsonRpcEncoder;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.tcp.TcpClient;

public class NettyTcpClientLspClient implements LspClient {

	private static final Logger log = LoggerFactory.getLogger(NettyTcpClientLspClient.class);

	private EmitterProcessor<ByteBuf> requests = EmitterProcessor.create();
	private EmitterProcessor<String> responses = EmitterProcessor.create();
	private final String host;
	private final Integer port;

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
			.newHandler((in, out) -> {

				in.context().addHandlerLast(new LspJsonRpcDecoder());
				out.context().addHandlerLast(new LspJsonRpcEncoder());

				in.receiveObject()
					.log()
					.ofType(String.class)
					.subscribe(responses);

				requests.doOnNext(bb -> {
					out.sendObject(bb).then().subscribe();
				}).subscribe();

				return out.neverComplete();
			})
			.block();

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

	private static class DefaultExchangeFunction implements ExchangeFunction {

		Subscriber<ByteBuf> requests;
		Publisher<String> responses;
		ObjectMapper mapper = new ObjectMapper();

		public DefaultExchangeFunction(Subscriber<ByteBuf> requests, Publisher<String> responses) {
			this.requests = requests;
			this.responses = responses;
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
					requests.onNext(Unpooled.copiedBuffer(r.getBytes()));
					return Mono.empty();
				})
				.then(Mono.from(Flux.from(responses).map(r -> {
						try {
							return mapper.readValue(r, DefaultJsonRpcResponse.class);
						} catch (Exception e) {
							log.error("Mapper error", e);
							throw new RuntimeException(e);
						}
					})
					.filter(r -> {
						return ObjectUtils.nullSafeEquals(request.getId(), r.getId());
					})
				));

		}

	}

}
