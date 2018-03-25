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
package org.springframework.dsl.lsp.server.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.dsl.lsp.server.HandlerAdapter;
import org.springframework.dsl.lsp.server.HandlerMapping;
import org.springframework.dsl.lsp.server.HandlerResult;
import org.springframework.dsl.lsp.server.HandlerResultHandler;
import org.springframework.dsl.lsp.server.LspHandler;
import org.springframework.dsl.lsp.server.LspServerException;
import org.springframework.dsl.lsp.server.ServerLspExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// TODO: Auto-generated Javadoc
/**
 * Central dispatcher for COAP request handlers/controllers. Dispatches to registered
 * handlers for processing a coap request, providing convenient mapping facilities.
 *
 * @author Janne Valkealahti
 *
 */
public class DispatcherHandler implements LspHandler, ApplicationContextAware {

	/** The Constant HANDLER_NOT_FOUND_EXCEPTION. */
	private static final Exception HANDLER_NOT_FOUND_EXCEPTION =
			new LspServerException("No matching handler");
	
	/** The handler mappings. */
	private List<HandlerMapping> handlerMappings;
	
	/** The handler adapters. */
	private List<HandlerAdapter> handlerAdapters;
	
	/** The result handlers. */
	private List<HandlerResultHandler> resultHandlers;

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		initStrategies(applicationContext);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.LspHandler#handle(org.springframework.dsl.lsp.server.ServerLspExchange)
	 */
	@Override
	public Mono<Void> handle(ServerLspExchange exchange) {
		return Flux.fromIterable(this.handlerMappings)
				.concatMap(mapping -> mapping.getHandler(exchange))
				.next()
				.switchIfEmpty(Mono.error(HANDLER_NOT_FOUND_EXCEPTION))
				.flatMap(handler -> invokeHandler(exchange, handler))
				.flatMap(result -> handleResult(exchange, result));
	}

	/**
	 * Inits the strategies.
	 *
	 * @param context the context
	 */
	protected void initStrategies(ApplicationContext context) {
		Map<String, HandlerMapping> mappingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				context, HandlerMapping.class, true, false);

		this.handlerMappings = new ArrayList<>(mappingBeans.values());
		AnnotationAwareOrderComparator.sort(this.handlerMappings);

		Map<String, HandlerAdapter> adapterBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				context, HandlerAdapter.class, true, false);

		this.handlerAdapters = new ArrayList<>(adapterBeans.values());
		AnnotationAwareOrderComparator.sort(this.handlerAdapters);

		Map<String, HandlerResultHandler> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				context, HandlerResultHandler.class, true, false);

		this.resultHandlers = new ArrayList<>(beans.values());
		AnnotationAwareOrderComparator.sort(this.resultHandlers);
	}

	/**
	 * Invoke handler.
	 *
	 * @param exchange the exchange
	 * @param handler the handler
	 * @return the mono
	 */
	private Mono<HandlerResult> invokeHandler(ServerLspExchange exchange, Object handler) {
		for (HandlerAdapter handlerAdapter : this.handlerAdapters) {
			if (handlerAdapter.supports(handler)) {
				return handlerAdapter.handle(exchange, handler);
			}
		}
		return Mono.error(new IllegalStateException("No HandlerAdapter: " + handler));
	}

	/**
	 * Handle result.
	 *
	 * @param exchange the exchange
	 * @param result the result
	 * @return the mono
	 */
	private Mono<Void> handleResult(ServerLspExchange exchange, HandlerResult result) {
		return getResultHandler(result)
				.handleResult(exchange, result)
				.onErrorResume(ex -> result.applyExceptionHandler(ex)
				.flatMap(exceptionResult -> getResultHandler(exceptionResult)
				.handleResult(exchange, exceptionResult)));
	}

	/**
	 * Gets the result handler.
	 *
	 * @param handlerResult the handler result
	 * @return the result handler
	 */
	private HandlerResultHandler getResultHandler(HandlerResult handlerResult) {
		for (HandlerResultHandler resultHandler : this.resultHandlers) {
			if (resultHandler.supports(handlerResult)) {
				return resultHandler;
			}
		}
		throw new IllegalStateException("No HandlerResultHandler for " + handlerResult.getReturnValue());
	}
}
