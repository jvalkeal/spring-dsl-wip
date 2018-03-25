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

import java.lang.reflect.Type;
import java.net.URI;

// TODO: Auto-generated Javadoc
/**
 * Extension of {@link LspEntity}.
 *
 * @author Janne Valkealahti
 *
 * @param <T> the type of a entity body
 */
public class LspRequestEntity<T> extends LspEntity<T> {

	/** The method. */
	private final LspMethod method;

	/** The url. */
	private final URI url;

	/** The type. */
	private final Type type;

	/**
	 * Instantiates a new lsp request entity.
	 *
	 * @param method the method
	 * @param url the url
	 * @param type the type
	 */
	public LspRequestEntity(LspMethod method, URI url, Type type) {
		super();
		this.method = method;
		this.url = url;
		this.type = type;
	}

}
