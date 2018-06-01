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
package org.springframework.dsl.jsonrpc.result.condition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dsl.jsonrpc.ServerJsonRpcExchange;

public class JsonRcpRequestMethodsRequestCondition
		extends AbstractRequestCondition<JsonRcpRequestMethodsRequestCondition> {

	private final Set<String> methods;

	public JsonRcpRequestMethodsRequestCondition(String... requestMethods) {
		this(asList(requestMethods));
	}

	public JsonRcpRequestMethodsRequestCondition(Collection<String> requestMethods) {
		this.methods = Collections.unmodifiableSet(new LinkedHashSet<>(requestMethods));
	}

	@Override
	public JsonRcpRequestMethodsRequestCondition combine(JsonRcpRequestMethodsRequestCondition other) {
		Set<String> set = new LinkedHashSet<>(this.methods);
		set.addAll(other.methods);
		return new JsonRcpRequestMethodsRequestCondition(set);
	}

	@Override
	public JsonRcpRequestMethodsRequestCondition getMatchingCondition(ServerJsonRpcExchange exchange) {
		if (getMethods().isEmpty()) {
			return this;
		}
//		return matchRequestMethod(exchange.getRequest().getMethod());

		return null;
	}

	@Override
	public int compareTo(JsonRcpRequestMethodsRequestCondition other, ServerJsonRpcExchange exchange) {
		return (other.methods.size() - this.methods.size());
	}

	@Override
	protected Collection<String> getContent() {
		return this.methods;
	}

	@Override
	protected String getToStringInfix() {
		return null;
	}

	public Set<String> getMethods() {
		return methods;
	}

	private static List<String> asList(String... requestMethods) {
		return (requestMethods != null ? Arrays.asList(requestMethods) : Collections.emptyList());
	}
}
