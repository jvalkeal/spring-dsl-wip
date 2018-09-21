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

import java.nio.charset.Charset;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.dsl.jsonrpc.support.AbstractJsonRpcOutputMessage;
import org.springframework.dsl.lsp.client.LspClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebSocketBoundedLspClient implements LspClient {

	private WebSocketSession session;

	public WebSocketBoundedLspClient(WebSocketSession session) {
		this.session = session;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public RequestSpec request() {
		return null;
	}

	@Override
	public NotificationSpec notification() {
		return new DefaultNotificationSpec();
	}

	private class DefaultNotificationSpec implements NotificationSpec {

		private String method;
		private Object params;

		@Override
		public NotificationSpec method(String method) {
			this.method = method;
			return this;
		}

		@Override
		public NotificationSpec params(Object params) {
			this.params = params;
			return this;
		}

		@Override
		public Mono<Void> exchange() {
			JsonRpcOutputMessage adaptedResponse = new WebSocketJsonRpcOutputMessage(session, session.bufferFactory());
			String error = "{\"jsonrpc\":\"2.0\", \"method\":\"" + method + "\"}";
			DataBuffer buffer = session.bufferFactory().wrap(error.getBytes(Charset.defaultCharset()));
			Flux<DataBuffer> body = Flux.just(buffer);
			adaptedResponse.writeWith(body).subscribe();
			adaptedResponse.setComplete().subscribe();

			return Mono.empty();
		}

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
					return new WebSocketMessage(WebSocketMessage.Type.TEXT, bodyBuffer);
				});
			return session.send(messages);
		}

		@Override
		protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body) {
			return null;
		}
	}
}
