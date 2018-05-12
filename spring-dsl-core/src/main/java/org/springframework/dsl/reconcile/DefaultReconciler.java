/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.dsl.reconcile;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.document.TextDocumentContentChange;
import org.springframework.dsl.lsp.domain.Diagnostic;
import org.springframework.dsl.lsp.domain.DiagnosticSeverity;
import org.springframework.dsl.lsp.domain.PublishDiagnosticsParams;
import org.springframework.dsl.lsp.service.Reconciler;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Responds to document changes and calls {@link Linter} to validate
 * document contents.
 *
 * @author Kris De Volder
 * @author Janne Valkealahti
 *
 */
public class DefaultReconciler implements Reconciler {

	private static final Logger log = LoggerFactory.getLogger(DefaultReconciler.class);
	private final Linter linter;

	/**
	 * Instantiates a new simple reconciler.
	 *
	 * @param linter the linter
	 */
	public DefaultReconciler(Linter linter) {
		Assert.notNull(linter, "linter must be set");
		this.linter = linter;
	}

	@Override
	public Flux<PublishDiagnosticsParams> reconcile(TextDocumentContentChange event) {
		log.debug("Reconciling {}", event);
		Document doc = event.getDocument();
		Flux<ReconcileProblem> problems = linter.lint(event.getDocument());
		//TODO:  make this 'smarter' to ensure responsiveness, support cancelation etc.s

		return problems
			.filter(p -> getDiagnosticSeverity(p) != null)
			.flatMap(p -> toDiagnostic(doc, p))
			.flatMap(d -> {
				return Mono.just(new PublishDiagnosticsParams(event.getDocument().uri(), Arrays.asList(d)));
			}
			);

	}

	private Mono<Diagnostic> toDiagnostic(Document document, ReconcileProblem problem) {
		DiagnosticSeverity severity = getDiagnosticSeverity(problem);
		if (severity != null) {
				Diagnostic d = new Diagnostic();
				d.setRange(problem.getRange());
				d.setCode(problem.getCode());
				d.setMessage(problem.getMessage());
				d.setSeverity(getDiagnosticSeverity(problem));
				return Mono.just(d);
		}
		return Mono.empty();
	}

	protected DiagnosticSeverity getDiagnosticSeverity(ReconcileProblem problem) {
		ProblemSeverity severity = problem.getType().getDefaultSeverity();
		switch (severity) {
		case ERROR:
			return DiagnosticSeverity.Error;
		case WARNING:
			return DiagnosticSeverity.Warning;
		case INFO:
			return DiagnosticSeverity.Information;
		case HINT:
			return DiagnosticSeverity.Hint;
		case IGNORE:
			return null;
		default:
			throw new IllegalStateException("Bug! Missing switch case?");
		}
	}

}
