/*
 * Copyright 2017 the original author or authors.
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
package org.springframework.statemachine.dsl.lsp.server.support;

import java.util.Arrays;

import org.springframework.statemachine.dsl.lsp.LspMethod;
import org.springframework.statemachine.dsl.lsp.server.ServerLspRequest;

public class GenericServerLspRequest implements ServerLspRequest {

	private final Object body;
	private LspMethod method;
//	private int contentFormat = -1;
//	private RequestPath path;

	public GenericServerLspRequest(Object body) {
		this.body = body;
	}

	@Override
	public Object getBody() {
		return body;
	}

//	@Override
//	public int getContentFormat() {
//		return contentFormat;
//	}
//
//	public void setContentFormat(int contentFormat) {
//		this.contentFormat = contentFormat;
//	}
//
//	@Override
//	public RequestPath getPath() {
//		return path;
//	}
//
//	public void setPath(RequestPath path) {
//		this.path = path;
//	}

	public void setMethod(LspMethod method) {
		this.method = method;
	}

	@Override
	public LspMethod getMethod() {
		return method;
	}
}
