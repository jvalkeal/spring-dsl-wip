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
package org.springframework.dsl.lsp;

// TODO: Auto-generated Javadoc
/**
 * Represents an CoAP request or response entity, consisting of headers and body.
 *
 * @author Janne Valkealahti
 *
 * @param <T> the type of a entity body
 */
public class LspEntity<T> {

	/** The Constant EMPTY. */
	public static final LspEntity<?> EMPTY = new LspEntity<>();

	/** The body. */
	private final T body;

	/**
	 * Instantiates a new lsp entity.
	 */
	public LspEntity() {
		this(null);
	}

	/**
	 * Instantiates a new lsp entity.
	 *
	 * @param body the body
	 */
	public LspEntity(T body) {
		this.body = body;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public T getBody() {
		return body;
	}

}
