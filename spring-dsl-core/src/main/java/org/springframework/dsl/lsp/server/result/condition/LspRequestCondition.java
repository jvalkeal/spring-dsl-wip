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
package org.springframework.dsl.lsp.server.result.condition;

import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.lang.Nullable;

// TODO: Auto-generated Javadoc
/**
 * Contract for request mapping conditions.
 * 
 * <p>Request conditions can be combined via {@link #combine(Object)}, matched to
 * a request via {@link #getMatchingCondition(ServerLspExchange)}, and compared
 * to each other via {@link #compareTo(Object, ServerLspExchange)} to determine
 * which is a closer match for a given request.
 *
 * @author Janne Valkealahti
 * @param <T> the type of objects that this RequestCondition can be combined
 * with and compared to
 */
public interface LspRequestCondition<T> {

	/**
	 * Combine this condition with another such as conditions from a
	 * type-level and method-level {@code @RequestMapping} annotation.
	 *
	 * @param other the condition to combine with.
	 * @return a request condition instance that is the result of combining
	 * the two condition instances.
	 */
	T combine(T other);

	/**
	 * Check if the condition matches the request returning a potentially new
	 * instance created for the current request. For example a condition with
	 * multiple URL patterns may return a new instance only with those patterns
	 * that match the request.
	 *
	 * @param exchange the server coap exchange
	 * @return a condition instance in case of a match or {@code null} otherwise.
	 */
	@Nullable
	T getMatchingCondition(ServerLspExchange exchange);

	/**
	 * Compare this condition to another condition in the context of
	 * a specific request. This method assumes both instances have
	 * been obtained via {@link #getMatchingCondition(ServerLspExchange)}
	 * to ensure they have content relevant to current request only.
	 *
	 * @param other the other to compare
	 * @param exchange the server coap exchange
	 * @return a combare to other
	 */
	int compareTo(T other, ServerLspExchange exchange);
}
