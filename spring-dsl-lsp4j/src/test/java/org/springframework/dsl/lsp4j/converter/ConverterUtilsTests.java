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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.domain.TextDocumentSyncOptions;

public class ConverterUtilsTests {

	@Test
	public void testGenericConversionsUsingDefaultConstructors() {
		// we do back and forward conversion from spring dsl and lsp4j
		// to both directions which should catch most common mistakes.

		assertInitializeParams(new InitializeParams());
		assertInitializeParams(new org.eclipse.lsp4j.InitializeParams());

		assertInitializeResult(new InitializeResult());
		assertInitializeResult(new org.eclipse.lsp4j.InitializeResult());

		assertServerCapabilities(new ServerCapabilities());
		assertServerCapabilities(new org.eclipse.lsp4j.ServerCapabilities());

		assertTextDocumentSyncOptions(new TextDocumentSyncOptions());
		assertTextDocumentSyncOptions(new org.eclipse.lsp4j.TextDocumentSyncOptions());
	}

	private static void assertInitializeParams(InitializeParams from) {
		assertObjects(from, ConverterUtils.toInitializeParams(ConverterUtils.toInitializeParams(from)));
	}

	private static void assertInitializeParams(org.eclipse.lsp4j.InitializeParams from) {
		assertObjects(from, ConverterUtils.toInitializeParams(ConverterUtils.toInitializeParams(from)));
	}

	private static void assertInitializeResult(InitializeResult from) {
		assertObjects(from, ConverterUtils.toInitializeResult(ConverterUtils.toInitializeResult(from)));
	}

	private static void assertInitializeResult(org.eclipse.lsp4j.InitializeResult from) {
		assertObjects(from, ConverterUtils.toInitializeResult(ConverterUtils.toInitializeResult(from)));
	}

	private static void assertServerCapabilities(ServerCapabilities from) {
		assertObjects(from, ConverterUtils.toServerCapabilities(ConverterUtils.toServerCapabilities(from)));
	}

	private static void assertServerCapabilities(org.eclipse.lsp4j.ServerCapabilities from) {
		assertObjects(from, ConverterUtils.toServerCapabilities(ConverterUtils.toServerCapabilities(from)));
	}

	private static void assertTextDocumentSyncOptions(TextDocumentSyncOptions from) {
		assertObjects(from, ConverterUtils.toTextDocumentSyncOptions(ConverterUtils.toTextDocumentSyncOptions(from)));
	}

	private static void assertTextDocumentSyncOptions(org.eclipse.lsp4j.TextDocumentSyncOptions from) {
		assertObjects(from, ConverterUtils.toTextDocumentSyncOptions(ConverterUtils.toTextDocumentSyncOptions(from)));
	}

	private static void assertObjects(Object from, Object to) {
		assertThat(from).isNotSameAs(to);
		assertThat(from).isEqualTo(to);
		assertThat(from).isEqualToComparingFieldByField(to);
	}
}
