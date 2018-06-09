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
package org.springframework.dsl.lsp.server.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.dsl.jsonrpc.ServerJsonRpcExchange;
import org.springframework.dsl.jsonrpc.result.method.JsonRpcHandlerMethodArgumentResolver;
import org.springframework.dsl.lsp.domain.CompletionParams;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializedParams;
import org.springframework.dsl.lsp.domain.TextDocumentPositionParams;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import reactor.core.publisher.Mono;

/**
 * A {@link JsonRpcHandlerMethodArgumentResolver} implementation resolving
 * {@code LSP} domain objects based on a {@code params} in a
 * {@code message}. Dispatches conversion of an argument type to
 * {@link ConversionService}.
 *
 * @author Janne Valkealahti
 *
 */
public class LspDomainArgumentResolver implements JsonRpcHandlerMethodArgumentResolver {

	private final ConversionService conversionService;

	/**
	 * Instantiates a new lsp domain argument resolver.
	 *
	 * @param conversionService the conversion service
	 */
	public LspDomainArgumentResolver(ConversionService conversionService) {
		Assert.notNull(conversionService, "'conversionService' must be set");
		this.conversionService = conversionService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		return InitializeParams.class.isAssignableFrom(type)
				|| InitializedParams.class.isAssignableFrom(type)
				|| DidChangeTextDocumentParams.class.isAssignableFrom(type)
				|| DidCloseTextDocumentParams.class.isAssignableFrom(type)
				|| DidOpenTextDocumentParams.class.isAssignableFrom(type)
				|| DidSaveTextDocumentParams.class.isAssignableFrom(type)
				|| CompletionParams.class.isAssignableFrom(type)
				|| TextDocumentPositionParams.class.isAssignableFrom(type)
				;
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, ServerJsonRpcExchange exchange) {
		Class<?> paramType = parameter.getParameterType();

		return Mono.just(BeanUtils.instantiateClass(paramType));
//		Object body = null;
//		if (exchange.getRequest() != null) {
//		}
//		Class<?> clazz = parameter.getParameterType();
//		return Mono.just(conversionService.convert(body, clazz));
	}
}
