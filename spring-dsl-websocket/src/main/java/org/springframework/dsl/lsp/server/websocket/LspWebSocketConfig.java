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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dsl.lsp.server.config.GenericLspConfiguration;
import org.springframework.dsl.lsp.server.jsonrpc.RpcHandler;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

/**
 * Generic {@code LSP} configuration for a websocket integration.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
//@EnableWebSocket
@Import(GenericLspConfiguration.class)
public class LspWebSocketConfig/* implements WebSocketConfigurer*/ {

	private static final Logger log = LoggerFactory.getLogger(LspWebSocketConfig.class);

	@Bean
	public HandlerMapping handlerMapping(RpcHandler rpcHandler) {
		log.info("XXX init vs handler");
		Map<String, WebSocketHandler> map = new HashMap<>();
		map.put("/ws", new LspWebSocketHandler(rpcHandler));

		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setUrlMap(map);
		mapping.setOrder(Ordered.HIGHEST_PRECEDENCE); // before annotated controllers
		return mapping;
	}

	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

//	private final WebSocketHandler webSocketHandler;
//
//	public LspWebSocketConfig(WebSocketHandler webSocketHandler) {
//		this.webSocketHandler = webSocketHandler;
//	}
//
//	@Override
//	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//		registry.addHandler(webSocketHandler, "/ws");
//	}
}
