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
package org.springframework.dsl.lsp.server.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dsl.domain.ClientCapabilities;
import org.springframework.dsl.domain.CompletionItem;
import org.springframework.dsl.domain.CompletionItemKind;
import org.springframework.dsl.domain.CompletionList;
import org.springframework.dsl.domain.CompletionOptions;
import org.springframework.dsl.domain.Diagnostic;
import org.springframework.dsl.domain.DiagnosticSeverity;
import org.springframework.dsl.domain.DynamicRegistration;
import org.springframework.dsl.domain.InitializeResult;
import org.springframework.dsl.domain.InsertTextFormat;
import org.springframework.dsl.domain.MarkupContent;
import org.springframework.dsl.domain.MarkupKind;
import org.springframework.dsl.domain.MessageActionItem;
import org.springframework.dsl.domain.MessageParams;
import org.springframework.dsl.domain.MessageType;
import org.springframework.dsl.domain.PublishDiagnosticsParams;
import org.springframework.dsl.domain.ServerCapabilities;
import org.springframework.dsl.domain.ShowMessageRequestParams;
import org.springframework.dsl.domain.Synchronization;
import org.springframework.dsl.domain.TextDocumentClientCapabilities;
import org.springframework.dsl.domain.TextDocumentSyncKind;
import org.springframework.dsl.domain.TextDocumentSyncOptions;
import org.springframework.dsl.domain.TextEdit;
import org.springframework.dsl.lsp.server.config.LspDomainJacksonConfiguration;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public class LspDomainJacksonSerializationTests {

	private ObjectMapper mapper;

	@Before
	public void setup() {
		LspDomainJacksonConfiguration configuration = new LspDomainJacksonConfiguration();
		this.mapper = configuration.lspObjectMapper();
	}

	@After
	public void clean() {
		mapper = null;
	}

	@Test
	public void testCompletionOptions() throws Exception {
		CompletionOptions from = new CompletionOptions();
		String json = mapper.writeValueAsString(from);
		CompletionOptions to = mapper.readValue(json, CompletionOptions.class);
		assertObjects(from, to);

		from = CompletionOptions.completionOptions()
			.resolveProvider(true)
			.triggerCharacters(Arrays.asList("a", "b"))
			.build();
		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, CompletionOptions.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("CompletionOptions1.json");
		to = mapper.readValue(expect, CompletionOptions.class);
		assertObjects(from, to);
	}

	@Test
	public void testTextDocumentSyncOptions() throws Exception {
		TextDocumentSyncOptions from = new TextDocumentSyncOptions();
		String json = mapper.writeValueAsString(from);
		TextDocumentSyncOptions to = mapper.readValue(json, TextDocumentSyncOptions.class);
		assertObjects(from, to);

		from = TextDocumentSyncOptions.textDocumentSyncOptions()
				.openClose(true)
				.change(TextDocumentSyncKind.Incremental)
				.willSave(true)
				.willSaveWaitUntil(true)
				.build();

		json = mapper.writeValueAsString(from);
		Integer change = JsonPath.read(json, "change");
		assertThat(change).isEqualTo(2);

		to = mapper.readValue(json, TextDocumentSyncOptions.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("TextDocumentSyncOptions1.json");
		to = mapper.readValue(expect, TextDocumentSyncOptions.class);
		assertObjects(from, to);
	}

	@Test
	public void testServerCapabilities() throws Exception {
		ServerCapabilities from = new ServerCapabilities();
		String json = mapper.writeValueAsString(from);
		ServerCapabilities to = mapper.readValue(json, ServerCapabilities.class);
		assertObjects(from, to);

		from = ServerCapabilities.serverCapabilities()
				.textDocumentSyncOptions()
					.openClose(true)
					.willSave(true)
					.willSaveWaitUntil(true)
					.and()
				.hoverProvider(true)
				.completionProvider()
					.resolveProvider(true)
					.triggerCharacters(Arrays.asList("a", "b"))
					.and()
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, ServerCapabilities.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("ServerCapabilities1.json");
		to = mapper.readValue(expect, ServerCapabilities.class);
		assertObjects(from, to);

		from = ServerCapabilities.serverCapabilities()
				.textDocumentSyncKind(TextDocumentSyncKind.Incremental)
				.hoverProvider(true)
				.completionProvider()
					.resolveProvider(true)
					.triggerCharacters(Arrays.asList("a", "b"))
					.and()
				.build();
		expect = loadResourceAsString("ServerCapabilities2.json");
		to = mapper.readValue(expect, ServerCapabilities.class);
		assertObjects(from, to);
	}

	@Test
	public void testInitializeResult() throws Exception {
		InitializeResult from = new InitializeResult();
		String json = mapper.writeValueAsString(from);
		InitializeResult to = mapper.readValue(json, InitializeResult.class);
		assertObjects(from, to);

		from = InitializeResult.initializeResult()
				.capabilities()
					.textDocumentSyncOptions()
						.openClose(true)
						.willSave(true)
						.willSaveWaitUntil(true)
						.and()
					.hoverProvider(true)
					.completionProvider()
						.resolveProvider(true)
						.triggerCharacters(Arrays.asList("a", "b"))
						.and()
					.and()
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, InitializeResult.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("InitializeResult1.json");
		to = mapper.readValue(expect, InitializeResult.class);
		assertObjects(from, to);
	}

	@Test
	public void testDiagnostic() throws Exception {
		Diagnostic from = new Diagnostic();
		String json = mapper.writeValueAsString(from);
		Diagnostic to = mapper.readValue(json, Diagnostic.class);
		assertObjects(from, to);

		from = Diagnostic.diagnostic()
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
				.severity(DiagnosticSeverity.Error)
				.code("code")
				.source("source")
				.message("message")
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, Diagnostic.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("Diagnostic1.json");
		to = mapper.readValue(expect, Diagnostic.class);
		assertObjects(from, to);
	}

	@Test
	public void testPublishDiagnosticsParams() throws Exception {
		PublishDiagnosticsParams from = new PublishDiagnosticsParams();
		String json = mapper.writeValueAsString(from);
		PublishDiagnosticsParams to = mapper.readValue(json, PublishDiagnosticsParams.class);
		assertObjects(from, to);

		from = PublishDiagnosticsParams.publishDiagnosticsParams()
				.uri("uri")
				.diagnostic()
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
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, PublishDiagnosticsParams.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("PublishDiagnosticsParams1.json");
		to = mapper.readValue(expect, PublishDiagnosticsParams.class);
		assertObjects(from, to);

		from = PublishDiagnosticsParams.publishDiagnosticsParams()
				.uri("uri")
				.diagnostic()
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
				.diagnostic()
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

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, PublishDiagnosticsParams.class);
		assertObjects(from, to);

		expect = loadResourceAsString("PublishDiagnosticsParams2.json");
		to = mapper.readValue(expect, PublishDiagnosticsParams.class);
		assertObjects(from, to);
	}

	@Test
	public void testDynamicRegistration() throws Exception {
		DynamicRegistration from = new DynamicRegistration();
		String json = mapper.writeValueAsString(from);
		DynamicRegistration to = mapper.readValue(json, DynamicRegistration.class);
		assertObjects(from, to);

		from = DynamicRegistration.dynamicRegistration()
				.dynamicRegistration(true)
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, DynamicRegistration.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("DynamicRegistration1.json");
		to = mapper.readValue(expect, DynamicRegistration.class);
		assertObjects(from, to);
	}

	@Test
	public void testSynchronization() throws Exception {
		Synchronization from = new Synchronization();
		String json = mapper.writeValueAsString(from);
		Synchronization to = mapper.readValue(json, Synchronization.class);
		assertObjects(from, to);

		from = Synchronization.synchronization()
				.dynamicRegistration(true)
				.willSave(true)
				.willSaveWaitUntil(true)
				.didSave(true)
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, Synchronization.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("Synchronization1.json");
		to = mapper.readValue(expect, Synchronization.class);
		assertObjects(from, to);

		from = new Synchronization(true, true, true, false);
		to = new Synchronization(true, true, true, true);
		assertThat(from).isNotEqualTo(to);
		from = new Synchronization(true, true, true, false);
		to = new Synchronization(false, true, true, false);
		assertThat(from).isNotEqualTo(to);
	}

	@Test
	public void testTextDocumentClientCapabilities() throws Exception {
		TextDocumentClientCapabilities from = new TextDocumentClientCapabilities();
		String json = mapper.writeValueAsString(from);
		TextDocumentClientCapabilities to = mapper.readValue(json, TextDocumentClientCapabilities.class);
		assertObjects(from, to);

		from = TextDocumentClientCapabilities.textDocumentClientCapabilities()
				.synchronization()
					.dynamicRegistration(true)
					.willSave(true)
					.willSaveWaitUntil(true)
					.didSave(true)
					.and()
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, TextDocumentClientCapabilities.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("TextDocumentClientCapabilities1.json");
		to = mapper.readValue(expect, TextDocumentClientCapabilities.class);
		assertObjects(from, to);
	}

	@Test
	public void testClientCapabilities() throws Exception {
		ClientCapabilities from = new ClientCapabilities();
		String json = mapper.writeValueAsString(from);
		ClientCapabilities to = mapper.readValue(json, ClientCapabilities.class);
		assertObjects(from, to);

		from = ClientCapabilities.clientCapabilities()
				.experimental("experimental")
				.textDocument()
					.synchronization()
						.dynamicRegistration(true)
						.willSave(true)
						.willSaveWaitUntil(true)
						.didSave(true)
						.and()
					.and()
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, ClientCapabilities.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("ClientCapabilities1.json");
		to = mapper.readValue(expect, ClientCapabilities.class);
		assertObjects(from, to);
	}

	@Test
	public void testMarkupContent() throws Exception {
		MarkupContent from = new MarkupContent();
		String json = mapper.writeValueAsString(from);
		MarkupContent to = mapper.readValue(json, MarkupContent.class);
		assertObjects(from, to);

		from = MarkupContent.markupContent()
				.kind(MarkupKind.markdown)
				.value("value")
				.build();

		json = mapper.writeValueAsString(from);
		String kind = JsonPath.read(json, "kind");
		assertThat(kind).isEqualTo("markdown");

		to = mapper.readValue(json, MarkupContent.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("MarkupContent1.json");
		to = mapper.readValue(expect, MarkupContent.class);
		assertObjects(from, to);
	}

	@Test
	public void testTextEdit() throws Exception {
		TextEdit from = new TextEdit();
		String json = mapper.writeValueAsString(from);
		TextEdit to = mapper.readValue(json, TextEdit.class);
		assertObjects(from, to);

		from = TextEdit.textEdit()
				.range()
					.start()
						.line(0)
						.character(0)
						.and()
					.end()
						.line(1)
						.character(1)
						.and()
					.and()
				.newText("newText")
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, TextEdit.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("TextEdit1.json");
		to = mapper.readValue(expect, TextEdit.class);
		assertObjects(from, to);
	}

	@Test
	public void testCompletionItem() throws Exception {
		CompletionItem from = new CompletionItem();
		String json = mapper.writeValueAsString(from);
		CompletionItem to = mapper.readValue(json, CompletionItem.class);
		assertObjects(from, to);

		from = CompletionItem.completionItem()
				.label("label")
				.kind(CompletionItemKind.Class)
				.detail("detail")
				.documentation()
					.kind(MarkupKind.markdown)
					.value("value")
					.and()
				.sortText("sortText")
				.filterText("filterText")
				.insertText("insertText")
				.insertTextFormat(InsertTextFormat.PlainText)
				.textEdit()
					.range()
						.start()
							.line(0)
							.character(0)
							.and()
						.end()
							.line(1)
							.character(1)
							.and()
						.and()
					.newText("newText")
					.and()
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, CompletionItem.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("CompletionItem1.json");
		to = mapper.readValue(expect, CompletionItem.class);
		assertObjects(from, to);
	}

	@Test
	public void testCompletionList() throws Exception {
		CompletionList from = new CompletionList();
		String json = mapper.writeValueAsString(from);
		CompletionList to = mapper.readValue(json, CompletionList.class);
		assertObjects(from, to);

		from = CompletionList.completionList()
				.isIncomplete(true)
				.item()
					.label("label1")
					.and()
				.item()
					.label("label2")
					.and()
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, CompletionList.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("CompletionList1.json");
		to = mapper.readValue(expect, CompletionList.class);
		assertObjects(from, to);
	}

	@Test
	public void testMessageActionItem() throws Exception {
		MessageActionItem from = new MessageActionItem();
		String json = mapper.writeValueAsString(from);
		MessageActionItem to = mapper.readValue(json, MessageActionItem.class);
		assertObjects(from, to);

		from = MessageActionItem.messageActionItem()
				.title("title")
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, MessageActionItem.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("MessageActionItem1.json");
		to = mapper.readValue(expect, MessageActionItem.class);
		assertObjects(from, to);
	}

	@Test
	public void testMessageParams() throws Exception {
		MessageParams from = new MessageParams();
		String json = mapper.writeValueAsString(from);
		MessageParams to = mapper.readValue(json, MessageParams.class);
		assertObjects(from, to);

		from = MessageParams.messageParams()
				.type(MessageType.Info)
				.message("message")
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, MessageParams.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("MessageParams1.json");
		to = mapper.readValue(expect, MessageParams.class);
		assertObjects(from, to);
	}

	@Test
	public void testShowMessageRequestParams() throws Exception {
		ShowMessageRequestParams from = new ShowMessageRequestParams();
		String json = mapper.writeValueAsString(from);
		ShowMessageRequestParams to = mapper.readValue(json, ShowMessageRequestParams.class);
		assertObjects(from, to);

		from = ShowMessageRequestParams.showMessageRequestParams()
				.action()
					.title("title")
					.and()
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, ShowMessageRequestParams.class);
		assertObjects(from, to);

		String expect = loadResourceAsString("ShowMessageRequestParams1.json");
		to = mapper.readValue(expect, ShowMessageRequestParams.class);
		assertObjects(from, to);

		from = ShowMessageRequestParams.showMessageRequestParams()
				.action()
					.title("title1")
					.and()
				.action()
					.title("title2")
					.and()
				.build();

		json = mapper.writeValueAsString(from);
		to = mapper.readValue(json, ShowMessageRequestParams.class);
		assertObjects(from, to);

		expect = loadResourceAsString("ShowMessageRequestParams2.json");
		to = mapper.readValue(expect, ShowMessageRequestParams.class);
		assertObjects(from, to);
	}

	private static String loadResourceAsString(String resource) throws IOException {
		return loadResourceAsString(new ClassPathResource("org/springframework/dsl/lsp/server/domain/" + resource));
	}

	private static String loadResourceAsString(Resource resource) throws IOException {
		return StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
	}

	private static void assertObjects(Object from, Object to) {
		assertThat(from).isNotSameAs(to);
		assertThat(from).isEqualTo(to);
		assertThat(from).isEqualToComparingFieldByField(to);
	}
}