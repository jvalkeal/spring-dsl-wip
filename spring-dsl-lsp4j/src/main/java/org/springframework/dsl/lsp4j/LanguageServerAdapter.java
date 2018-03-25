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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
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
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.RenameParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentPositionParams;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.springframework.dsl.lsp.LspMethod;
import org.springframework.dsl.lsp.server.LspHandler;
import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.dsl.lsp.server.support.DefaultServerCoapExchange;
import org.springframework.dsl.lsp.server.support.GenericServerLspRequest;
import org.springframework.dsl.lsp.server.support.GenericServerLspResponse;
import org.springframework.util.Assert;

import reactor.core.publisher.Mono;

public class LanguageServerAdapter implements LanguageServer {

	private final LspHandler lspHandler;

	public LanguageServerAdapter(LspHandler lspHandler) {
		Assert.notNull(lspHandler, "lspHandler must be set");
		this.lspHandler = lspHandler;
	}

	private ServerLspExchange createExchange(LspMethod lspMethod) {

		GenericServerLspRequest request = new GenericServerLspRequest(null);
		request.setMethod(lspMethod);

		GenericServerLspResponse response = new GenericServerLspResponse();
		return new DefaultServerCoapExchange(request, response);
	}

	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {

		ServerLspExchange exchange = createExchange(LspMethod.INITIALIZE);

		Mono<Void> handle = lspHandler.handle(exchange);

		CompletableFuture<InitializeResult> completableFuture =
				handle.flatMap(f -> Mono.just(new InitializeResult()))
			.doOnSuccess(c -> {
			}).toFuture();


		return completableFuture;
	}

	@Override
	public CompletableFuture<Object> shutdown() {
		return null;
	}

	@Override
	public void exit() {
	}

	@Override
	public TextDocumentService getTextDocumentService() {
		return new TextDocumentService() {

			@Override
			public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(DocumentSymbolParams params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(TextDocumentPositionParams position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void didSave(DidSaveTextDocumentParams params) {
				// TODO Auto-generated method stub

			}

			@Override
			public void didOpen(DidOpenTextDocumentParams params) {
				// TODO Auto-generated method stub

			}

			@Override
			public void didClose(DidCloseTextDocumentParams params) {
				// TODO Auto-generated method stub

			}

			@Override
			public void didChange(DidChangeTextDocumentParams params) {

			}

			@Override
			public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
					TextDocumentPositionParams position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<List<? extends Command>> codeAction(CodeActionParams params) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
	public WorkspaceService getWorkspaceService() {
		return null;
	}

//	private void handleRequest() {
//
//		ServerLspExchange exchange = createExchange();
//
//		Mono<Void> handle = lspHandler.handle(exchange);
//
//		handle.onErrorResume(e -> {
//			return Mono.empty();
//		}).doOnSuccess(c -> {
//
//		}).subscribe();
//	}


}
