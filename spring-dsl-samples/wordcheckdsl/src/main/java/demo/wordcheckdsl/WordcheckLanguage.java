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
package demo.wordcheckdsl;

import org.springframework.dsl.document.Document;
import org.springframework.dsl.model.LanguageId;

/**
 * {@code wordcheck} language representation containing parser to tokenize a dsl
 * which can be used to give various answers to {@code LSP} requests.
 *
 * @author Janne Valkealahti
 * @author Kris De Volder
 *
 */
public class WordcheckLanguage {

	public final static LanguageId LANGUAGEID = LanguageId.languageId("wordcheck", "Wordcheck Language");
	private final Document document;

	/**
	 * Instantiates a new simple language.
	 *
	 * @param document the document
	 */
	public WordcheckLanguage(Document document) {
		this.document = document;
	}

	/**
	 * Gets the document known to this language.
	 *
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}

}
