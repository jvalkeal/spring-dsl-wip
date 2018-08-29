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
package demo.wordcheckdsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.dsl.document.Document;
import org.springframework.dsl.reconcile.Linter;
import org.springframework.dsl.reconcile.ReconcileProblem;
import org.springframework.dsl.service.AbstractDslService;

import reactor.core.publisher.Flux;

/**
 * A {@link Linter} for a {@code simple} language.
 *
 * @author Janne Valkealahti
 * @author Kris De Volder
 *
 */
public class WordcheckLanguageLinter extends AbstractDslService implements Linter {

	public WordcheckLanguageLinter() {
		super(Arrays.asList(WordcheckLanguage.LANGUAGEID));
	}

	@Override
	public Flux<ReconcileProblem> lint(Document document) {
		return Flux.defer(() -> {
			return Flux.fromIterable(lintProblems(document));
		});
	}

	private List<ReconcileProblem> lintProblems(Document document) {
		List<ReconcileProblem> problems = new ArrayList<>();
		return problems;
	}
}
