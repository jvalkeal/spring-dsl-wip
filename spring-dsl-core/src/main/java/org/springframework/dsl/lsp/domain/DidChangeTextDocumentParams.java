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
package org.springframework.dsl.lsp.domain;

import java.util.ArrayList;
import java.util.List;

public class DidChangeTextDocumentParams {

	private VersionedTextDocumentIdentifier textDocument;

	private List<TextDocumentContentChangeEvent> contentChanges = new ArrayList<TextDocumentContentChangeEvent>();

	public VersionedTextDocumentIdentifier getTextDocument() {
		return textDocument;
	}

	public void setTextDocument(VersionedTextDocumentIdentifier textDocument) {
		this.textDocument = textDocument;
	}

	public List<TextDocumentContentChangeEvent> getContentChanges() {
		return contentChanges;
	}

	public void setContentChanges(List<TextDocumentContentChangeEvent> contentChanges) {
		this.contentChanges = contentChanges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentChanges == null) ? 0 : contentChanges.hashCode());
		result = prime * result + ((textDocument == null) ? 0 : textDocument.hashCode());
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
		DidChangeTextDocumentParams other = (DidChangeTextDocumentParams) obj;
		if (contentChanges == null) {
			if (other.contentChanges != null)
				return false;
		} else if (!contentChanges.equals(other.contentChanges))
			return false;
		if (textDocument == null) {
			if (other.textDocument != null)
				return false;
		} else if (!textDocument.equals(other.textDocument))
			return false;
		return true;
	}
}
