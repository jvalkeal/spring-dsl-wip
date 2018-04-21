/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.dsl.document;

import java.util.List;

import org.springframework.dsl.lsp.domain.TextDocumentContentChangeEvent;

public class TextDocumentContentChange {

	private final TextDocument document;
	private final List<TextDocumentContentChangeEvent> changes;

	public TextDocumentContentChange(TextDocument doc, List<TextDocumentContentChangeEvent> changes) {
		this.document = doc;
		this.changes = changes;
	}

	public TextDocument getDocument() {
		return document;
	}

	public List<TextDocumentContentChangeEvent> getChanges() {
		return changes;
	}

}