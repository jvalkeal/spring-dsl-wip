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
package org.springframework.dsl.lsp.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dsl.lsp.LspClientContext;
import org.springframework.dsl.lsp.annotation.LspController;
import org.springframework.dsl.lsp.annotation.LspDidChange;
import org.springframework.dsl.lsp.annotation.LspDidClose;
import org.springframework.dsl.lsp.annotation.LspDidOpen;
import org.springframework.dsl.lsp.annotation.LspDidSave;
import org.springframework.dsl.lsp.annotation.LspInitialize;
import org.springframework.dsl.lsp.annotation.LspNoResponseBody;
import org.springframework.dsl.lsp.annotation.LspResponseBody;
import org.springframework.dsl.lsp.domain.CompletionOptions;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.domain.TextDocumentSyncKind;
import org.springframework.dsl.lsp.model.Document;
import org.springframework.dsl.lsp.service.Completioner;
import org.springframework.dsl.lsp.service.Hoverer;
import org.springframework.dsl.lsp.service.Reconciler;

/**
 * A generic {@code LspController} implementation providing common higher level
 * features what a {@code Language Server} should provide.
 * <p>
 * We extensively use {@link ObjectProvider} to see if particular services are
 * defined in an {@code Application Context}. This allows us to prepare a
 * response to {@code LSP Client}'s {@code initialize} request based on defined
 * services.
 * <p>
 * There are services in a context of a {@code LSP} which alter a response
 * message to {@code LSP Client}'s {@code initialize} request, while some
 * services are optional. For example {@code Spring DSL} provides higher level
 * {@link Reconciler} service which is not required by a {@code LSP} but will
 * respond to {@code Client}'s {@code LspDidChange} requests by providing
 * diagnostics back to a client. As {@code LspDidChange} is a one-way request,
 * response is not needed but server may response multiple diagnostics messages
 * back to a client to i.e. to tell what is wrong in a current {@link Document}.
 *
 * @author Janne Valkealahti
 *
 */
@LspController
public class GenericLanguageServerController {

	private final ObjectProvider<Reconciler> reconcilerProvider;
	private final ObjectProvider<Completioner> completionerProvider;
	private final ObjectProvider<Hoverer> hovererProvider;

	public GenericLanguageServerController(ObjectProvider<Reconciler> reconcilerProvider,
			ObjectProvider<Completioner> completionerProvider, ObjectProvider<Hoverer> hovererProvider) {
		this.reconcilerProvider = reconcilerProvider;
		this.completionerProvider = completionerProvider;
		this.hovererProvider = hovererProvider;
	}

	@LspInitialize
	@LspResponseBody
	public InitializeResult clientInit(InitializeParams params) {
		ServerCapabilities serverCapabilities = new ServerCapabilities();
		serverCapabilities.setHoverProvider(hovererProvider.getIfAvailable() != null);
		serverCapabilities.setTextDocumentSyncKind(TextDocumentSyncKind.Full);
		if (completionerProvider.getIfAvailable() != null) {
			serverCapabilities.setCompletionProvider(new CompletionOptions());
		}
		return new InitializeResult(serverCapabilities);
	}

	@LspDidOpen
	@LspNoResponseBody
	public void clientDocumentOpened(DidOpenTextDocumentParams params) {
	}

	@LspDidChange
	@LspNoResponseBody
	public void clientDocumentChanged(DidChangeTextDocumentParams params, LspClientContext context) {
		Reconciler reconciler = reconcilerProvider.getIfAvailable();
		if (reconciler != null) {

		}
	}

	@LspDidClose
	@LspNoResponseBody
	public void clientDocumentClosed(DidCloseTextDocumentParams params) {
	}

	@LspDidSave
	@LspNoResponseBody
	public void clientDocumentSaved(DidSaveTextDocumentParams params) {
	}
}
