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
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.Hover;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.Position;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.domain.TextDocumentIdentifier;
import org.springframework.dsl.lsp.domain.TextDocumentItem;
import org.springframework.dsl.lsp.domain.TextDocumentPositionParams;
import org.springframework.dsl.lsp.domain.TextDocumentSyncOptions;
import org.springframework.dsl.lsp.domain.VersionedTextDocumentIdentifier;

/**
 * Tests for {@link ConverterUtils}.
 *
 * @author Janne Valkealahti
 *
 */
public class ConverterUtilsTests {

	@Test
	public void testGenericConversionsUsingDefaultConstructors() {
		// test back and forward conversion from spring dsl and lsp4j
		// to both directions which should catch most common mistakes.

		assertInitializeParams(new InitializeParams());
		assertInitializeParams(new org.eclipse.lsp4j.InitializeParams());

		assertInitializeResult(new InitializeResult());
		assertInitializeResult(new org.eclipse.lsp4j.InitializeResult());

		assertServerCapabilities(new ServerCapabilities());
		assertServerCapabilities(new org.eclipse.lsp4j.ServerCapabilities());

		assertTextDocumentSyncOptions(new TextDocumentSyncOptions());
		assertTextDocumentSyncOptions(new org.eclipse.lsp4j.TextDocumentSyncOptions());

		assertDidChangeTextDocumentParams(new DidChangeTextDocumentParams());
		assertDidChangeTextDocumentParams(new org.eclipse.lsp4j.DidChangeTextDocumentParams());

		assertDidCloseTextDocumentParams(new DidCloseTextDocumentParams());
		assertDidCloseTextDocumentParams(new org.eclipse.lsp4j.DidCloseTextDocumentParams());

		assertTextDocumentItem(new TextDocumentItem());
		assertTextDocumentItem(new org.eclipse.lsp4j.TextDocumentItem());

		assertDidOpenTextDocumentParams(new DidOpenTextDocumentParams());
		assertDidOpenTextDocumentParams(new org.eclipse.lsp4j.DidOpenTextDocumentParams());

		assertDidSaveTextDocumentParams(new DidSaveTextDocumentParams());
		assertDidSaveTextDocumentParams(new org.eclipse.lsp4j.DidSaveTextDocumentParams());

		assertHover(new Hover());
		assertHover(new org.eclipse.lsp4j.Hover());

		assertPosition(new Position());
		assertPosition(new org.eclipse.lsp4j.Position());

		assertTextDocumentIdentifier(new TextDocumentIdentifier());
		assertTextDocumentIdentifier(new org.eclipse.lsp4j.TextDocumentIdentifier());

		assertVersionedTextDocumentIdentifier(new VersionedTextDocumentIdentifier());
		assertVersionedTextDocumentIdentifier(new org.eclipse.lsp4j.VersionedTextDocumentIdentifier());

		assertTextDocumentPositionParams(new TextDocumentPositionParams());
		assertTextDocumentPositionParams(new org.eclipse.lsp4j.TextDocumentPositionParams());
	}

	@Test
	public void testGenericConversionsUsingValues() {

		assertDidOpenTextDocumentParams(
				new DidOpenTextDocumentParams(new TextDocumentItem("fakeuri", "fakelanguageid", 9, "faketext")));
		assertDidOpenTextDocumentParams(new org.eclipse.lsp4j.DidOpenTextDocumentParams(
				new org.eclipse.lsp4j.TextDocumentItem("fakeuri", "fakelanguageid", 9, "faketext")));

		VersionedTextDocumentIdentifier versionedTextDocumentIdentifier = new VersionedTextDocumentIdentifier();
		versionedTextDocumentIdentifier.setVersion(1);
		versionedTextDocumentIdentifier.setUri("fakeuri");
		DidChangeTextDocumentParams didChangeTextDocumentParams = new DidChangeTextDocumentParams();
		didChangeTextDocumentParams.setTextDocument(versionedTextDocumentIdentifier);
		assertDidChangeTextDocumentParams(didChangeTextDocumentParams);

		org.eclipse.lsp4j.VersionedTextDocumentIdentifier lsp4jVersionedTextDocumentIdentifier = new org.eclipse.lsp4j.VersionedTextDocumentIdentifier();
		lsp4jVersionedTextDocumentIdentifier.setVersion(1);
		lsp4jVersionedTextDocumentIdentifier.setUri("fakeuri");
		org.eclipse.lsp4j.DidChangeTextDocumentParams lsp4jdidChangeTextDocumentParams = new org.eclipse.lsp4j.DidChangeTextDocumentParams();
		lsp4jdidChangeTextDocumentParams.setTextDocument(lsp4jVersionedTextDocumentIdentifier);
		assertDidChangeTextDocumentParams(new org.eclipse.lsp4j.DidChangeTextDocumentParams());
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

	private static void assertDidChangeTextDocumentParams(DidChangeTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toDidChangeTextDocumentParams(ConverterUtils.toDidChangeTextDocumentParams(from)));
	}

	private static void assertDidChangeTextDocumentParams(org.eclipse.lsp4j.DidChangeTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toDidChangeTextDocumentParams(ConverterUtils.toDidChangeTextDocumentParams(from)));
	}

	private static void assertDidCloseTextDocumentParams(DidCloseTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toDidCloseTextDocumentParams(ConverterUtils.toDidCloseTextDocumentParams(from)));
	}

	private static void assertDidCloseTextDocumentParams(org.eclipse.lsp4j.DidCloseTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toDidCloseTextDocumentParams(ConverterUtils.toDidCloseTextDocumentParams(from)));
	}

	private static void assertTextDocumentItem(TextDocumentItem from) {
		assertObjects(from, ConverterUtils.toTextDocumentItem(ConverterUtils.toTextDocumentItem(from)));
	}

	private static void assertTextDocumentItem(org.eclipse.lsp4j.TextDocumentItem from) {
		assertObjects(from, ConverterUtils.toTextDocumentItem(ConverterUtils.toTextDocumentItem(from)));
	}

	private static void assertDidOpenTextDocumentParams(DidOpenTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toDidOpenTextDocumentParams(ConverterUtils.toDidOpenTextDocumentParams(from)));
	}

	private static void assertDidOpenTextDocumentParams(org.eclipse.lsp4j.DidOpenTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toDidOpenTextDocumentParams(ConverterUtils.toDidOpenTextDocumentParams(from)));
	}

	private static void assertDidSaveTextDocumentParams(DidSaveTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toDidSaveTextDocumentParams(ConverterUtils.toDidSaveTextDocumentParams(from)));
	}

	private static void assertDidSaveTextDocumentParams(org.eclipse.lsp4j.DidSaveTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toDidSaveTextDocumentParams(ConverterUtils.toDidSaveTextDocumentParams(from)));
	}

	private static void assertHover(Hover from) {
		assertObjects(from, ConverterUtils.toHover(ConverterUtils.toHover(from)));
	}

	private static void assertHover(org.eclipse.lsp4j.Hover from) {
		assertObjects(from, ConverterUtils.toHover(ConverterUtils.toHover(from)));
	}

	private static void assertPosition(Position from) {
		assertObjects(from, ConverterUtils.toPosition(ConverterUtils.toPosition(from)));
	}

	private static void assertPosition(org.eclipse.lsp4j.Position from) {
		assertObjects(from, ConverterUtils.toPosition(ConverterUtils.toPosition(from)));
	}

	private static void assertTextDocumentIdentifier(TextDocumentIdentifier from) {
		assertObjects(from, ConverterUtils.toTextDocumentIdentifier(ConverterUtils.toTextDocumentIdentifier(from)));
	}

	private static void assertTextDocumentIdentifier(org.eclipse.lsp4j.TextDocumentIdentifier from) {
		assertObjects(from, ConverterUtils.toTextDocumentIdentifier(ConverterUtils.toTextDocumentIdentifier(from)));
	}

	private static void assertVersionedTextDocumentIdentifier(VersionedTextDocumentIdentifier from) {
		assertObjects(from, ConverterUtils.toVersionedTextDocumentIdentifier(ConverterUtils.toVersionedTextDocumentIdentifier(from)));
	}

	private static void assertVersionedTextDocumentIdentifier(org.eclipse.lsp4j.VersionedTextDocumentIdentifier from) {
		assertObjects(from, ConverterUtils.toVersionedTextDocumentIdentifier(ConverterUtils.toVersionedTextDocumentIdentifier(from)));
	}

	private static void assertTextDocumentPositionParams(TextDocumentPositionParams from) {
		assertObjects(from, ConverterUtils.toTextDocumentPositionParams(ConverterUtils.toTextDocumentPositionParams(from)));
	}

	private static void assertTextDocumentPositionParams(org.eclipse.lsp4j.TextDocumentPositionParams from) {
		assertObjects(from, ConverterUtils.toTextDocumentPositionParams(ConverterUtils.toTextDocumentPositionParams(from)));
	}

	private static void assertObjects(Object from, Object to) {
		assertThat(from).isNotSameAs(to);
		assertThat(from).isEqualTo(to);
		assertThat(from).isEqualToComparingFieldByField(to);
	}
}
