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
package org.springframework.dsl.lsp.server.result.method.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.dsl.lsp.LspClient;
import org.springframework.dsl.lsp.LspClientContext;
import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.dsl.lsp.server.result.method.LspHandlerMethodArgumentResolver;

import reactor.core.publisher.Mono;

/**
 * Implementation of a {@link LspHandlerMethodArgumentResolver} working with a
 * {@link ServerLspExchange}. This implementation is able to resolve various
 * parameters available directly from {@link ServerLspExchange}.
 *
 * @author Janne Valkealahti
 *
 */
public class ServerLspExchangeArgumentResolver implements LspHandlerMethodArgumentResolver {

	private static final Logger log = LoggerFactory.getLogger(ServerLspExchangeArgumentResolver.class);

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		return ServerLspExchange.class.isAssignableFrom(type) || LspClientContext.class.isAssignableFrom(type);
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, ServerLspExchange exchange) {
		Class<?> paramType = parameter.getParameterType();
		if (ServerLspExchange.class.isAssignableFrom(paramType)) {
			return Mono.just(exchange);
		} else if (LspClientContext.class.isAssignableFrom(paramType)){
			return Mono.just(new LspClientContext() {

				@Override
				public LspClient getClient() {
					return new LspClient() {

						@Override
						public Mono<Void> send(Object message) {
							log.info("XXXX client send {}", message);
							return Mono.empty();
						}
					};
				}
			});
		} else {
			// should never happen
			throw new IllegalArgumentException(
					"Unknown parameter type: " + paramType + " in method: " + parameter.getMethod());
		}
	}
}
