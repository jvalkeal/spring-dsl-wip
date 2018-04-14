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

import org.springframework.lang.Nullable;

// TODO: Auto-generated Javadoc
/**
 * The Interface NameValueExpression.
 *
 * @param <T> the generic type
 */
public interface NameValueExpression<T> {

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	@Nullable
	T getValue();

	/**
	 * Checks if is negated.
	 *
	 * @return true, if is negated
	 */
	boolean isNegated();

}
