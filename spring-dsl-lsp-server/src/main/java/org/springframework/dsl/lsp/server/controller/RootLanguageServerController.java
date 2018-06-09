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
import org.springframework.dsl.jsonrpc.annotation.JsonRpcController;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcRequestMapping;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcResponseBody;
import org.springframework.dsl.lsp.domain.CompletionOptions;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.InitializedParams;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.domain.TextDocumentSyncKind;
import org.springframework.dsl.lsp.service.Completioner;
import org.springframework.dsl.lsp.service.DocumentStateTracker;
import org.springframework.dsl.lsp.service.Hoverer;
import org.springframework.dsl.lsp.service.Reconciler;
import org.springframework.util.Assert;

import reactor.core.publisher.Mono;

/**
 * A generic {@code JsonRpcController} implementation base root level features
 * what a {@code Language Server} should provide.
 *
 * @author Janne Valkealahti
 *
 */
@JsonRpcController
public class RootLanguageServerController implements InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(RootLanguageServerController.class);
	private final DocumentStateTracker documentStateTracker;
	private final ObjectProvider<Completioner> completionerProvider;
	private final ObjectProvider<Hoverer> hovererProvider;
	private Completioner completioner;

	/**
	 * Instantiate a base language server controller.
	 *
	 * @param documentStateTracker the document state tracker
	 * @param reconcilerProvider the provider for reconciler
	 * @param completionerProvider the provider for completioner
	 * @param hovererProvider the provider for hoverer
	 */
	public RootLanguageServerController(DocumentStateTracker documentStateTracker,
			ObjectProvider<Reconciler> reconcilerProvider, ObjectProvider<Completioner> completionerProvider,
			ObjectProvider<Hoverer> hovererProvider) {
		Assert.notNull(documentStateTracker, "'documentStateTracker' must be set");
		this.documentStateTracker = documentStateTracker;
		this.completionerProvider = completionerProvider;
		this.hovererProvider = hovererProvider;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.completioner = completionerProvider.getIfAvailable();
	}

	@JsonRpcRequestMapping(method = "initialize")
	@JsonRpcResponseBody
	Mono<InitializeResult> initialize(InitializeParams params) {
		log.debug("initialize {}", params);
		ServerCapabilities serverCapabilities = new ServerCapabilities();
		serverCapabilities.setHoverProvider(hovererProvider.getIfAvailable() != null);
		// TODO: think how to use sync kind None
		serverCapabilities.setTextDocumentSyncKind(
				documentStateTracker.isIncrementalChangesSupported() ? TextDocumentSyncKind.Incremental
						: TextDocumentSyncKind.Full);
		if (completioner != null) {
			serverCapabilities.setCompletionProvider(new CompletionOptions());
		}
		return Mono.just(new InitializeResult(serverCapabilities));
	}

	@JsonRpcRequestMapping(method = "initialized")
	@JsonRpcResponseBody
	public void initialized(InitializedParams params) {
		log.trace("initialized {}", params);
	}
}
