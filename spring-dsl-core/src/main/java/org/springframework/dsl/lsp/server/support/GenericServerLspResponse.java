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

import org.springframework.dsl.lsp.server.ServerLspResponse;

/**
 * The Class GenericServerLspResponse.
 */
public class GenericServerLspResponse implements ServerLspResponse {

	/** The request payload. */
	private List<Object> requestPayloads = new ArrayList<>();

	@Override
	public Object[] getBody() {
		return requestPayloads.toArray();
	}

	@Override
	public void addBody(Object body) {
		this.requestPayloads.add(body);
	}

}
