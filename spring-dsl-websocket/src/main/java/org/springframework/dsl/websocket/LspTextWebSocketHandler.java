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
package org.springframework.dsl.websocket;

import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import reactor.core.publisher.Mono;

/**
 *
 *
 * @author Janne Valkealahti
 *
 */
public class LspTextWebSocketHandler extends TextWebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(LspTextWebSocketHandler.class);
	private final Function<byte[], Mono<String>> consumer;

	public LspTextWebSocketHandler(Function<byte[], Mono<String>> consumer) {
		this.consumer = consumer;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.debug("afterConnectionEstablished {}", session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		log.debug("handleTextMessage {} {}", session.getId(), message.getPayload());

		byte[] payload = message.getPayload().getBytes();

		Mono<String> response = consumer.apply(payload);
		session.sendMessage(new TextMessage(response.block().getBytes()));

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.debug("afterConnectionClosed {} {}", session.getId(), status);
	}
}
