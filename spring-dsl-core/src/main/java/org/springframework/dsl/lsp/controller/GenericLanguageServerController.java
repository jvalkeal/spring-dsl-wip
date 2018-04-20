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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.lsp.LspClientContext;
import org.springframework.dsl.lsp.annotation.LspCompletion;
import org.springframework.dsl.lsp.annotation.LspController;
import org.springframework.dsl.lsp.annotation.LspDidChange;
import org.springframework.dsl.lsp.annotation.LspDidClose;
import org.springframework.dsl.lsp.annotation.LspDidOpen;
import org.springframework.dsl.lsp.annotation.LspDidSave;
import org.springframework.dsl.lsp.annotation.LspHover;
import org.springframework.dsl.lsp.annotation.LspInitialize;
import org.springframework.dsl.lsp.annotation.LspNoResponseBody;
import org.springframework.dsl.lsp.annotation.LspResponseBody;
import org.springframework.dsl.lsp.domain.CompletionItem;
import org.springframework.dsl.lsp.domain.CompletionList;
import org.springframework.dsl.lsp.domain.CompletionOptions;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.Hover;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.domain.TextDocumentPositionParams;
import org.springframework.dsl.lsp.domain.TextDocumentSyncKind;
import org.springframework.dsl.lsp.service.Completioner;
import org.springframework.dsl.lsp.service.DocumentStateTracker;
import org.springframework.dsl.lsp.service.Hoverer;
import org.springframework.dsl.lsp.service.Reconciler;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
 * <p>
 * This implementation relies on {@link DocumentStateTracker} as a base
 * functionality to handle @{@code LSP} {@code didOpen}, {@code didChanged},
 * {@code didSave} and {@code didClose} requests and handle further actions
 * based on those requests.
 *
 * @author Janne Valkealahti
 *
 */
@LspController
public class GenericLanguageServerController implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(GenericLanguageServerController.class);
	private final DocumentStateTracker documentStateTracker;
	private final ObjectProvider<Reconciler> reconcilerProvider;
	private final ObjectProvider<Completioner> completionerProvider;
	private final ObjectProvider<Hoverer> hovererProvider;
	private Reconciler reconciler;
	private Completioner completioner;
	private Hoverer hoverer;

	/**
	 * Instantiate a generic language server controller.
	 *
	 * @param documentStateTracker the document state tracker
	 * @param reconcilerProvider the provider for reconciler
	 * @param completionerProvider the provider for completioner
	 * @param hovererProvider the provider for hoverer
	 */
	public GenericLanguageServerController(DocumentStateTracker documentStateTracker,
			ObjectProvider<Reconciler> reconcilerProvider, ObjectProvider<Completioner> completionerProvider,
			ObjectProvider<Hoverer> hovererProvider) {
		Assert.notNull(documentStateTracker, "'documentStateTracker' must be set");
		this.documentStateTracker = documentStateTracker;
		this.reconcilerProvider = reconcilerProvider;
		this.completionerProvider = completionerProvider;
		this.hovererProvider = hovererProvider;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO: need to support dispatching to multiple language id's
		this.reconciler = reconcilerProvider.getIfAvailable();
		this.completioner = completionerProvider.getIfAvailable();
		this.hoverer = hovererProvider.getIfAvailable();
	}

	/**
	 * Method handling {@code LSP client initialize} request and responding
	 * {@link InitializeResult} based on what services are available for this
	 * controller.
	 *
	 * @param params the initialize params
	 * @return a mono of initialize result
	 */
	@LspInitialize
	@LspResponseBody
	public InitializeResult clientInit(InitializeParams params) {
		ServerCapabilities serverCapabilities = new ServerCapabilities();
		serverCapabilities.setHoverProvider(hovererProvider.getIfAvailable() != null);
		// TODO: think how to use sync kind None
		serverCapabilities.setTextDocumentSyncKind(
				documentStateTracker.isIncrementalChangesSupported() ? TextDocumentSyncKind.Incremental
						: TextDocumentSyncKind.Full);
		if (completioner != null) {
			serverCapabilities.setCompletionProvider(new CompletionOptions());
		}
		return new InitializeResult(serverCapabilities);
	}

	/**
	 * Method handling {@code LSP client didOpen} request and dispatching
	 * information about opened {@code document} and its metadata to document
	 * tracker. Behaviour of further {@code LSP client} requests for
	 * {@code didChanged}, {@code didSave} and {@code didClose} will be based on
	 * information stored and dispatched in this method.
	 *
	 * @param params the {@link DidOpenTextDocumentParams}
	 * @param context the lsp client context
	 */
	@LspDidOpen
	@LspNoResponseBody
	public void clientDocumentOpened(DidOpenTextDocumentParams params, LspClientContext context) {
		this.documentStateTracker.didOpen(params).doOnNext(c -> {
			reconciler.reconcile(c).doOnNext(d -> {
				context.getClient().send(d);
			}).subscribe();
		}).subscribe();
	}

	/**
	 * Method handling {@code LSP client didChange} request and dispatching into
	 * {@link Reconciler} if available.
	 *
	 * @param params the {@link DidChangeTextDocumentParams}
	 * @param context the lsp client context
	 */
	@LspDidChange
	@LspNoResponseBody
	public void clientDocumentChanged(DidChangeTextDocumentParams params, LspClientContext context) {
		this.documentStateTracker.didChange(params).doOnNext(c -> {
			reconciler.reconcile(c).doOnNext(d -> {
				context.getClient().send(d);
			}).subscribe();
		}).subscribe();
	}

	/**
	 * Method handling {@code LSP client didClose} request and dispatching into
	 * {@link DocumentStateTracker}.
	 *
	 * @param params the {@link DidCloseTextDocumentParams}
	 * @param context the lsp client context
	 */
	@LspDidClose
	@LspNoResponseBody
	public void clientDocumentClosed(DidCloseTextDocumentParams params, LspClientContext context) {
		this.documentStateTracker.didClose(params).doOnNext(c -> {
			reconciler.reconcile(c).doOnNext(d -> {
				context.getClient().send(d);
			}).subscribe();
		}).subscribe();
	}

	/**
	 * Method handling {@code LSP client didSave} request and dispatching into
	 * {@link DocumentStateTracker}.
	 *
	 * @param params the {@link DidSaveTextDocumentParams}
	 * @param context the lsp client context
	 */
	@LspDidSave
	@LspNoResponseBody
	public void clientDocumentSaved(DidSaveTextDocumentParams params, LspClientContext context) {
		this.documentStateTracker.didSave(params).doOnNext(c -> {
			reconciler.reconcile(c).doOnNext(d -> {
				context.getClient().send(d);
			}).subscribe();
		}).subscribe();
	}

	/**
	 * Method handling {@code LSP client hover} request and dispatching into
	 * {@link Hoverer}.
	 *
	 * @param params the {@link TextDocumentPositionParams}
	 * @return a mono of hover
	 */
	@LspHover
	@LspResponseBody
	public Mono<Hover> hover(TextDocumentPositionParams params) {
		if (hoverer != null) {
			return hoverer.hover(documentStateTracker.getDocument(params.getTextDocument().getUri()),
					params.getPosition());
		}
		return Mono.empty();
	}

	/**
	 * Method handling {@code LSP client completion} request and dispatching into
	 * {@link Completioner}.
	 *
	 * @param params the {@link TextDocumentPositionParams}
	 * @return a flux of completion items
	 */
	@LspCompletion
	@LspResponseBody
	public Flux<CompletionItem> completion(TextDocumentPositionParams params) {
		if (completioner != null) {
			return completioner.complete(null, null);
		}
		return Flux.empty();
	}
}
