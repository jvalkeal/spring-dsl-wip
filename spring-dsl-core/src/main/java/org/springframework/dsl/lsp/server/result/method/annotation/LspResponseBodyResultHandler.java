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

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dsl.lsp.annotation.LspResponseBody;
import org.springframework.dsl.lsp.server.HandlerResult;
import org.springframework.dsl.lsp.server.HandlerResultHandler;
import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.dsl.lsp.server.ServerLspResponse;

import reactor.core.publisher.Mono;

// TODO: Auto-generated Javadoc
/**
 * {@code HandlerResultHandler} that handles return values from methods annotated
 * with {@code @CoapResponseBody} writing to the body of the request or response.
 *
 * @author Janne Valkealahti
 *
 */
public class LspResponseBodyResultHandler implements HandlerResultHandler {

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.HandlerResultHandler#supports(org.springframework.dsl.lsp.server.HandlerResult)
	 */
	@Override
	public boolean supports(HandlerResult result) {
		MethodParameter parameter = result.getReturnTypeSource();
		Class<?> containingClass = parameter.getContainingClass();
		return (AnnotationUtils.findAnnotation(containingClass, LspResponseBody.class) != null ||
				parameter.getMethodAnnotation(LspResponseBody.class) != null);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.HandlerResultHandler#handleResult(org.springframework.dsl.lsp.server.ServerLspExchange, org.springframework.dsl.lsp.server.HandlerResult)
	 */
	@Override
	public Mono<Void> handleResult(ServerLspExchange exchange, HandlerResult result) {
		ServerLspResponse response = exchange.getResponse();
		response.setBody(((String)result.getReturnValue()).getBytes());
		return Mono.empty();
	}
}
