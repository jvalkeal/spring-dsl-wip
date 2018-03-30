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
package demo.simpledsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.lsp.LanguageClientContext;
import org.springframework.dsl.lsp.annotation.LspController;
import org.springframework.dsl.lsp.annotation.LspDidChange;
import org.springframework.dsl.lsp.annotation.LspInitialize;
import org.springframework.dsl.lsp.annotation.LspResponseBody;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;

@LspController
public class SimpleLspController {

	private final static Logger log = LoggerFactory.getLogger(SimpleLspController.class);

	@LspInitialize
	@LspResponseBody
	public InitializeResult clientInit(InitializeParams params) {
		log.info("clientInit {}", params);
		return new InitializeResult();
	}

	@LspDidChange
	public void clientDocumentChanged(DidChangeTextDocumentParams params, LanguageClientContext context) {
		log.info("clientDocumentChanged {} {}", params, context);
	}
}
