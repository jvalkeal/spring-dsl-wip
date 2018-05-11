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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentFormattingParams;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams;
import org.eclipse.lsp4j.DocumentRangeFormattingParams;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.RenameParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.dsl.lsp.LspClient;
import org.springframework.dsl.lsp.LspClientContext;
import org.springframework.dsl.lsp.LspMethod;
import org.springframework.dsl.lsp.domain.PublishDiagnosticsParams;
import org.springframework.dsl.lsp.server.LspHandler;
import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.dsl.lsp.server.support.DefaultServerCoapExchange;
import org.springframework.dsl.lsp.server.support.GenericServerLspRequest;
import org.springframework.dsl.lsp.server.support.GenericServerLspResponse;
import org.springframework.util.Assert;

import reactor.core.publisher.Mono;

/**
 * Adapter implementing {@code LSP4J} {@link LanguageServer} and dispatching
 * operations to {@link LspHandler}.
 * <p>
 * There are two types of methods called from {@code LSP4J}, firstly a simple
 * request/response methods where request always expects an response, secondly
 * request methods which server may optionally response something.
 * <p>
 * This adapter implementation is used to hide all {@code LSP4J} api methods so
 * that we're able to fully work with our own {@code LSP} classes. Essentially
 * we don't want to expose or require implementors to use any {@code LSP4J}
 * classes giving a change to replace {@code LSP4J} without any changes in a
 * user level code.
 *
 * @author Janne Valkealahti
 *
 */
public class Lsp4jLanguageServerAdapter implements LanguageServer, LanguageClientAware {

	private static final Logger log = LoggerFactory.getLogger(Lsp4jLanguageServerAdapter.class);

	private final LspHandler lspHandler;
	private final ConversionService conversionService;
	private LanguageClient client;

	/**
	 * Instantiates a new lsp4j language server adapter.
	 *
	 * @param lspHandler the lsp handler
	 * @param conversionService the conversion service
	 */
	public Lsp4jLanguageServerAdapter(LspHandler lspHandler, ConversionService conversionService) {
		Assert.notNull(lspHandler, "lspHandler must be set");
		Assert.notNull(conversionService, "conversionService must be set");
		this.lspHandler = lspHandler;
		this.conversionService = conversionService;
	}

	@Override
	public void connect(LanguageClient client) {
		this.client = client;
	}

	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
		log.trace("initialize {}", params);
		ServerLspExchange exchange = createExchange(LspMethod.INITIALIZE, params);
		return lspHandler.handle(exchange)
				.then(convert(exchange, InitializeResult.class, conversionService))
				.toFuture();
	}

	@Override
	public void initialized(InitializedParams params) {
		log.trace("initialized {}", params);
		lspHandler.handle(createExchange(LspMethod.INITIALIZED, params, client, conversionService)).subscribe();
	}

	@Override
	public CompletableFuture<Object> shutdown() {
		log.trace("shutdown");
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public void exit() {
		log.trace("exit");
		// TODO: if we're in a process mode, this should exit jvm. If we're in embedded mode, like websocket
		//       should not do anything.
	}

	@Override
	public TextDocumentService getTextDocumentService() {
		return new TextDocumentService() {

			@Override
			public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
				log.trace("hover request {}", position);
				ServerLspExchange exchange = createExchange(LspMethod.TEXTDOCUMENT_HOVER, position, client, conversionService);
				return lspHandler.handle(exchange)
						.then(convert(exchange, Hover.class, conversionService))
						.doOnNext(response -> {
							log.trace("hover response {}", response);
						})
						.toFuture();
			}

			@Override
			public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(DocumentSymbolParams params) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams position) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void didSave(DidSaveTextDocumentParams params) {
				log.trace("didSave {}", params);
				lspHandler.handle(createExchange(LspMethod.DIDSAVE, params, client, conversionService)).subscribe();
			}

			@Override
			public void didOpen(DidOpenTextDocumentParams params) {
				log.trace("didOpen {}", params);
				lspHandler.handle(createExchange(LspMethod.DIDOPEN, params, client, conversionService)).subscribe();
			}

			@Override
			public void didClose(DidCloseTextDocumentParams params) {
				log.trace("didClose {}", params);
				lspHandler.handle(createExchange(LspMethod.DIDCLOSE, params, client, conversionService)).subscribe();
			}

			@Override
			public void didChange(DidChangeTextDocumentParams params) {
				log.trace("didChange {}", params);
				lspHandler.handle(createExchange(LspMethod.DIDCHANGE, params, client, conversionService)).subscribe();
			}

			@Override
			public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams position) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
					CompletionParams params) {
				log.trace("completion request {}", params);
				ServerLspExchange exchange = createExchange(LspMethod.TEXTDOCUMENT_COMPLETION, params, client, conversionService);
				return lspHandler.handle(exchange)
					.then(convert2(exchange, CompletionItem.class, conversionService))
					.flatMap(list -> Mono.just(Either.<List<CompletionItem>, CompletionList>forRight(new CompletionList(list))))
					.doOnNext(response -> {
						log.trace("completion response {}", response);
					})
					.toFuture();
			}

			@Override
			public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CompletableFuture<List<? extends Command>> codeAction(CodeActionParams params) {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public WorkspaceService getWorkspaceService() {
		log.trace("getWorkspaceService");
		return null;
	}

	private static ServerLspExchange createExchange(LspMethod lspMethod, Object body) {
		return createExchange(lspMethod, body, null, null);
	}

	private static ServerLspExchange createExchange(LspMethod lspMethod, Object body, LanguageClient languageClient,
			ConversionService conversionService) {
		GenericServerLspRequest request = new GenericServerLspRequest(body);

		if (languageClient != null) {
			request.setContext(new Lsp4jLspClientContext(languageClient, conversionService));
		}

		request.setMethod(lspMethod);
		GenericServerLspResponse response = new GenericServerLspResponse();
		return new DefaultServerCoapExchange(request, response);
	}

	private static <T> Mono<T> convert(ServerLspExchange exchange, Class<T> clazz, ConversionService conversionService) {
//		return Mono.fromSupplier(() -> conversionService.convert(exchange.getResponse().getBody(), clazz));
		return Mono.fromSupplier(() -> {
			Object[] bodys = exchange.getResponse().getBody();
			if (bodys.length > 0) {
				return conversionService.convert(bodys[0], clazz);
			} else {
				return null;
			}
			});
	}

	private static <T> Mono<List<T>> convert2(ServerLspExchange exchange, Class<T> clazz, ConversionService conversionService) {
		return Mono.fromSupplier(() -> {
			ArrayList<T> list = new ArrayList<>();
			for (Object body : exchange.getResponse().getBody()) {
				list.add(conversionService.convert(body, clazz));
			}
			return list;
		});
	}

	private static class Lsp4jLspClientContext implements LspClientContext {

		private final LanguageClient client;
		private final ConversionService conversionService;

		public Lsp4jLspClientContext(LanguageClient client, ConversionService conversionService) {
			this.client = client;
			this.conversionService = conversionService;
		}

		@Override
		public LspClient getClient() {
			return new LspClient() {

				@Override
				public Mono<Void> send(PublishDiagnosticsParams diagnostics) {
					client.publishDiagnostics(
							conversionService.convert(diagnostics, org.eclipse.lsp4j.PublishDiagnosticsParams.class));
					return Mono.empty();
				}

			};
		}

	}
}
