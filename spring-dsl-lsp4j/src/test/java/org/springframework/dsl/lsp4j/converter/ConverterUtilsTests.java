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

import java.util.Arrays;

import org.junit.Test;
import org.springframework.dsl.lsp.domain.ClientCapabilities;
import org.springframework.dsl.lsp.domain.Command;
import org.springframework.dsl.lsp.domain.CompletionContext;
import org.springframework.dsl.lsp.domain.CompletionItem;
import org.springframework.dsl.lsp.domain.CompletionOptions;
import org.springframework.dsl.lsp.domain.CompletionParams;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.Hover;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.InitializedParams;
import org.springframework.dsl.lsp.domain.MarkupContent;
import org.springframework.dsl.lsp.domain.MarkupKind;
import org.springframework.dsl.lsp.domain.Position;
import org.springframework.dsl.lsp.domain.PublishDiagnosticsParams;
import org.springframework.dsl.lsp.domain.Range;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.domain.TextDocumentContentChangeEvent;
import org.springframework.dsl.lsp.domain.TextDocumentIdentifier;
import org.springframework.dsl.lsp.domain.TextDocumentItem;
import org.springframework.dsl.lsp.domain.TextDocumentPositionParams;
import org.springframework.dsl.lsp.domain.TextDocumentSyncOptions;
import org.springframework.dsl.lsp.domain.TextEdit;
import org.springframework.dsl.lsp.domain.VersionedTextDocumentIdentifier;
import org.springframework.dsl.lsp.domain.WillSaveTextDocumentParams;

/**
 * Tests for {@link ConverterUtils}.
 *
 * @author Janne Valkealahti
 *
 */
public class ConverterUtilsTests {

	@Test
	public void testInitializeParams() {
		assertInitializeParams(new InitializeParams());
		assertInitializeParams(new org.eclipse.lsp4j.InitializeParams());
	}

	@Test
	public void testClientCapabilities() {
		assertClientCapabilities(new ClientCapabilities());
		assertClientCapabilities(new org.eclipse.lsp4j.ClientCapabilities());
	}

	@Test
	public void testInitializedParams() {
		assertInitializedParams(new InitializedParams());
		assertInitializedParams(new org.eclipse.lsp4j.InitializedParams());
	}

	@Test
	public void testInitializeResult() {
		assertInitializeResult(new InitializeResult());
		assertInitializeResult(new org.eclipse.lsp4j.InitializeResult());
	}

	@Test
	public void testServerCapabilities() {
		assertServerCapabilities(new ServerCapabilities());
		assertServerCapabilities(new org.eclipse.lsp4j.ServerCapabilities());
	}

	@Test
	public void testTextDocumentSyncOptions() {
		assertTextDocumentSyncOptions(new TextDocumentSyncOptions());
		assertTextDocumentSyncOptions(new org.eclipse.lsp4j.TextDocumentSyncOptions());
	}

	@Test
	public void testDidOpenTextDocumentParams() {
		assertDidOpenTextDocumentParams(new DidOpenTextDocumentParams());
		assertDidOpenTextDocumentParams(new org.eclipse.lsp4j.DidOpenTextDocumentParams());

		DidOpenTextDocumentParams from1 = DidOpenTextDocumentParams.didOpenTextDocumentParams()
				.textDocument()
					.uri("uri")
					.languageId("language")
					.version(1)
					.text("text")
					.and()
				.build();
		assertDidOpenTextDocumentParams(from1);
	}

	@Test
	public void testDidChangeTextDocumentParams() {
		assertDidChangeTextDocumentParams(new DidChangeTextDocumentParams());
		assertDidChangeTextDocumentParams(new org.eclipse.lsp4j.DidChangeTextDocumentParams());

		DidChangeTextDocumentParams from1 = DidChangeTextDocumentParams.didChangeTextDocumentParams()
				.textDocument()
					.uri("uri")
					.version(1)
					.and()
				.contentChanges()
					.text("text1")
					.rangeLength(1)
					.range()
						.start()
							.line(1)
							.character(1)
							.and()
						.end()
							.line(2)
							.character(2)
							.and()
						.and()
					.and()
				.contentChanges()
					.text("text2")
					.rangeLength(2)
					.range()
						.start()
							.line(3)
							.character(3)
							.and()
						.end()
							.line(4)
							.character(4)
							.and()
						.and()
					.and()
				.build();
		assertThat(from1.getContentChanges().size()).isEqualTo(2);
		assertDidChangeTextDocumentParams(from1);
	}

	@Test
	public void testDidCloseTextDocumentParams() {
		assertDidCloseTextDocumentParams(new DidCloseTextDocumentParams());
		assertDidCloseTextDocumentParams(new org.eclipse.lsp4j.DidCloseTextDocumentParams());
	}

	@Test
	public void testTextDocumentItem() {
		assertTextDocumentItem(new TextDocumentItem());
		assertTextDocumentItem(new org.eclipse.lsp4j.TextDocumentItem());

		TextDocumentItem from1 = TextDocumentItem.textDocumentItem()
				.uri("uri")
				.languageId("language")
				.version(1)
				.text("text")
				.build();
		assertTextDocumentItem(from1);
	}

	@Test
	public void testDidSaveTextDocumentParams() {
		assertDidSaveTextDocumentParams(new DidSaveTextDocumentParams());
		assertDidSaveTextDocumentParams(new org.eclipse.lsp4j.DidSaveTextDocumentParams());

		DidSaveTextDocumentParams from1 = DidSaveTextDocumentParams.didSaveTextDocumentParams()
			.textDocument()
				.uri("uri")
				.and()
			.text("text")
			.build();
		assertDidSaveTextDocumentParams(from1);

		org.eclipse.lsp4j.DidSaveTextDocumentParams from2 = new org.eclipse.lsp4j.DidSaveTextDocumentParams();
		from2.setText("text");
		from2.setTextDocument(new org.eclipse.lsp4j.TextDocumentIdentifier("uri"));
		assertDidSaveTextDocumentParams(from2);
	}

	@Test
	public void testWillSaveTextDocumentParams() {
		assertWillSaveTextDocumentParams(new WillSaveTextDocumentParams());
		assertWillSaveTextDocumentParams(new org.eclipse.lsp4j.WillSaveTextDocumentParams());
	}

	@Test
	public void testMarkupContent() {
		assertMarkupContent(new MarkupContent());
		assertMarkupContent(new org.eclipse.lsp4j.MarkupContent());
	}

	@Test
	public void testCompletionContext() {
		assertCompletionContext(new CompletionContext());
		assertCompletionContext(new org.eclipse.lsp4j.CompletionContext());
	}

	@Test
	public void testCompletionParams() {
		assertCompletionParams(new CompletionParams());
		assertCompletionParams(new org.eclipse.lsp4j.CompletionParams());
	}

	@Test
	public void testTextDocumentIdentifier() {
		assertTextDocumentIdentifier(new TextDocumentIdentifier());
		assertTextDocumentIdentifier(new org.eclipse.lsp4j.TextDocumentIdentifier());

		TextDocumentIdentifier from1 = TextDocumentIdentifier.textDocumentIdentifier()
				.uri("uri")
				.build();
		assertTextDocumentIdentifier(from1);
	}

	@Test
	public void testVersionedTextDocumentIdentifier() {
		assertVersionedTextDocumentIdentifier(new VersionedTextDocumentIdentifier());
		assertVersionedTextDocumentIdentifier(new org.eclipse.lsp4j.VersionedTextDocumentIdentifier());

		VersionedTextDocumentIdentifier from1 = VersionedTextDocumentIdentifier.versionedTextDocumentIdentifier()
				.version(1)
				.uri("uri")
				.build();
		assertVersionedTextDocumentIdentifier(from1);
	}

	@Test
	public void testTextDocumentPositionParams() {
		assertTextDocumentPositionParams(new TextDocumentPositionParams());
		assertTextDocumentPositionParams(new org.eclipse.lsp4j.TextDocumentPositionParams());
	}

	@Test
	public void testCommand() {
		assertCommand(new Command());
		assertCommand(new org.eclipse.lsp4j.Command());
	}

	@Test
	public void testCompletionOptions() {
		assertCompletionOptions(new CompletionOptions());
		assertCompletionOptions(new org.eclipse.lsp4j.CompletionOptions());
	}

	@Test
	public void testPublishDiagnosticsParams() {
		assertPublishDiagnosticsParams(new PublishDiagnosticsParams());
		assertPublishDiagnosticsParams(new org.eclipse.lsp4j.PublishDiagnosticsParams());
	}

	@Test
	public void testTextDocumentContentChangeEvent() {
		assertTextDocumentContentChangeEvent(new TextDocumentContentChangeEvent());
		assertTextDocumentContentChangeEvent(new org.eclipse.lsp4j.TextDocumentContentChangeEvent());

		TextDocumentContentChangeEvent from1 = TextDocumentContentChangeEvent.textDocumentContentChangeEvent()
				.text("text")
				.rangeLength(1)
				.range()
					.start()
						.line(1)
						.character(1)
						.and()
					.end()
						.line(2)
						.character(2)
						.and()
					.and()
				.build();
		assertTextDocumentContentChangeEvent(from1);
	}

	@Test
	public void testHover() {
		assertHover(new Hover());
		assertHover(new org.eclipse.lsp4j.Hover());

		Hover from1 = Hover.hover()
				.contents()
					.kind(MarkupKind.plaintext)
					.value("value")
					.and()
				.range()
					.start()
						.line(1)
						.character(1)
						.and()
					.end()
						.line(2)
						.character(2)
						.and()
					.and()
				.build();
		assertHover(from1);

		from1 = Hover.hover()
				.contents()
					.kind(MarkupKind.plaintext)
					.value("value")
					.and()
				.build();
		assertHover(from1);

		org.eclipse.lsp4j.Hover from2 = new org.eclipse.lsp4j.Hover();
		from2.setRange(new org.eclipse.lsp4j.Range(new org.eclipse.lsp4j.Position(1, 1),
				new org.eclipse.lsp4j.Position(2, 2)));
		org.eclipse.lsp4j.MarkupContent markupContent = new org.eclipse.lsp4j.MarkupContent();
		markupContent.setKind("plaintext");
		markupContent.setValue("value");
		from2.setContents(markupContent);
		assertHover(from2);
	}

	@Test
	public void testPosition() {
		assertPosition(new Position());
		assertPosition(new org.eclipse.lsp4j.Position());

		Position from1 = Position.position()
			.line(1)
			.character(1)
			.build();
		assertPosition(from1);

		org.eclipse.lsp4j.Position from2 = new org.eclipse.lsp4j.Position(1, 1);
		assertPosition(from2);
	}

	@Test
	public void testRange() {
		assertRange(new Range());
		assertRange(new org.eclipse.lsp4j.Range());

		Range from1 = Range.range()
			.start()
				.line(1)
				.character(1)
				.and()
			.end()
				.line(2)
				.character(2)
				.and()
			.build();
		assertRange(from1);

		org.eclipse.lsp4j.Range from2 = new org.eclipse.lsp4j.Range(new org.eclipse.lsp4j.Position(1, 1),
				new org.eclipse.lsp4j.Position(2, 2));
		assertRange(from2);
	}

	@Test
	public void testTextEdit() {
		assertTextEdit(new TextEdit());
		assertTextEdit(new org.eclipse.lsp4j.TextEdit());

		TextEdit from1 = TextEdit.textEdit()
			.range()
				.start()
					.line(1)
					.character(1)
					.and()
				.end()
					.line(2)
					.character(2)
					.and()
				.and()
			.newText("text")
			.build();
		assertTextEdit(from1);

	}

	@Test
	public void testCompletionItem() {
		assertCompletionItem(new CompletionItem());
		assertCompletionItem(new org.eclipse.lsp4j.CompletionItem());

		CompletionItem from1 = CompletionItem.completionItem()
			.label("label")
			.textEdit()
				.range()
					.start()
						.line(1)
						.character(1)
						.and()
					.end()
						.line(2)
						.character(2)
						.and()
					.and()
				.newText("text")
			.and()
			.build();
		assertCompletionItem(from1);

		from1 = CompletionItem.completionItem()
				.label("label")
				.build();
		assertCompletionItem(from1);

		org.eclipse.lsp4j.CompletionItem from2 = new org.eclipse.lsp4j.CompletionItem();
		from2.setLabel("label");
		org.eclipse.lsp4j.TextEdit textEdit = new org.eclipse.lsp4j.TextEdit(
				new org.eclipse.lsp4j.Range(new org.eclipse.lsp4j.Position(1, 1), new org.eclipse.lsp4j.Position(2, 2)),
				"text");
		from2.setTextEdit(textEdit);
	}

	@Test
	public void testGenericConversionsUsingValues() {

		assertTextDocumentIdentifier(new TextDocumentIdentifier("fakeuri"));
		assertTextDocumentIdentifier(new org.eclipse.lsp4j.TextDocumentIdentifier("fakeuri"));

		assertDidOpenTextDocumentParams(
				new DidOpenTextDocumentParams(new TextDocumentItem("fakeuri", "fakelanguageid", 9, "faketext")));
		assertDidOpenTextDocumentParams(new org.eclipse.lsp4j.DidOpenTextDocumentParams(
				new org.eclipse.lsp4j.TextDocumentItem("fakeuri", "fakelanguageid", 9, "faketext")));

		VersionedTextDocumentIdentifier versionedTextDocumentIdentifier = new VersionedTextDocumentIdentifier();
		versionedTextDocumentIdentifier.setVersion(1);
		versionedTextDocumentIdentifier.setUri("fakeuri");
		DidChangeTextDocumentParams didChangeTextDocumentParams = new DidChangeTextDocumentParams();
		didChangeTextDocumentParams.setTextDocument(versionedTextDocumentIdentifier);
		didChangeTextDocumentParams
				.setContentChanges(Arrays.asList(new TextDocumentContentChangeEvent(new Range(0, 0, 0), 0, "0")));
		assertDidChangeTextDocumentParams(didChangeTextDocumentParams);

		org.eclipse.lsp4j.VersionedTextDocumentIdentifier lsp4jVersionedTextDocumentIdentifier = new org.eclipse.lsp4j.VersionedTextDocumentIdentifier();
		lsp4jVersionedTextDocumentIdentifier.setVersion(1);
		lsp4jVersionedTextDocumentIdentifier.setUri("fakeuri");
		org.eclipse.lsp4j.DidChangeTextDocumentParams lsp4jdidChangeTextDocumentParams = new org.eclipse.lsp4j.DidChangeTextDocumentParams();
		lsp4jdidChangeTextDocumentParams.setTextDocument(lsp4jVersionedTextDocumentIdentifier);
		lsp4jdidChangeTextDocumentParams.setContentChanges(
				Arrays.asList(new org.eclipse.lsp4j.TextDocumentContentChangeEvent(new org.eclipse.lsp4j.Range(
						new org.eclipse.lsp4j.Position(0, 0), new org.eclipse.lsp4j.Position(0, 0)), 0, "0")));
		assertDidChangeTextDocumentParams(lsp4jdidChangeTextDocumentParams);

		TextDocumentIdentifier textDocumentIdentifier = new TextDocumentIdentifier();
		textDocumentIdentifier.setUri("fakeuri");
		DidCloseTextDocumentParams didCloseTextDocumentParams = new DidCloseTextDocumentParams();
		didCloseTextDocumentParams.setTextDocument(textDocumentIdentifier);
		assertDidCloseTextDocumentParams(didCloseTextDocumentParams);

		org.eclipse.lsp4j.TextDocumentIdentifier lsp4jTextDocumentIdentifier = new org.eclipse.lsp4j.TextDocumentIdentifier();
		lsp4jTextDocumentIdentifier.setUri("fakeuri");
		org.eclipse.lsp4j.DidCloseTextDocumentParams lsp4jDidCloseTextDocumentParams = new org.eclipse.lsp4j.DidCloseTextDocumentParams();
		lsp4jDidCloseTextDocumentParams.setTextDocument(lsp4jTextDocumentIdentifier);
		assertDidCloseTextDocumentParams(lsp4jDidCloseTextDocumentParams);

		TextDocumentPositionParams textDocumentPositionParams = new TextDocumentPositionParams();
		textDocumentPositionParams.setPosition(new Position(1, 1));
		textDocumentPositionParams.setTextDocument(textDocumentIdentifier);
		assertTextDocumentPositionParams(textDocumentPositionParams);

		org.eclipse.lsp4j.TextDocumentPositionParams lsp4jTextDocumentPositionParams = new org.eclipse.lsp4j.TextDocumentPositionParams();
		lsp4jTextDocumentPositionParams.setPosition(new org.eclipse.lsp4j.Position(1, 1));
		lsp4jTextDocumentPositionParams.setTextDocument(lsp4jTextDocumentIdentifier);
		assertTextDocumentPositionParams(lsp4jTextDocumentPositionParams);

		Hover hover = new Hover();
		Position start = new Position(1, 1);
		Position end = new Position(1, 1);
		Range range = new Range(start, end);
		hover.setRange(range);
		MarkupContent contents = new MarkupContent();
		contents.setKind(MarkupKind.plaintext);
		contents.setValue("hi");
		hover.setContents(contents);
		assertHover(hover);

		org.eclipse.lsp4j.Hover lsp4jHover = new org.eclipse.lsp4j.Hover();
		org.eclipse.lsp4j.Position lsp4jStart = new org.eclipse.lsp4j.Position(1, 1);
		org.eclipse.lsp4j.Position lsp4jEnd = new org.eclipse.lsp4j.Position(1, 1);
		org.eclipse.lsp4j.Range lsp4jRange = new org.eclipse.lsp4j.Range(lsp4jStart, lsp4jEnd);
		lsp4jHover.setRange(lsp4jRange);
		org.eclipse.lsp4j.MarkupContent markupContent = new org.eclipse.lsp4j.MarkupContent();
		lsp4jHover.setContents(markupContent);
		assertHover(lsp4jHover);

		CompletionParams completionParams = new CompletionParams();
		completionParams.setTextDocument(textDocumentIdentifier);
		assertCompletionParams(completionParams);

		org.eclipse.lsp4j.CompletionParams lsp4jCompletionParams = new org.eclipse.lsp4j.CompletionParams();
		lsp4jCompletionParams.setTextDocument(lsp4jTextDocumentIdentifier);
		assertCompletionParams(lsp4jCompletionParams);
	}

	private static void assertInitializeParams(InitializeParams from) {
		assertObjects(from, ConverterUtils.toInitializeParams(ConverterUtils.toInitializeParams(from)));
	}

	private static void assertInitializeParams(org.eclipse.lsp4j.InitializeParams from) {
		assertObjects(from, ConverterUtils.toInitializeParams(ConverterUtils.toInitializeParams(from)));
	}

	private static void assertClientCapabilities(ClientCapabilities from) {
		assertObjects(from, ConverterUtils.toClientCapabilities(ConverterUtils.toClientCapabilities(from)));
	}

	private static void assertClientCapabilities(org.eclipse.lsp4j.ClientCapabilities from) {
		assertObjects(from, ConverterUtils.toClientCapabilities(ConverterUtils.toClientCapabilities(from)));
	}

	private static void assertInitializedParams(InitializedParams from) {
		assertObjects(from, ConverterUtils.toInitializedParams(ConverterUtils.toInitializedParams(from)));
	}

	private static void assertInitializedParams(org.eclipse.lsp4j.InitializedParams from) {
		assertObjects(from, ConverterUtils.toInitializedParams(ConverterUtils.toInitializedParams(from)));
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

	private static void assertWillSaveTextDocumentParams(WillSaveTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toWillSaveTextDocumentParams(ConverterUtils.toWillSaveTextDocumentParams(from)));
	}

	private static void assertWillSaveTextDocumentParams(org.eclipse.lsp4j.WillSaveTextDocumentParams from) {
		assertObjects(from, ConverterUtils.toWillSaveTextDocumentParams(ConverterUtils.toWillSaveTextDocumentParams(from)));
	}

	private static void assertMarkupContent(MarkupContent from) {
		assertObjects(from, ConverterUtils.toMarkupContent(ConverterUtils.toMarkupContent(from)));
	}

	private static void assertMarkupContent(org.eclipse.lsp4j.MarkupContent from) {
		assertObjects(from, ConverterUtils.toMarkupContent(ConverterUtils.toMarkupContent(from)));
	}

	private static void assertHover(Hover from) {
		assertObjects(from, ConverterUtils.toHover(ConverterUtils.toHover(from)));
	}

	private static void assertHover(org.eclipse.lsp4j.Hover from) {
		assertObjects(from, ConverterUtils.toHover(ConverterUtils.toHover(from)));
	}

	private static void assertCompletionContext(CompletionContext from) {
		assertObjects(from, ConverterUtils.toCompletionContext(ConverterUtils.toCompletionContext(from)));
	}

	private static void assertCompletionContext(org.eclipse.lsp4j.CompletionContext from) {
		assertObjects(from, ConverterUtils.toCompletionContext(ConverterUtils.toCompletionContext(from)));
	}

	private static void assertCompletionParams(CompletionParams from) {
		assertObjects(from, ConverterUtils.toCompletionParams(ConverterUtils.toCompletionParams(from)));
	}

	private static void assertCompletionParams(org.eclipse.lsp4j.CompletionParams from) {
		assertObjects(from, ConverterUtils.toCompletionParams(ConverterUtils.toCompletionParams(from)));
	}

	private static void assertPosition(Position from) {
		assertObjects(from, ConverterUtils.toPosition(ConverterUtils.toPosition(from)));
	}

	private static void assertPosition(org.eclipse.lsp4j.Position from) {
		assertObjects(from, ConverterUtils.toPosition(ConverterUtils.toPosition(from)));
	}

	private static void assertRange(Range from) {
		assertObjects(from, ConverterUtils.toRange(ConverterUtils.toRange(from)));
	}

	private static void assertRange(org.eclipse.lsp4j.Range from) {
		assertObjects(from, ConverterUtils.toRange(ConverterUtils.toRange(from)));
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

	private static void assertTextEdit(TextEdit from) {
		assertObjects(from, ConverterUtils.toTextEdit(ConverterUtils.toTextEdit(from)));
	}

	private static void assertTextEdit(org.eclipse.lsp4j.TextEdit from) {
		assertObjects(from, ConverterUtils.toTextEdit(ConverterUtils.toTextEdit(from)));
	}

	private static void assertCommand(Command from) {
		assertObjects(from, ConverterUtils.toCommand(ConverterUtils.toCommand(from)));
	}

	private static void assertCommand(org.eclipse.lsp4j.Command from) {
		assertObjects(from, ConverterUtils.toCommand(ConverterUtils.toCommand(from)));
	}

	private static void assertCompletionItem(CompletionItem from) {
		assertObjects(from, ConverterUtils.toCompletionItem(ConverterUtils.toCompletionItem(from)));
	}

	private static void assertCompletionItem(org.eclipse.lsp4j.CompletionItem from) {
		assertObjects(from, ConverterUtils.toCompletionItem(ConverterUtils.toCompletionItem(from)));
	}

	private static void assertCompletionOptions(CompletionOptions from) {
		assertObjects(from, ConverterUtils.toCompletionOptions(ConverterUtils.toCompletionOptions(from)));
	}

	private static void assertCompletionOptions(org.eclipse.lsp4j.CompletionOptions from) {
		assertObjects(from, ConverterUtils.toCompletionOptions(ConverterUtils.toCompletionOptions(from)));
	}

	private static void assertPublishDiagnosticsParams(PublishDiagnosticsParams from) {
		assertObjects(from, ConverterUtils.toPublishDiagnosticsParams(ConverterUtils.toPublishDiagnosticsParams(from)));
	}

	private static void assertPublishDiagnosticsParams(org.eclipse.lsp4j.PublishDiagnosticsParams from) {
		assertObjects(from, ConverterUtils.toPublishDiagnosticsParams(ConverterUtils.toPublishDiagnosticsParams(from)));
	}

	private static void assertTextDocumentContentChangeEvent(TextDocumentContentChangeEvent from) {
		assertObjects(from, ConverterUtils.toTextDocumentContentChangeEvent(ConverterUtils.toTextDocumentContentChangeEvent(from)));
	}

	private static void assertTextDocumentContentChangeEvent(org.eclipse.lsp4j.TextDocumentContentChangeEvent from) {
		assertObjects(from, ConverterUtils.toTextDocumentContentChangeEvent(ConverterUtils.toTextDocumentContentChangeEvent(from)));
	}

	private static void assertObjects(Object from, Object to) {
		assertThat(from).isNotSameAs(to);
		assertThat(from).isEqualTo(to);
		assertThat(from).isEqualToComparingFieldByField(to);
	}
}
