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

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.document.TextDocumentContentChange;
import org.springframework.dsl.lsp.domain.Diagnostic;
import org.springframework.dsl.lsp.domain.DiagnosticSeverity;
import org.springframework.dsl.lsp.domain.PublishDiagnosticsParams;
import org.springframework.dsl.lsp.service.Reconciler;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Responds to document changes and calls {@link Linter} to validate
 * document contents.
 */
public class SimpleReconciler implements Reconciler {

	private static final Logger log = LoggerFactory.getLogger(SimpleReconciler.class);

	private Linter linterFunction;
	private Disposable dispoable;

	public SimpleReconciler(Linter linterFunction) {
		this.linterFunction = linterFunction;
	}


	@Override
	public Flux<PublishDiagnosticsParams> reconcile(TextDocumentContentChange event) {
		Document doc = event.getDocument();
		Flux<ReconcileProblem> problems = linterFunction.lint(event.getDocument());
		//TODO:  make this 'smarter' to ensure responsiveness, support cancelation etc.

		return problems
			.filter(p -> getDiagnosticSeverity(p) != null)
			.flatMap(p -> toDiagnostic(doc, p))
			.flatMap(d -> {
				return Mono.just(new PublishDiagnosticsParams(event.getDocument().getId().getUri(), Arrays.asList(d)));
			}
			);

	}

	private Mono<Diagnostic> toDiagnostic(Document document, ReconcileProblem problem) {
		DiagnosticSeverity severity = getDiagnosticSeverity(problem);
		if (severity != null) {
//			try {
				Diagnostic d = new Diagnostic();
				d.setRange(problem.getRange());
//				d.setRange(document.toRange(new DefaultRegion(problem.getOffset(), problem.getLength())));
				d.setCode(problem.getCode());
				d.setMessage(problem.getMessage());
				d.setSeverity(getDiagnosticSeverity(problem));
				return Mono.just(d);
//			} catch (BadLocationException e) {
//				// Ignore invalid reconcile problems.
//			}
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

	@PreDestroy
	public void dispose() {
		if (dispoable!=null) {
			dispoable.dispose();
		}
	}

}
