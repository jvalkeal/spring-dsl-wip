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
import org.springframework.dsl.jsonrpc.ServerJsonRpcExchange;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcController;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcNotification;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcRequestMapping;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcResponseBody;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.InitializedParams;
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
	}

	@JsonRpcRequestMapping(method = "initialize")
	@JsonRpcResponseBody
	Mono<InitializeResult> initialize(InitializeParams params, ServerJsonRpcExchange exchange) {
		// initialize is a first request from a lsp client, thus we return response having
		// capabilities and also create a session what further communication can use.

//		log.debug("initialize {}", params);
		log.debug("initializexxx {} {}", params, exchange.getSession());
		return Mono.fromSupplier(() -> {
			boolean oldFormat = params.getCapabilities().getTextDocument().getSynchronization()
					.getDidSave() == null;
			return InitializeResult.initializeResult()
				.capabilities()
					.hoverProvider(hovererProvider.getIfAvailable() != null)
					.completionProvider(completionerProvider.getIfAvailable() != null)
						.resolveProvider(false)
						.and()
					.textDocumentSyncKind(oldFormat ? TextDocumentSyncKind.Incremental : null)
					.textDocumentSyncOptions(!oldFormat)
						.openClose(true)
						// TODO: think how to use sync kind None
						.change(documentStateTracker.isIncrementalChangesSupported() ? TextDocumentSyncKind.Incremental
							: TextDocumentSyncKind.Full)
						.and()
					.and()
				.build();
		});
	}

	@JsonRpcRequestMapping(method = "initialized")
	@JsonRpcNotification
	public void initialized(InitializedParams params) {
		log.debug("initialized {}", params);
	}

	@JsonRpcRequestMapping(method = "shutdown")
	@JsonRpcResponseBody
	public Mono<Object> shutdown() {
		log.debug("shutdown");
		return Mono.empty();
	}
}
