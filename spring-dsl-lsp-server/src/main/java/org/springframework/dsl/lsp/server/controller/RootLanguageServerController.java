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
import org.springframework.dsl.domain.InitializeParams;
import org.springframework.dsl.domain.InitializeResult;
import org.springframework.dsl.domain.InitializedParams;
import org.springframework.dsl.domain.TextDocumentSyncKind;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcController;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcNotification;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcRequestMapping;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcResponseBody;
import org.springframework.dsl.jsonrpc.session.JsonRpcSession;
import org.springframework.dsl.lsp.server.LspServerSystemConstants;
import org.springframework.dsl.service.DefaultDocumentStateTracker;
import org.springframework.dsl.service.DslServiceRegistry;

import reactor.core.publisher.Mono;

/**
 * A generic {@code JsonRpcController} implementation base root level features
 * what a {@code Language Server} should provide.
 *
 * @author Janne Valkealahti
 *
 */
@JsonRpcController
public class RootLanguageServerController {

	private static final Logger log = LoggerFactory.getLogger(RootLanguageServerController.class);
	private final DslServiceRegistry registry;

	/**
	 * Instantiate a base language server controller.
	 *
	 * @param dslServiceRegistry the dsl service registry
	 */
	public RootLanguageServerController(DslServiceRegistry dslServiceRegistry) {
		this.registry = dslServiceRegistry;
	}

	@JsonRpcRequestMapping(method = "initialize")
	@JsonRpcResponseBody
	Mono<InitializeResult> initialize(InitializeParams params, JsonRpcSession session) {
		log.debug("initialize {}", params);
		// initialize is a first request from a lsp client, thus we return response having
		// capabilities and also create a session what further communication can use.
		return Mono.fromSupplier(() -> {
			boolean oldFormat = params.getCapabilities().getTextDocument().getSynchronization()
					.getDidSave() == null;
			return InitializeResult.initializeResult()
				.capabilities()
					.hoverProvider(!registry.getHoverers().isEmpty())
					.completionProvider(!registry.getCompletioners().isEmpty())
						.resolveProvider(false)
						.and()
					.textDocumentSyncKind(oldFormat ? TextDocumentSyncKind.Incremental : null)
					.textDocumentSyncOptions(!oldFormat)
						.openClose(true)
						// TODO: think how to use sync kind None
						.change(TextDocumentSyncKind.Incremental)
//						.change(documentStateTracker.isIncrementalChangesSupported() ? TextDocumentSyncKind.Incremental
//							: TextDocumentSyncKind.Full)
						.and()
					.and()
				.build();
		}).doOnSuccess(result -> {
			// TODO: just a conceptual tweak now to see how session is used
			session.getAttributes().put(LspServerSystemConstants.SESSION_ATTRIBUTE_SESSION_INITIALIZED, true);
			session.getAttributes().put(LspServerSystemConstants.SESSION_ATTRIBUTE_DOCUMENT_STATE_TRACKER,
					new DefaultDocumentStateTracker());
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
