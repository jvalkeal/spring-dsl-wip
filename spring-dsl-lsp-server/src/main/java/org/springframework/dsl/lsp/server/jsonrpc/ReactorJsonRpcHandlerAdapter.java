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
package org.springframework.dsl.lsp.server.jsonrpc;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.dsl.jsonrpc.JsonRpcInputMessage;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcRequest;
import org.springframework.messaging.Message;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;
import reactor.ipc.netty.NettyPipeline;

public class ReactorJsonRpcHandlerAdapter implements BiFunction<NettyInbound, NettyOutbound, Mono<Void>> {

	private static final Logger log = LoggerFactory.getLogger(ReactorJsonRpcHandlerAdapter.class);
	private final RpcHandler rpcHandler;

	public ReactorJsonRpcHandlerAdapter(RpcHandler rpcHandler) {
		this.rpcHandler = rpcHandler;
	}

	@Override
	public Mono<Void> apply(NettyInbound in, NettyOutbound out) {
		ObjectMapper mapper = new ObjectMapper();
		NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(out.alloc());

		Function<String, DefaultJsonRpcRequest> jsonDecoder = s -> {
			try {
				return mapper.readValue(s, DefaultJsonRpcRequest.class);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		};

		in.context().addHandlerLast(new LspJsonRpcDecoder());

		in.receiveObject()
			.ofType(String.class)
			.map(jsonDecoder)
			.subscribe(bb -> {
				log.info("Receive request {}", bb);

				JsonRpcInputMessage i = new JsonRpcInputMessage() {

					@Override
					public String getJsonrpc() {
						return bb.getJsonrpc();
					}

					@Override
					public Integer getId() {
						return bb.getId();
					}

					@Override
					public Flux<DataBuffer> getBody() {
						return null;
					}

					@Override
					public String getMethod() {
						return bb.getMethod();
					}

					@Override
					public Object getParams() {
						return bb.getParams();
					}
				};

				JsonRpcInputMessage adaptedRequest = new ReactorJsonRpcInputMessage(in, bufferFactory);
				JsonRpcOutputMessage adaptedResponse = new ReactorJsonRpcOutputMessage(out, bufferFactory);

				rpcHandler.handle(i, adaptedResponse)
						.doOnError(ex -> log.error("Handling completed with error", ex))
						.doOnSuccess(aVoid -> log.debug("Handling completed with success"))
						.subscribe();

			});


		return out.options(NettyPipeline.SendOptions::flushOnEach)
				.neverComplete();
	}

}
