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
package org.springframework.dsl.model;

import org.springframework.util.Assert;

/**
 * Instead of working with a plain {@code String} as a {@code languageId}, in
 * this class we also keep a generic name of the language.
 *
 * @author Janne Valkealahti
 *
 */
public class LanguageId {

	public static final LanguageId TXT = languageId("txt", "Plaintext");
	public static final LanguageId BAT = languageId("bat", "Windows Bat");
	public static final LanguageId JAVA = languageId("java", "Java");

	private final String identifier;
	private final String language;

	/**
	 * Instantiates a new language id.
	 *
	 * @param identifier the identifier
	 * @param language the language
	 */
	public LanguageId(String identifier, String language) {
		Assert.hasLength(identifier, "identifier must not be empty");
		this.identifier = identifier;
		this.language = language;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getLanguage() {
		return language;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LanguageId other = (LanguageId) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	public static LanguageId languageId(String identifier) {
		return languageId(identifier, null);
	}

	public static LanguageId languageId(String identifier, String language) {
		return new LanguageId(identifier, language);
	}
}
