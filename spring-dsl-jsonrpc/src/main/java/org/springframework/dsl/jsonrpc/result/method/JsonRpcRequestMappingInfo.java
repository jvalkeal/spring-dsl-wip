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
package org.springframework.dsl.jsonrpc.result.method;

import org.springframework.dsl.jsonrpc.ServerJsonRpcExchange;
import org.springframework.dsl.jsonrpc.result.condition.JsonRcpRequestMethodsRequestCondition;
import org.springframework.dsl.jsonrpc.result.condition.JsonRpcRequestCondition;

public class JsonRpcRequestMappingInfo implements JsonRpcRequestCondition<JsonRpcRequestMappingInfo> {

	private final JsonRcpRequestMethodsRequestCondition methodsCondition;

	/**
	 * Instantiates a new json rpc request mapping info.
	 *
	 * @param methodsCondition the methods condition
	 */
	public JsonRpcRequestMappingInfo(JsonRcpRequestMethodsRequestCondition methodsCondition) {
		this.methodsCondition = methodsCondition;
	}

	@Override
	public JsonRpcRequestMappingInfo combine(JsonRpcRequestMappingInfo other) {
		JsonRcpRequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
		return new JsonRpcRequestMappingInfo(methods);
	}

	@Override
	public JsonRpcRequestMappingInfo getMatchingCondition(ServerJsonRpcExchange exchange) {
		JsonRcpRequestMethodsRequestCondition methods = methodsCondition.getMatchingCondition(exchange);
		if (methods == null) {
			return null;
		}
		return new JsonRpcRequestMappingInfo(methods);
	}

	@Override
	public int compareTo(JsonRpcRequestMappingInfo other, ServerJsonRpcExchange exchange) {
		return 0;
	}

	public static Builder builder() {
		return new DefaultBuilder();
	}

	/**
	 * Defines a builder for creating a {@link JsonRpcRequestMappingInfo}.
	 */
	public interface Builder {

		/**
		 * Set the json rpc request method conditions.
		 *
		 * @param methods the methods
		 * @return the builder for chaining
		 */
		Builder methods(String... methods);

		/**
		 * Builds the {@link JsonRpcRequestMappingInfo}.
		 *
		 * @return the json rpc request mapping info
		 */
		JsonRpcRequestMappingInfo build();
	}

	private static class DefaultBuilder implements Builder {

		private String[] methods;

		@Override
		public Builder methods(String... methods) {
			this.methods = methods;
			return this;
		}

		@Override
		public JsonRpcRequestMappingInfo build() {
			return new JsonRpcRequestMappingInfo(new JsonRcpRequestMethodsRequestCondition(methods));
		}
	}
}
