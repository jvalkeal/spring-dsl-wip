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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.document.LanguageId;
import org.springframework.dsl.document.TextDocument;
import org.springframework.dsl.lsp.domain.Hover;
import org.springframework.dsl.lsp.domain.Position;

import reactor.core.publisher.Mono;

public class SimpleLanguageHovererTests {

	private final SimpleLanguageHoverer hoverer = new SimpleLanguageHoverer();

	@Test
	public void testHovers() {
		Document document = new TextDocument("", LanguageId.PLAINTEXT, 0, SimpleLanguageTests.content1);

		Mono<Hover> hover = hoverer.hover(document, new Position(0, 1));
		assertThat(hover).isNotNull();
		assertThat(hover.block()).isNotNull();
		assertThat(hover.block().getContents()).isNotNull();
		assertThat(hover.block().getContents().getValue()).isEqualTo("token");
	}

}