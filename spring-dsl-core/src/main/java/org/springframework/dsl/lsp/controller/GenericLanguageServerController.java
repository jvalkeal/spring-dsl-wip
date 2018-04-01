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
import org.springframework.dsl.lsp.annotation.LspController;
import org.springframework.dsl.lsp.annotation.LspInitialize;
import org.springframework.dsl.lsp.annotation.LspResponseBody;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.service.Reconciler;

/**
 * A generic {@code LSP Controller} implementation providing most common
 * features what {@code Language Server} should provide.
 *
 * @author Janne Valkealahti
 *
 */
@LspController
public class GenericLanguageServerController {

	private final ObjectProvider<Reconciler> reconcilerProvider;

	public GenericLanguageServerController(ObjectProvider<Reconciler> reconcilerProvider) {
		this.reconcilerProvider = reconcilerProvider;
	}

	@LspInitialize
	@LspResponseBody
	public InitializeResult clientInit(InitializeParams params) {
		return new InitializeResult();
	}
}
