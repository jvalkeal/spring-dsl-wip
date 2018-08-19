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
package org.springframework.dsl.lsp.server.websocket;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.dsl.jsonrpc.JsonRpcInputMessage;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.dsl.jsonrpc.support.AbstractJsonRpcOutputMessage;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcRequest;
import org.springframework.dsl.lsp.server.jsonrpc.RpcHandler;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

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

public class LspWebSocketHandler implements WebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(LspWebSocketHandler.class);
	private final RpcHandler rpcHandler;

	public LspWebSocketHandler(RpcHandler rpcHandler) {
		this.rpcHandler = rpcHandler;
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(DefaultJsonRpcRequest.class, new DefaultJsonRpcRequestDeserializer());
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);

		Function<String, DefaultJsonRpcRequest> jsonDecoder = s -> {
			try {
				return mapper.readValue(s, DefaultJsonRpcRequest.class);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		};

		return session.receive()
			.map(message -> {
				String payload = message.getPayloadAsText();
				log.debug("Message {}", message);
				log.debug("Message payload {}", payload);
				String split[] = payload.split("\\r?\\n", 2);
				if (split.length == 1) {
					return split[0];
				} else {
					return split[1];
				}

			})
			.map(jsonDecoder)
			.doOnNext(bb -> {

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
						return Mono.justOrEmpty(bb.getParams().toString());
					}

					@Override
					public Mono<String> getSessionId() {
						return Mono.fromSupplier(() -> session.getId());

					}
				};

//				JsonRpcInputMessage adaptedRequest = null;
				JsonRpcOutputMessage adaptedResponse = new WebSocketJsonRpcOutputMessage(session, session.bufferFactory());

				rpcHandler.handle(i, adaptedResponse)
					.doOnError(ex -> {
						log.error("Handling completed with error", ex);
						String error = "{\"jsonrpc\":\"2.0\", \"id\":" + bb.getId() + ", \"error\":{\"code\":-32603, \"message\": \"internal server error\"}}";
						DataBuffer buffer = session.bufferFactory().wrap(error.getBytes(Charset.defaultCharset()));
						Flux<DataBuffer> body = Flux.just(buffer);
						adaptedResponse.writeWith(body).subscribe();
						adaptedResponse.setComplete().subscribe();
					})
					.doOnSuccess(aVoid -> log.debug("Handling completed with success"))
					.subscribe();
			})
			.then();

	}

	private static class WebSocketJsonRpcOutputMessage extends AbstractJsonRpcOutputMessage {

		private WebSocketSession session;

		public WebSocketJsonRpcOutputMessage(WebSocketSession session, DataBufferFactory dataBufferFactory) {
			super(dataBufferFactory);
			this.session = session;
		}

		@Override
		protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body) {

			Flux<WebSocketMessage> messages = Flux.from(body)
				.map(bodyBuffer -> {
//					String headers = "Content-Length: " + bodyBuffer.readableByteCount() + "\r\n\r\n";
//					DataBuffer headersBuffer = bufferFactory().wrap(ByteBuffer.wrap(headers.getBytes()));
//					DataBuffer buffer = bufferFactory().join(Arrays.asList(headersBuffer, bodyBuffer));
//					return new WebSocketMessage(WebSocketMessage.Type.TEXT, buffer);
					return new WebSocketMessage(WebSocketMessage.Type.TEXT, bodyBuffer);
				});
			return session.send(messages);
		}

		@Override
		protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body) {
			return null;
		}
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
