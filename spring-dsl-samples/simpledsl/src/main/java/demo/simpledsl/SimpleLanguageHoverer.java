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

import org.springframework.dsl.document.Document;
import org.springframework.dsl.domain.Hover;
import org.springframework.dsl.domain.MarkupKind;
import org.springframework.dsl.domain.Position;
import org.springframework.dsl.service.AbstractDslService;
import org.springframework.dsl.service.Hoverer;

import demo.simpledsl.SimpleLanguage.Token;
import reactor.core.publisher.Mono;

/**
 * A {@link Hoverer} implementation for a {@code simple} sample language.
 *
 * @author Janne Valkealahti
 * @see EnableSimpleLanguage
 *
 */
public class SimpleLanguageHoverer extends AbstractDslService implements Hoverer {

	public SimpleLanguageHoverer() {
		super(Arrays.asList(SimpleLanguage.LANGUAGEID));
	}

	@Override
	public Mono<Hover> hover(Document document, Position position) {
		// we're getting a request to provide a hover in a document
		// for a specific position. from a document, request a token
		// for this particular position and return information about
		// it, if available and fill hover with it info.
		// we're returning this as a supplier so that actual hover
		// operation will happen when demand for it is requested.

		SimpleLanguage simpleLanguage = SimpleLanguage.build(document);
		Token token = simpleLanguage.getToken(position);
		if (token != null) {
			Hover hover = Hover.hover()
					.contents()
						.kind(MarkupKind.plaintext)
						.value(token.getType().toString())
						.and()
					.build();
			return Mono.just(hover);
		} else {
			return Mono.empty();
		}
	}
}
