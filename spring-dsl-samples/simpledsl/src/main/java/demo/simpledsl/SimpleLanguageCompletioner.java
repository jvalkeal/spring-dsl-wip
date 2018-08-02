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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.dsl.document.Document;
import org.springframework.dsl.domain.CompletionItem;
import org.springframework.dsl.domain.Position;
import org.springframework.dsl.model.LanguageId;
import org.springframework.dsl.service.Completioner;

import demo.simpledsl.SimpleLanguage.TokenType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A {@link Completioner} implementation for a {@code simple} sample language.
 *
 * @author Janne Valkealahti
 * @see EnableSimpleLanguage
 *
 */
public class SimpleLanguageCompletioner implements Completioner {

	private final List<LanguageId> ids = Arrays.asList(SimpleLanguage.LANGUAGEID);

	@Override
	public List<LanguageId> getSupportedLanguageIds() {
		return ids;
	}

	@Override
	public Flux<CompletionItem> complete(Document document, Position position) {
		SimpleLanguage simpleLanguage = SimpleLanguage.build(document);
		Collection<TokenType> tokens = simpleLanguage.resolveLegalTokens(position);
		return Flux.fromIterable(tokens)
				.filter(tokenType -> tokenType != TokenType.VALUE)
				.flatMap(tt -> {
					return Mono.just(CompletionItem.completionItem().label(tt.toString().toLowerCase()).build());
				});
	}
}
