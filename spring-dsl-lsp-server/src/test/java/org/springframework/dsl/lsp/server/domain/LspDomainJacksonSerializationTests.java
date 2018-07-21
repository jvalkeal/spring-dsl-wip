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
import org.springframework.dsl.lsp.domain.CompletionOptions;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.domain.TextDocumentSyncKind;
import org.springframework.dsl.lsp.domain.TextDocumentSyncOptions;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jayway.jsonpath.JsonPath;

public class LspDomainJacksonSerializationTests {
	
	private ObjectMapper mapper;
	
	@Before
	public void setup() {
		SimpleModule module = new SimpleModule();
		module.addSerializer(ServerCapabilities.class, new ServerCapabilitiesJsonSerializer());
		module.addDeserializer(ServerCapabilities.class, new ServerCapabilitiesJsonDeserializer());
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
		this.mapper = mapper;
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
