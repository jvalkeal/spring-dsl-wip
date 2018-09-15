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

import java.util.function.BiFunction;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.jsonrpc.JsonRpcRequest;
import org.springframework.dsl.jsonrpc.JsonRpcResponse;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcRequest;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcResponse;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcResponseJsonDeserializer;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;
import reactor.ipc.netty.tcp.BlockingNettyContext;
import reactor.ipc.netty.tcp.TcpClient;

/**
 * {@link LspClient} connecting to a known {@code Language Server} address.
 *
 * @author Janne Valkealahti
 *
 */
public class NettyTcpClientLspClient implements LspClient {

	private static final Logger log = LoggerFactory.getLogger(NettyTcpClientLspClient.class);
	private final String host;
	private final Integer port;
	private BlockingNettyContext blockingNettyContext;
	private BiFunction<NettyInbound, NettyOutbound, Mono<Void>> function;
	private Processor<ByteBuf, JsonRpcResponse> processor;

	/**
	 * Instantiates a new netty tcp client lsp client.
	 *
	 * @param host the host
	 * @param port the port
	 * @param function the function
	 * @param processor the processor
	 */
	public NettyTcpClientLspClient(String host, int port, BiFunction<NettyInbound, NettyOutbound, Mono<Void>> function,
			Processor<ByteBuf, JsonRpcResponse> processor) {
		this.host = host;
		this.port = port;
		this.function = function;
		this.processor = processor;
	}

	@Override
	public void start() {
		if (blockingNettyContext == null) {
			init();
		}
	}

	@Override
	public void stop() {
		if (blockingNettyContext != null) {
			blockingNettyContext.shutdown();
		}
		blockingNettyContext = null;
	}

	@Override
	public RequestSpec request() {
		return new DefaultRequestSpec(new DefaultExchangeFunction(processor, processor));
	}

	private void init() {
		blockingNettyContext = TcpClient.create(host, port).start(function);
	}

	private static class DefaultRequestSpec implements RequestSpec {

		private String id;
		private String method;
		private Object params;
		private ExchangeFunction exchangeFunction;

		public DefaultRequestSpec(ExchangeFunction exchangeFunction) {
			this.exchangeFunction = exchangeFunction;
		}

		@Override
		public RequestSpec id(String id) {
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

		final Subscriber<ByteBuf> requests;
		final Publisher<JsonRpcResponse> responses;
		ObjectMapper mapper;

		public DefaultExchangeFunction(Subscriber<ByteBuf> requests, Publisher<JsonRpcResponse> responses) {
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
					requests.onNext(Unpooled.copiedBuffer(r.getBytes()));
					return Mono.empty();
				})
				.then(Mono.from(Flux.from(responses).filter(r -> {
					return ObjectUtils.nullSafeEquals(r.getId(), request.getId());
				})));
		}
	}
}
