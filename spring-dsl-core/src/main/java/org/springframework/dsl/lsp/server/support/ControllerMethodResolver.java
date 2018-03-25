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

import java.util.List;

import org.springframework.dsl.lsp.server.HandlerMethod;
import org.springframework.dsl.lsp.server.result.method.InvocableHandlerMethod;
import org.springframework.dsl.lsp.server.result.method.LspHandlerMethodArgumentResolver;

// TODO: Auto-generated Javadoc
/**
 * The Class ControllerMethodResolver.
 */
public class ControllerMethodResolver {

	/** The request mapping resolvers. */
	private final List<LspHandlerMethodArgumentResolver> requestMappingResolvers;

	/**
	 * Instantiates a new controller method resolver.
	 *
	 * @param requestMappingResolvers the request mapping resolvers
	 */
	public ControllerMethodResolver(List<LspHandlerMethodArgumentResolver> requestMappingResolvers) {
		this.requestMappingResolvers = requestMappingResolvers;
	}

	/**
	 * Gets the request mapping method.
	 *
	 * @param handlerMethod the handler method
	 * @return the request mapping method
	 */
	public InvocableHandlerMethod getRequestMappingMethod(HandlerMethod handlerMethod) {
		InvocableHandlerMethod invocable = new InvocableHandlerMethod(handlerMethod);
		invocable.setArgumentResolvers(this.requestMappingResolvers);
		return invocable;
	}

}
