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
package org.springframework.dsl.lsp.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.domain.CompletionList;
import org.springframework.dsl.domain.CompletionParams;
import org.springframework.dsl.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.domain.Hover;
import org.springframework.dsl.domain.Position;
import org.springframework.dsl.domain.PublishDiagnosticsParams;
import org.springframework.dsl.domain.TextDocumentPositionParams;
import org.springframework.dsl.domain.TextEdit;
import org.springframework.dsl.domain.WillSaveTextDocumentParams;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcController;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcNotification;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcRequestMapping;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcResponseBody;
import org.springframework.dsl.jsonrpc.session.JsonRpcSession;
import org.springframework.dsl.lsp.server.LspServerSystemConstants;
import org.springframework.dsl.service.Completioner;
import org.springframework.dsl.service.DocumentStateTracker;
import org.springframework.dsl.service.DslServiceRegistry;
import org.springframework.dsl.service.Hoverer;
import org.springframework.dsl.service.Reconciler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A {@code JsonRpcController} implementation {@code textDocument} features
 * what a {@code Language Server} should provide.
 *
 * @author Janne Valkealahti
 *
 */
@JsonRpcController
@JsonRpcRequestMapping(method = "textDocument/")
public class TextDocumentLanguageServerController implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(TextDocumentLanguageServerController.class);
	private final ObjectProvider<Reconciler> reconcilerProvider;
	private Reconciler reconciler;
	private DslServiceRegistry registry;

	/**
	 * Instantiates a new text document language server controller.
	 *
	 * @param reconcilerProvider the reconciler provider
	 * @param dslServiceRegistry the dsl service registry
	 */
	public TextDocumentLanguageServerController(ObjectProvider<Reconciler> reconcilerProvider,
			DslServiceRegistry dslServiceRegistry) {
		this.reconcilerProvider = reconcilerProvider;
		this.registry = dslServiceRegistry;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.reconciler = reconcilerProvider.getIfAvailable();
	}

	/**
	 * Method handling {@code LSP client didOpen} request and dispatching
	 * information about opened {@code document} and its metadata to document
	 * tracker. Behaviour of further {@code LSP client} requests for
	 * {@code didChanged}, {@code didSave} and {@code didClose} will be based on
	 * information stored and dispatched in this method.
	 *
	 * @param params the {@link DidOpenTextDocumentParams}
	 * @param session the {@link JsonRpcSession}
	 */
	@JsonRpcRequestMapping(method = "didOpen")
	@JsonRpcNotification(method = "textDocument/publishDiagnostics")
	public Flux<PublishDiagnosticsParams> clientDocumentOpened(DidOpenTextDocumentParams params, JsonRpcSession session) {
		log.debug("clientDocumentOpened {}", params);
		DocumentStateTracker documentStateTracker = getTracker(session);
		return Flux.from(documentStateTracker.didOpen(params))
				.flatMap(document -> reconciler.reconcile(document)
				.switchIfEmpty(Mono.just(new PublishDiagnosticsParams(params.getTextDocument().getUri()))));
	}

	/**
	 * Method handling {@code LSP client didChange} request and dispatching into
	 * {@link Reconciler} if available.
	 *
	 * @param params the {@link DidChangeTextDocumentParams}
	 * @param session the {@link JsonRpcSession}
	 */
	@JsonRpcRequestMapping(method = "didChange")
	@JsonRpcNotification(method = "textDocument/publishDiagnostics")
	public Flux<PublishDiagnosticsParams> clientDocumentChanged(DidChangeTextDocumentParams params, JsonRpcSession session) {
		log.debug("clientDocumentChanged {}", params);
		DocumentStateTracker documentStateTracker = getTracker(session);
		return Flux.from(documentStateTracker.didChange(params))
				.flatMap(document -> reconciler.reconcile(document)
				.switchIfEmpty(Mono.just(new PublishDiagnosticsParams(params.getTextDocument().getUri()))));
	}

	/**
	 * Method handling {@code LSP client didClose} request and dispatching into
	 * {@link DocumentStateTracker}.
	 *
	 * @param params the {@link DidCloseTextDocumentParams}
	 * @param session the {@link JsonRpcSession}
	 */
	@JsonRpcRequestMapping(method = "didClose")
	@JsonRpcNotification
	public Mono<Void> clientDocumentClosed(DidCloseTextDocumentParams params, JsonRpcSession session) {
		log.debug("clientDocumentClosed {}", params);
		DocumentStateTracker documentStateTracker = getTracker(session);
		return Flux.from(documentStateTracker.didClose(params))
				.then();
	}

	/**
	 * Method handling {@code LSP client didSave} request and dispatching into
	 * {@link DocumentStateTracker}.
	 *
	 * @param params the {@link DidSaveTextDocumentParams}
	 * @param session the {@link JsonRpcSession}
	 */
	@JsonRpcRequestMapping(method = "didSave")
	@JsonRpcNotification
	public Mono<Void> clientDocumentSaved(DidSaveTextDocumentParams params, JsonRpcSession session) {
		log.debug("clientDocumentSaved {}", params);
		DocumentStateTracker documentStateTracker = getTracker(session);
		return Flux.from(documentStateTracker.didSave(params))
				.then();
	}

	/**
	 * Method handling {@code LSP client willSave} request and dispatching into
	 * {@link DocumentStateTracker}.
	 *
	 * @param params the {@link WillSaveTextDocumentParams}
	 * @param session the {@link JsonRpcSession}
	 */
	@JsonRpcRequestMapping(method = "willSave")
	@JsonRpcNotification
	public Mono<Void> clientDocumentWillSave(WillSaveTextDocumentParams params, JsonRpcSession session) {
		log.debug("clientDocumentWillSave {}", params);
		DocumentStateTracker documentStateTracker = getTracker(session);
		return Flux.from(documentStateTracker.willSave(params))
				.then();
	}

	/**
	 * Method handling {@code LSP client willSaveWaitUntil} request and dispatching into
	 * {@link DocumentStateTracker}.
	 *
	 * @param params the {@link WillSaveTextDocumentParams}
	 * @return a flux of textedit's
	 */
	@JsonRpcRequestMapping(method = "willSaveWaitUntil")
	@JsonRpcResponseBody
	public Flux<TextEdit> clientDocumentWillSaveWaitUntil(WillSaveTextDocumentParams params) {
		return Flux.empty();
	}

	/**
	 * Method handling {@code LSP client hover} request and dispatching into
	 * {@link Hoverer}.
	 *
	 * @param params the {@link TextDocumentPositionParams}
	 * @param session the {@link JsonRpcSession}
	 * @return a mono of hover
	 */
	@JsonRpcRequestMapping(method = "hover")
	@JsonRpcResponseBody
	public Mono<Hover> hover(TextDocumentPositionParams params, JsonRpcSession session) {
		log.debug("hover {}", params);
		DocumentStateTracker documentStateTracker = getTracker(session);
		Document document = documentStateTracker.getDocument(params.getTextDocument().getUri());
		Position position = params.getPosition();

		return Flux.fromIterable(registry.getHoverers(document.languageId()))
				.concatMap(hoverer -> hoverer.hover(document, position))
				.next();
	}

	/**
	 * Method handling {@code LSP client completion} request and dispatching into
	 * {@link Completioner}.
	 *
	 * @param params the {@link CompletionParams}
	 * @param session the {@link JsonRpcSession}
	 * @return a mono of completion list
	 */
	@JsonRpcRequestMapping(method = "completion")
	@JsonRpcResponseBody
	public Mono<CompletionList> completion(CompletionParams params, JsonRpcSession session) {
		// TODO: spec is CompletionItem[] | CompletionList | null
		//       not sure if there are clients which only supports CompletionItem[]
		log.debug("completion {}", params);
		DocumentStateTracker documentStateTracker = getTracker(session);
		Document document = documentStateTracker.getDocument(params.getTextDocument().getUri());
		Position position = params.getPosition();

		// TODO: think how to integrate into isIncomplete setting
		return Flux.fromIterable(registry.getCompletioners(document.languageId()))
				.concatMap(completioner -> completioner.complete(document, position))
				.buffer()
				.map(completionItems -> {
					return CompletionList.completionList()
							.isIncomplete(false)
							.items(completionItems)
							.build();
				})
				.next();
	}

	private static DocumentStateTracker getTracker(JsonRpcSession session) {
		return session.getRequiredAttribute(LspServerSystemConstants.SESSION_ATTRIBUTE_DOCUMENT_STATE_TRACKER);
	}
}
