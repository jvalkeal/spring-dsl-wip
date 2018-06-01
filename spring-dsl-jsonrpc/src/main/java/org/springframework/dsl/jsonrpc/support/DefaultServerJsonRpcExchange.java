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
package org.springframework.dsl.jsonrpc.support;

import org.springframework.dsl.jsonrpc.JsonRpcInputMessage;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.dsl.jsonrpc.ServerJsonRpcExchange;

public class DefaultServerJsonRpcExchange implements ServerJsonRpcExchange {

	private final JsonRpcInputMessage request;
	private final JsonRpcOutputMessage response;

	public DefaultServerJsonRpcExchange(JsonRpcInputMessage request, JsonRpcOutputMessage response) {
		this.request = request;
		this.response = response;
	}

	@Override
	public JsonRpcInputMessage getRequest() {
		return request;
	}

	@Override
	public JsonRpcOutputMessage getResponse() {
		return response;
	}

}
