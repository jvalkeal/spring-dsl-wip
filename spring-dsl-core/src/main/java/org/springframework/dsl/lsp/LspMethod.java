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

import java.util.HashMap;
import java.util.Map;

/**
 * Java enumeration of {@code LSP} request methods.
 *
 * @author Janne Valkealahti
 *
 */
public enum LspMethod {

	INITIALIZE,

	INITIALIZED,

	TEXTDOCUMENT_DIDOPEN,

	TEXTDOCUMENT_DIDCHANGE,

	TEXTDOCUMENT_DIDSAVE,

	TEXTDOCUMENT_DIDCLOSE,

	TEXTDOCUMENT_WILLSAVE,

	TEXTDOCUMENT_WILLSAVEWAITUNTIL,

	TEXTDOCUMENT_COMPLETION,

	TEXTDOCUMENT_HOVER;

	/** The Constant mappings. */
	private static final Map<String, LspMethod> mappings = new HashMap<String, LspMethod>(10);

	static {
		for (LspMethod method : values()) {
			mappings.put(method.name(), method);
		}
	}

	/**
	 * Resolve the given method value to an {@code CoapMethod}.
	 *
	 * @param method the method value as a String
	 * @return the corresponding {@code CoapMethod}, or {@code null} if not found
	 */
	public static LspMethod resolve(String method) {
		return (method != null ? mappings.get(method) : null);
	}


	/**
	 * Determine whether this {@code CoapMethod} matches the given
	 * method value.
	 *
	 * @param method the method value as a String
	 * @return {@code true} if it matches, {@code false} otherwise
	 */
	public boolean matches(String method) {
		return (this == resolve(method));
	}
}
