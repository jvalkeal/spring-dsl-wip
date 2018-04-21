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
package org.springframework.dsl.lsp4j.result.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.dsl.lsp.LspInputMessage;
import org.springframework.dsl.lsp.domain.CompletionParams;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.TextDocumentPositionParams;
import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.dsl.lsp.server.result.method.LspHandlerMethodArgumentResolver;
import org.springframework.util.Assert;

import reactor.core.publisher.Mono;

/**
 * A {@link LspHandlerMethodArgumentResolver} implementation resolving
 * {@code LSP} domain objects based on a {@code body} in a
 * {@link LspInputMessage}. Dispatches conversion of an argument type to
 * {@link ConversionService}.
 *
 * @author Janne Valkealahti
 *
 */
public class Lsp4jDomainArgumentResolver implements LspHandlerMethodArgumentResolver {

	private final ConversionService conversionService;

	public Lsp4jDomainArgumentResolver(ConversionService conversionService) {
		Assert.notNull(conversionService, "'conversionService' must be set");
		this.conversionService = conversionService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		return InitializeParams.class.isAssignableFrom(type)
				|| DidChangeTextDocumentParams.class.isAssignableFrom(type)
				|| DidCloseTextDocumentParams.class.isAssignableFrom(type)
				|| DidOpenTextDocumentParams.class.isAssignableFrom(type)
				|| DidSaveTextDocumentParams.class.isAssignableFrom(type)
				|| CompletionParams.class.isAssignableFrom(type)
				|| TextDocumentPositionParams.class.isAssignableFrom(type)
				;
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, ServerLspExchange exchange) {
		Object body = null;
		if (exchange.getRequest() != null) {
			body = exchange.getRequest().getBody();
		}
		Class<?> clazz = parameter.getParameterType();
		return Mono.just(conversionService.convert(body, clazz));
	}
}
