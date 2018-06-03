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

/**
 * Represents a {@code JSONRPC Request Object}.
 *
 * @author Janne Valkealahti
 *
 */
public class DefaultJsonRpcRequest extends AbstractJsonRpcObject {

	private String method;
	private String params;

	/**
	 * Instantiates a new json rpc request.
	 */
	public DefaultJsonRpcRequest() {
		super();
	}

	/**
	 * Instantiates a new json rpc request.
	 *
	 * @param id the id
	 * @param method the method
	 * @param params the params
	 */
	public DefaultJsonRpcRequest(Integer id, String method, String params) {
		this(null, id, method, params);
	}

	/**
	 * Instantiates a new json rpc request.
	 *
	 * @param jsonrpc the jsonrpc
	 * @param id the id
	 * @param method the method
	 * @param params the params
	 */
	public DefaultJsonRpcRequest(String jsonrpc, Integer id, String method, String params) {
		super(jsonrpc, id);
		this.method = method;
		this.params = params;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the method.
	 *
	 * @param method the new method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Gets the params.
	 *
	 * @return the params
	 */
	public String getParams() {
		return params;
	}

	/**
	 * Sets the params.
	 *
	 * @param params the new params
	 */
	public void setParams(String params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "DefaultJsonRpcRequest [method=" + method + ", params=" + params + ", getJsonrpc()=" + getJsonrpc()
				+ ", getId()=" + getId() + "]";
	}
}
