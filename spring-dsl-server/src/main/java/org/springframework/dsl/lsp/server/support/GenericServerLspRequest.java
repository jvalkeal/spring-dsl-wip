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

import org.springframework.dsl.lsp.LspClientContext;
import org.springframework.dsl.lsp.LspMethod;
import org.springframework.dsl.lsp.server.ServerLspRequest;

/**
 * The Class GenericServerLspRequest.
 */
public class GenericServerLspRequest implements ServerLspRequest {

	/** The body. */
	private final Object body;

	/** The method. */
	private LspMethod method;

	private LspClientContext context;

	/**
	 * Instantiates a new generic server lsp request.
	 *
	 * @param body the body
	 */
	public GenericServerLspRequest(Object body) {
		this.body = body;
	}

	@Override
	public Object getBody() {
		return body;
	}

	/**
	 * Sets the method.
	 *
	 * @param method
	 *            the new method
	 */
	public void setMethod(LspMethod method) {
		this.method = method;
	}

	@Override
	public LspMethod getMethod() {
		return method;
	}

	public void setContext(LspClientContext context) {
		this.context = context;
	}

	@Override
	public LspClientContext getContext() {
		return context;
	}
}
