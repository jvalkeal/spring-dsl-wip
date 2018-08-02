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
import java.nio.charset.Charset;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.dsl.jsonrpc.JsonRpcInputMessage;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcRequest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
		SimpleModule module = new SimpleModule();
		module.addDeserializer(DefaultJsonRpcRequest.class, new DefaultJsonRpcRequestDeserializer());
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(out.alloc());

		Function<String, DefaultJsonRpcRequest> jsonDecoder = s -> {
			try {
				return mapper.readValue(s, DefaultJsonRpcRequest.class);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		};

		in.context().addHandlerLast(new LspJsonRpcDecoder());
		out.context().addHandlerLast(new LspJsonRpcEncoder());

		in.receiveObject()
			.ofType(String.class)
			.map(jsonDecoder)
			.subscribe(bb -> {
				log.info("Receive request {}", bb);

				JsonRpcInputMessage i = new JsonRpcInputMessage() {

					@Override
					public Mono<String> getJsonrpc() {
						return Mono.justOrEmpty(bb.getJsonrpc());
					}

					@Override
					public Mono<Integer> getId() {
						return Mono.justOrEmpty(bb.getId());
					}

					@Override
					public Mono<String> getMethod() {
						return Mono.justOrEmpty(bb.getMethod());
					}

					@Override
					public Mono<String> getParams() {
						return Mono.justOrEmpty(bb.getParams());
					}

					@Override
					public Mono<String> getSessionId() {
						return Mono.fromSupplier(() -> in.context().channel().id().asLongText());
					}
				};



				JsonRpcInputMessage adaptedRequest = new ReactorJsonRpcInputMessage(in, bufferFactory);
				JsonRpcOutputMessage adaptedResponse = new ReactorJsonRpcOutputMessage(out, bufferFactory);

				rpcHandler.handle(i, adaptedResponse)
						.doOnError(ex -> {
							log.error("Handling completed with error", ex);

							String error = "{\"jsonrpc\":\"2.0\", \"id\":" + bb.getId() + ", \"error\":{\"code\":-32603, \"message\": \"internal server error\"}}";

							DataBuffer buffer = bufferFactory.wrap(error.getBytes(Charset.defaultCharset()));
							Flux<DataBuffer> body = Flux.just(buffer);
							adaptedResponse.writeWith(body).subscribe();
							adaptedResponse.setComplete().subscribe();
						})
						.doOnSuccess(aVoid -> log.debug("Handling completed with success"))
						.subscribe();

			});


		return out.options(NettyPipeline.SendOptions::flushOnEach)
				.neverComplete();
	}

	private static class DefaultJsonRpcRequestDeserializer extends JsonDeserializer<DefaultJsonRpcRequest> {

		@Override
		public DefaultJsonRpcRequest deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			ObjectCodec c = p.getCodec();
		    JsonNode node = c.readTree(p);
		    String jsonrpc = node.get("jsonrpc").asText();
		    JsonNode jsonNodeParams = node.get("params");
		    String params = null;
		    if (jsonNodeParams != null) {
			    if (jsonNodeParams.isValueNode()) {
			    	params = jsonNodeParams.asText();
			    } else {
			    	params = jsonNodeParams.toString();
			    }
		    }
		    JsonNode jsonNodeId = node.get("id");
		    Integer id = jsonNodeId != null ? jsonNodeId.asInt() : null;
		    String method = node.get("method").asText();
			return new DefaultJsonRpcRequest(jsonrpc, id, method, params);
		}

	}

}
