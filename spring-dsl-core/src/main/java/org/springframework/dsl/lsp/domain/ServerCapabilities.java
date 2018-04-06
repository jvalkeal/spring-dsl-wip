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

/**
 * {@code LSP} domain object for a specification {@code ServerCapabilities}.
 * <p>
 * In a spec field {@code textDocumentSync} can be either
 * {@code TextDocumentSyncOptions} matching {@link TextDocumentSyncOptions} or a
 * number matching ordinal in an enum {@link TextDocumentSyncKind}.
 *
 * @author Janne Valkealahti
 * @see TextDocumentSyncOptions
 * @see TextDocumentSyncKind
 *
 */
public class ServerCapabilities {

	private TextDocumentSyncOptions textDocumentSyncOptions;

	private TextDocumentSyncKind textDocumentSyncKind;

	private Boolean hoverProvider;

	private CompletionOptions completionProvider;

	public ServerCapabilities() {
	}

	public ServerCapabilities(TextDocumentSyncKind textDocumentSyncKind) {
		this.textDocumentSyncKind = textDocumentSyncKind;
	}

	public ServerCapabilities(TextDocumentSyncOptions textDocumentSyncOptions) {
		this.textDocumentSyncOptions = textDocumentSyncOptions;
	}

	public TextDocumentSyncOptions getTextDocumentSyncOptions() {
		return textDocumentSyncOptions;
	}

	public void setTextDocumentSyncOptions(TextDocumentSyncOptions textDocumentSyncOptions) {
		this.textDocumentSyncOptions = textDocumentSyncOptions;
	}

	public TextDocumentSyncKind getTextDocumentSyncKind() {
		return textDocumentSyncKind;
	}

	public void setTextDocumentSyncKind(TextDocumentSyncKind textDocumentSyncKind) {
		this.textDocumentSyncKind = textDocumentSyncKind;
	}

	public Boolean getHoverProvider() {
		return hoverProvider;
	}

	public void setHoverProvider(Boolean hoverProvider) {
		this.hoverProvider = hoverProvider;
	}

	public CompletionOptions getCompletionProvider() {
		return completionProvider;
	}

	public void setCompletionProvider(CompletionOptions completionProvider) {
		this.completionProvider = completionProvider;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((completionProvider == null) ? 0 : completionProvider.hashCode());
		result = prime * result + ((hoverProvider == null) ? 0 : hoverProvider.hashCode());
		result = prime * result + ((textDocumentSyncKind == null) ? 0 : textDocumentSyncKind.hashCode());
		result = prime * result + ((textDocumentSyncOptions == null) ? 0 : textDocumentSyncOptions.hashCode());
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
		ServerCapabilities other = (ServerCapabilities) obj;
		if (completionProvider == null) {
			if (other.completionProvider != null)
				return false;
		} else if (!completionProvider.equals(other.completionProvider))
			return false;
		if (hoverProvider == null) {
			if (other.hoverProvider != null)
				return false;
		} else if (!hoverProvider.equals(other.hoverProvider))
			return false;
		if (textDocumentSyncKind != other.textDocumentSyncKind)
			return false;
		if (textDocumentSyncOptions == null) {
			if (other.textDocumentSyncOptions != null)
				return false;
		} else if (!textDocumentSyncOptions.equals(other.textDocumentSyncOptions))
			return false;
		return true;
	}
}
