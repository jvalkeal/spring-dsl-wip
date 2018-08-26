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
package org.springframework.dsl.document;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DocumentRegionTests {

	private Document TDOC1 = new TextDocument(TextDocumentTests.DOC1);

	@Test
	public void test() {
		DocumentRegion documentRegion = new DocumentRegion(TDOC1, 0, 5);
		assertThat(documentRegion.toString()).isEqualTo(TextDocumentTests.DOC1);
	}
}
