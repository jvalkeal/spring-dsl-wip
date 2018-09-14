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

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Instead of working with a plain {@code String} as a {@code languageId}, in
 * this class we also keep a generic name of the language.
 *
 * @author Janne Valkealahti
 *
 */
public class LanguageId {

	public static final LanguageId ALL = languageId("*");
	public static final LanguageId TXT = languageId("txt", "Plaintext");
	public static final LanguageId BAT = languageId("bat", "Windows Bat");
	public static final LanguageId JAVA = languageId("java", "Java");
	// TODO: map all languages from LSP

	private final String identifier;
	private final String description;

	/**
	 * Instantiates a new language id.
	 *
	 * @param identifier the identifier
	 * @param description the language
	 */
	public LanguageId(String identifier, String description) {
		Assert.hasLength(identifier, "identifier must not be empty");
		this.identifier = identifier;
		this.description = description;
	}

	/**
	 * Gets the language identifier.
	 *
	 * @return the language identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Gets the language description.
	 *
	 * @return the language description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Indicates if a given {@link LanguageId} is compatible.
	 *
	 * @param other the language id
	 * @return true, if is compatible with
	 */
	public boolean isCompatibleWith(@Nullable LanguageId other) {
		if (other == null) {
			return false;
		}
		if (this == ALL) {
			return true;
		}
		return ObjectUtils.nullSafeEquals(this, other);
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

	/**
	 * Gets a {@link LanguageId} with identifier.
	 *
	 * @param identifier the identifier
	 * @return the language id
	 */
	public static LanguageId languageId(String identifier) {
		return languageId(identifier, null);
	}

	/**
	 * Gets a {@link LanguageId} with identifier and defined language description.
	 *
	 * @param identifier the identifier
	 * @param language the language
	 * @return the language id
	 */
	public static LanguageId languageId(String identifier, String language) {
		return new LanguageId(identifier, language);
	}
}
