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
package org.springframework.dsl.jsonrpc.result.method.annotation;

import java.util.Collections;
import java.util.List;

import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.dsl.jsonrpc.JsonRpcInputMessage;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.dsl.jsonrpc.ServerJsonRpcExchange;
import org.springframework.dsl.jsonrpc.codec.JsonRpcMessageWriter;
import org.springframework.dsl.jsonrpc.result.HandlerResultHandlerSupport;
import org.springframework.lang.Nullable;

import reactor.core.publisher.Mono;

/**
 * Abstract base class for result handlers that handle return values by writing
 * to the response with {@link JsonRpcMessageWriter}.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class AbstractMessageWriterResultHandler extends HandlerResultHandlerSupport {

	private final List<JsonRpcMessageWriter<?>> messageWriters;

	protected AbstractMessageWriterResultHandler(List<JsonRpcMessageWriter<?>> messageWriters, ReactiveAdapterRegistry adapterRegistry) {
		super(adapterRegistry);
		this.messageWriters = messageWriters;
	}

	protected Mono<Void> writeBody(@Nullable Object body, MethodParameter bodyParameter,
			ServerJsonRpcExchange exchange) {
		return this.writeBody(body, bodyParameter, null, exchange);
	}

	protected Mono<Void> writeBody(@Nullable Object body, MethodParameter bodyParameter,
			@Nullable MethodParameter actualParameter, ServerJsonRpcExchange exchange) {

		ResolvableType bodyType = ResolvableType.forMethodParameter(bodyParameter);
		ResolvableType actualType = (actualParameter == null ?
				bodyType : ResolvableType.forMethodParameter(actualParameter));
		Class<?> bodyClass = bodyType.resolve();
		ReactiveAdapter adapter = getAdapterRegistry().getAdapter(bodyClass, body);

		Publisher<?> publisher;
		ResolvableType elementType;
		if (adapter != null) {
			publisher = adapter.toPublisher(body);
			ResolvableType genericType = bodyType.getGeneric(0);
			elementType = getElementType(adapter, genericType);
		} else {
			publisher = Mono.justOrEmpty(body);
			elementType = ((bodyClass == null || bodyClass.equals(Object.class)) && body != null ?
					ResolvableType.forInstance(body) : bodyType);
		}

		if (void.class == elementType.getRawClass() || Void.class == elementType.getRawClass()) {
			return Mono.from((Publisher<Void>) publisher);
		}

		JsonRpcInputMessage request = exchange.getRequest();
		JsonRpcOutputMessage response = exchange.getResponse();

		for (JsonRpcMessageWriter<?> writer : messageWriters) {
			if (writer.canWrite(elementType)) {
				return writer.write((Publisher) publisher, elementType, response, Collections.emptyMap());
			}
		}

		return Mono.error(new RuntimeException("xxx"));
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
