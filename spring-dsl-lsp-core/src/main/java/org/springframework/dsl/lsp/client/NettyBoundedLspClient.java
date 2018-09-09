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

import java.nio.charset.Charset;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.dsl.jsonrpc.JsonRpcResponse;
import org.springframework.dsl.lsp.server.jsonrpc.ReactorJsonRpcOutputMessage;
import org.springframework.util.ObjectUtils;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyOutbound;

/**
 * {@link LspClient} bound to existing netty channels.
 *
 * @author Janne Valkealahti
 *
 */
public class NettyBoundedLspClient implements LspClient {

	private NettyOutbound out;
	private EmitterProcessor<JsonRpcResponse> responses = EmitterProcessor.create();

	public NettyBoundedLspClient(NettyOutbound out) {
		this.out = out;
	}

	@Override
	public void start() {
		// no-op
	}

	@Override
	public void stop() {
		// no-op
	}

	@Override
	public RequestSpec request() {
		return new DefaultRequestSpec();
	}

	public EmitterProcessor<JsonRpcResponse> getResponses() {
		return responses;
	}

	private class DefaultRequestSpec implements RequestSpec {

		private Integer id;
		private String method;
		private Object params;

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
			NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(out.alloc());

			JsonRpcOutputMessage adaptedResponse = new ReactorJsonRpcOutputMessage(out, bufferFactory);

			System.out.println("XXXX2");
			String error = "{\"jsonrpc\":\"2.0\", \"id\":" + id + ", \"method\":\"" + method + "\"}";
			DataBuffer buffer = bufferFactory.wrap(error.getBytes(Charset.defaultCharset()));
			Flux<DataBuffer> body = Flux.just(buffer);

			adaptedResponse.writeWith(body).subscribe();
			adaptedResponse.setComplete().subscribe();

//			DefaultJsonRpcResponse xxx = new DefaultJsonRpcResponse("2.0", 10, "clienthi", null);
//			return Mono.just(xxx);

			return Mono.from(responses)
					.doOnNext(r -> {
						System.out.println("XXXX4 " + r);
					})
					.filter(r -> ObjectUtils.nullSafeEquals(r.getId(), id));

//			return Mono.empty();
		}
	}
}
