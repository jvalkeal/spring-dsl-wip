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
package org.springframework.dsl.lsp4j;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

//import org.eclipse.lsp4j.InitializeParams;
//import org.eclipse.lsp4j.InitializeResult;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.dsl.lsp.LanguageClientContext;
import org.springframework.dsl.lsp.annotation.LspController;
import org.springframework.dsl.lsp.annotation.LspDidChange;
import org.springframework.dsl.lsp.annotation.LspInitialize;
import org.springframework.dsl.lsp.annotation.LspResponseBody;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.TextDocumentContentChangeEvent;
import org.springframework.dsl.lsp.model.Document;
import org.springframework.dsl.lsp.server.config.EnableLanguageServer;
import org.springframework.dsl.lsp.server.support.DispatcherHandler;
import org.springframework.dsl.lsp4j.converter.GenericLsp4jObjectConverter;
import org.springframework.dsl.lsp4j.result.method.annotation.Lsp4jDomainArgumentResolver;

import reactor.core.publisher.Mono;

/**
 * Tests for generic functionality around {@link LanguageServerAdapter}.
 *
 * @author Janne Valkealahti
 *
 */
public class LanguageServerAdapterTests extends AbstractLspTests {

	@Test
	public void testInit() throws Exception {
		context.register(Config1.class, TestLanguageServerController1.class);
		context.refresh();

		DispatcherHandler dispatcherHandler = context.getBean(DispatcherHandler.class);
		ConversionService conversionService = context.getBean(ConversionService.class);

		LanguageServerAdapter languageServerAdapter = new LanguageServerAdapter(dispatcherHandler, conversionService);

		org.eclipse.lsp4j.InitializeParams lsp4jInitializeParams = new org.eclipse.lsp4j.InitializeParams();
		lsp4jInitializeParams.setProcessId(1);
		CompletableFuture<org.eclipse.lsp4j.InitializeResult> lsp4jInitializeResultFuture = languageServerAdapter.initialize(lsp4jInitializeParams);

		org.eclipse.lsp4j.InitializeResult lsp4jInitializeResult = lsp4jInitializeResultFuture.get(3, TimeUnit.SECONDS);
		assertThat(lsp4jInitializeResult, notNullValue());

	}

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}

	@EnableLanguageServer
	private static class Config1 {

		@Bean
		public ConversionServiceFactoryBean lspConversionService() {
			ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
			Set<Object> converters = new HashSet<>();
			converters.add(new GenericLsp4jObjectConverter());
			factoryBean.setConverters(converters);
			return factoryBean;
		}


		@Bean
		public Lsp4jDomainArgumentResolver lsp4jDomainArgumentResolver() {
			return new Lsp4jDomainArgumentResolver();
		}
	}

	@LspController
	private static class TestLanguageServerController1 {

		@LspInitialize
		@LspResponseBody
		public InitializeResult clientInit(InitializeParams params) {
			return new InitializeResult();
		}

		@LspDidChange
		public void clientDocumentChanged(DidChangeTextDocumentParams params, LanguageClientContext context) {
		}

		// @OnDocumentChange
		public void xxx(Document document, List<TextDocumentContentChangeEvent> changes) {
		}
	}
}
