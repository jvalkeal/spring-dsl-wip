package org.springframework.dsl.lsp4j;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.eclipse.lsp4j.InitializeParams;
//import org.eclipse.lsp4j.InitializeResult;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dsl.lsp.LanguageClientContext;
import org.springframework.dsl.lsp.annotation.LspController;
import org.springframework.dsl.lsp.annotation.LspDidChange;
import org.springframework.dsl.lsp.annotation.LspInitialize;
import org.springframework.dsl.lsp.annotation.LspResponseBody;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.server.config.EnableLanguageServer;
import org.springframework.dsl.lsp.server.support.DispatcherHandler;
import org.springframework.dsl.lsp4j.LanguageServerAdapter;

public class Lsp4jTests extends AbstractLspTests {

	@Test
	public void testInit() throws Exception {
		context.register(Config1.class, TestLanguageServerController1.class);
		context.refresh();

		DispatcherHandler dispatcherHandler = context.getBean(DispatcherHandler.class);

		LanguageServerAdapter languageServerAdapter = new LanguageServerAdapter(dispatcherHandler);

		org.eclipse.lsp4j.InitializeParams lsp4jInitializeParams = new org.eclipse.lsp4j.InitializeParams();
		CompletableFuture<org.eclipse.lsp4j.InitializeResult> lsp4jInitializeResultFuture = languageServerAdapter.initialize(lsp4jInitializeParams);

		org.eclipse.lsp4j.InitializeResult lsp4jInitializeResult = lsp4jInitializeResultFuture.get(1, TimeUnit.SECONDS);
		assertThat(lsp4jInitializeResult, notNullValue());

	}

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}

	@EnableLanguageServer
	private static class Config1 {
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
	}

}
