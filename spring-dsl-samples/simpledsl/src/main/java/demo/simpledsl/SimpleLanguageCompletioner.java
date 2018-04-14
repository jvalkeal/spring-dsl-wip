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

import org.springframework.dsl.lsp.domain.CompletionItem;
import org.springframework.dsl.lsp.domain.TextDocumentPositionParams;
import org.springframework.dsl.lsp.service.Completioner;

import reactor.core.publisher.Flux;

/**
 * A {@link Completioner} implementation for a {@code simple} sample language.
 *
 * @author Janne Valkealahti
 * @see EnableSimpleLanguage
 *
 */
public class SimpleLanguageCompletioner implements Completioner {

	@Override
	public Flux<CompletionItem> complete(TextDocumentPositionParams params) {
		return Flux.empty();
	}
}
