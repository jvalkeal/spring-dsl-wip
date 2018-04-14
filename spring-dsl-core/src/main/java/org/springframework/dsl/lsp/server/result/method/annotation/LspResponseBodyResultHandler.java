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

import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dsl.lsp.annotation.LspResponseBody;
import org.springframework.dsl.lsp.server.HandlerResult;
import org.springframework.dsl.lsp.server.HandlerResultHandler;
import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.dsl.lsp.server.ServerLspResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link HandlerResultHandler} that handles return values from methods annotated
 * with {@link LspResponseBody} writing to the body of the request or response.
 *
 * @author Janne Valkealahti
 *
 */
//public class LspResponseBodyResultHandler implements HandlerResultHandler {
public class LspResponseBodyResultHandler extends AbstractMessageWriterResultHandler implements HandlerResultHandler {

	public LspResponseBodyResultHandler() {
		super(ReactiveAdapterRegistry.getSharedInstance());
	}

	@Override
	public boolean supports(HandlerResult result) {
		MethodParameter parameter = result.getReturnTypeSource();
		Class<?> containingClass = parameter.getContainingClass();
		return (AnnotationUtils.findAnnotation(containingClass, LspResponseBody.class) != null ||
				parameter.getMethodAnnotation(LspResponseBody.class) != null);
	}

	@Override
	public Mono<Void> handleResult(ServerLspExchange exchange, HandlerResult result) {
		Object body = result.getReturnValue();

		MethodParameter bodyParameter = result.getReturnTypeSource();
		ResolvableType bodyType = ResolvableType.forMethodParameter(bodyParameter);
		Class<?> bodyClass = bodyType.resolve();
		ReactiveAdapter adapter = getAdapterRegistry().getAdapter(bodyClass, body);

		Publisher<?> publisher;
		ResolvableType elementType;
		if (adapter != null) {
			publisher = adapter.toPublisher(body);
			ResolvableType genericType = bodyType.getGeneric(0);
			elementType = getElementType(adapter, genericType);
		}
		else {
			publisher = Mono.justOrEmpty(body);
			elementType = ((bodyClass == null || bodyClass.equals(Object.class)) && body != null ?
					ResolvableType.forInstance(body) : bodyType);
		}

//		if (void.class == elementType.getRawClass() || Void.class == elementType.getRawClass()) {
//			return Mono.from((Publisher<Void>) publisher);
//		}


		ServerLspResponse response = exchange.getResponse();
//		response.setBody(result.getReturnValue());
//		return Mono.empty();
		return write((Publisher<Object>) publisher, response);
	}

	private Mono<Void> write(Publisher<Object> publisher, ServerLspResponse response) {

		return Flux.from(publisher).flatMap(body -> {
			response.addBody(body);
			return Mono.empty();
		}).then();

//
//		return Mono.from(publisher).flatMap(body -> {
//			System.out.println("XXX " + body);
//			response.addBody(body);
//			return Mono.empty();
//		}).then();
	}

	private ResolvableType getElementType(ReactiveAdapter adapter, ResolvableType genericType) {
		if (adapter.isNoValue()) {
			return ResolvableType.forClass(Void.class);
		}
		else if (genericType != ResolvableType.NONE) {
			return genericType;
		}
		else {
			return ResolvableType.forClass(Object.class);
		}
	}

}
