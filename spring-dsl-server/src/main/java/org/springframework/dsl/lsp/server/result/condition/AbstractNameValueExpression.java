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
import org.springframework.util.ObjectUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractNameValueExpression.
 *
 * @param <T> the generic type
 */
abstract class AbstractNameValueExpression<T> implements NameValueExpression<T> {

	/** The name. */
	protected final String name;

	/** The value. */
	@Nullable
	protected final T value;

	/** The is negated. */
	protected final boolean isNegated;


	/**
	 * Instantiates a new abstract name value expression.
	 *
	 * @param expression the expression
	 */
	AbstractNameValueExpression(String expression) {
		int separator = expression.indexOf('=');
		if (separator == -1) {
			this.isNegated = expression.startsWith("!");
			this.name = (this.isNegated ? expression.substring(1) : expression);
			this.value = null;
		}
		else {
			this.isNegated = (separator > 0) && (expression.charAt(separator - 1) == '!');
			this.name = (this.isNegated ? expression.substring(0, separator - 1) : expression.substring(0, separator));
			this.value = parseValue(expression.substring(separator + 1));
		}
	}


	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.result.condition.NameValueExpression#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.result.condition.NameValueExpression#getValue()
	 */
	@Override
	@Nullable
	public T getValue() {
		return this.value;
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.result.condition.NameValueExpression#isNegated()
	 */
	@Override
	public boolean isNegated() {
		return this.isNegated;
	}

	/**
	 * Match.
	 *
	 * @param exchange the exchange
	 * @return true, if successful
	 */
	public final boolean match(ServerLspExchange exchange) {
		boolean isMatch;
		if (this.value != null) {
			isMatch = matchValue(exchange);
		}
		else {
			isMatch = matchName(exchange);
		}
		return this.isNegated != isMatch;
	}


	/**
	 * Checks if is case sensitive name.
	 *
	 * @return true, if is case sensitive name
	 */
	protected abstract boolean isCaseSensitiveName();

	/**
	 * Parses the value.
	 *
	 * @param valueExpression the value expression
	 * @return the t
	 */
	protected abstract T parseValue(String valueExpression);

	/**
	 * Match name.
	 *
	 * @param exchange the exchange
	 * @return true, if successful
	 */
	protected abstract boolean matchName(ServerLspExchange exchange);

	/**
	 * Match value.
	 *
	 * @param exchange the exchange
	 * @return true, if successful
	 */
	protected abstract boolean matchValue(ServerLspExchange exchange);


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof AbstractNameValueExpression) {
			AbstractNameValueExpression<?> other = (AbstractNameValueExpression<?>) obj;
			String thisName = isCaseSensitiveName() ? this.name : this.name.toLowerCase();
			String otherName = isCaseSensitiveName() ? other.name : other.name.toLowerCase();
			return ((thisName.equalsIgnoreCase(otherName)) &&
					(this.value != null ? this.value.equals(other.value) : other.value == null) &&
					this.isNegated == other.isNegated);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = (isCaseSensitiveName() ? this.name : this.name.toLowerCase()).hashCode();
		result = 31 * result + ObjectUtils.nullSafeHashCode(this.value);
		result = 31 * result + (this.isNegated ? 1 : 0);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (this.value != null) {
			builder.append(this.name);
			if (this.isNegated) {
				builder.append('!');
			}
			builder.append('=');
			builder.append(this.value);
		}
		else {
			if (this.isNegated) {
				builder.append('!');
			}
			builder.append(this.name);
		}
		return builder.toString();
	}
}
