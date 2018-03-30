package org.springframework.dsl.lsp4j.result.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.dsl.lsp.server.result.method.LspHandlerMethodArgumentResolver;
import org.springframework.util.ClassUtils;

import reactor.core.publisher.Mono;

public class Lsp4jDomainArgumentResolver implements LspHandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		return InitializeParams.class.isAssignableFrom(type);
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, ServerLspExchange exchange) {
		if (ClassUtils.isAssignable(org.eclipse.lsp4j.InitializeParams.class, exchange.getRequest().getBody().getClass())) {
			InitializeParams params = new InitializeParams();
			return Mono.just(params);
		} else {
			return Mono.empty();
		}
	}

}
