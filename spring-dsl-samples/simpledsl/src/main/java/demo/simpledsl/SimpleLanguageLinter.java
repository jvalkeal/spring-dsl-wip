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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.lsp.domain.Position;
import org.springframework.dsl.lsp.domain.Range;
import org.springframework.dsl.lsp.server.result.method.annotation.DefaultReconcileProblem;
import org.springframework.dsl.reconcile.Linter;
import org.springframework.dsl.reconcile.ProblemSeverity;
import org.springframework.dsl.reconcile.ProblemType;
import org.springframework.dsl.reconcile.ReconcileProblem;

import demo.simpledsl.SimpleLanguage.Line;
import reactor.core.publisher.Flux;

/**
 * A {@link Linter} for a {@code simple} language.
 *
 * @author Janne Valkealahti
 *
 */
public class SimpleLanguageLinter implements Linter {

	private static final Logger log = LoggerFactory.getLogger(SimpleLanguageLinter.class);

	private static ProblemType PROBLEM = new ProblemType() {

		@Override
		public ProblemSeverity getDefaultSeverity() {
			return ProblemSeverity.ERROR;
		}

		@Override
		public String getCode() {
			return "code";
		}
	};

	@Override
	public Flux<ReconcileProblem> lint(Document document) {
		List<ReconcileProblem> problems = new ArrayList<>();

		SimpleLanguage simpleLanguage = SimpleLanguage.build(document);
		List<Line> lines = simpleLanguage.getLines();

		for (Line line : lines) {
			if (line.getValueToken() == null) {
				Position start = new Position(line.getLine(), line.getKeyToken().getStart());
				Position end = new Position(line.getLine(), line.getKeyToken().getEnd());
				problems.add(new DefaultReconcileProblem(PROBLEM, "Missing value", new Range(start, end), "xxx"));
			}
		}
		return Flux.fromIterable(problems);
	}

}
