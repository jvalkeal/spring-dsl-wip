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
package org.springframework.dsl.lsp4j.converter;

import org.springframework.dsl.lsp.domain.CompletionOptions;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.domain.TextDocumentSyncKind;
import org.springframework.dsl.lsp.domain.TextDocumentSyncOptions;

/**
 * Utilities to convert between {@code LSP4J} and {@code Spring DSL} {@code LSP} domain objects.
 *
 * @author Janne Valkealahti
 *
 */
public final class ConverterUtils {

	public static InitializeResult toInitializeResult(org.eclipse.lsp4j.InitializeResult from) {
		InitializeResult to = new InitializeResult();
		if (from.getCapabilities() != null) {
			to.setCapabilities(toServerCapabilities(from.getCapabilities()));
		}
		return to;
	}

	public static org.eclipse.lsp4j.InitializeResult toInitializeResult(InitializeResult from) {
		org.eclipse.lsp4j.InitializeResult to = new org.eclipse.lsp4j.InitializeResult();
		if (from.getCapabilities() != null) {
			to.setCapabilities(toServerCapabilities(from.getCapabilities()));
		}
		return to;
	}

	public static ServerCapabilities toServerCapabilities(org.eclipse.lsp4j.ServerCapabilities from) {
		ServerCapabilities to = new ServerCapabilities();
		to.setHoverProvider(from.getHoverProvider());
		to.setCompletionProvider(toCompletionOptions(from.getCompletionProvider()));
		return to;
	}

	public static org.eclipse.lsp4j.ServerCapabilities toServerCapabilities(ServerCapabilities from) {
		org.eclipse.lsp4j.ServerCapabilities to = new org.eclipse.lsp4j.ServerCapabilities();
		to.setHoverProvider(from.getHoverProvider());
		to.setCompletionProvider(toCompletionOptions(from.getCompletionProvider()));
		to.setTextDocumentSync(toTextDocumentSyncKind(from.getTextDocumentSyncKind()));
		return to;
	}

	public static TextDocumentSyncKind toTextDocumentSyncKind(org.eclipse.lsp4j.TextDocumentSyncKind from) {
		if (from == null) {
			return null;
		}
		return TextDocumentSyncKind.valueOf(from.name());
	}

	public static org.eclipse.lsp4j.TextDocumentSyncKind toTextDocumentSyncKind(TextDocumentSyncKind from) {
		if (from == null) {
			return null;
		}
		return org.eclipse.lsp4j.TextDocumentSyncKind.valueOf(from.name());
	}

	public static CompletionOptions toCompletionOptions(org.eclipse.lsp4j.CompletionOptions from) {
		if (from == null) {
			return null;
		}
		return new CompletionOptions();
	}

	public static org.eclipse.lsp4j.CompletionOptions toCompletionOptions(CompletionOptions from) {
		if (from == null) {
			return null;
		}
		return new org.eclipse.lsp4j.CompletionOptions();
	}

}
