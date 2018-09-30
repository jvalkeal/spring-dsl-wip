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
package org.springframework.dsl.antlr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.dsl.Test2Grammar;
import org.springframework.dsl.Test2Lexer;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.model.LanguageId;
import org.springframework.dsl.service.reconcile.DefaultReconcileProblem;
import org.springframework.dsl.service.reconcile.Linter;
import org.springframework.dsl.service.reconcile.ReconcileProblem;
import org.springframework.dsl.symboltable.ClassSymbol;
import org.springframework.dsl.symboltable.SymbolTable;

import reactor.core.publisher.Flux;

/**
 * {@link Linter} for {@code test2} language.
 *
 * @author Janne Valkealahti
 *
 */
public class Test2AntlrLinter extends AbstractAntlrLinter<Test2Lexer, Test2Grammar> {

	public Test2AntlrLinter() {
		super(TestAntrlUtils.TEST2_ANTRL_FACTORY);
	}

	@Override
	public List<LanguageId> getSupportedLanguageIds() {
		return Arrays.asList(TestAntrlUtils.TEST2_LANGUAGE_ID);
	}

	@Override
	public Flux<ReconcileProblem> lintInternal(Document document) {
		List<ReconcileProblem> errors = new ArrayList<>();
		Test2Grammar parser = getParser(document.content());
        parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
        parser.removeErrorListeners();
        parser.addErrorListener(new Test2ErrorListener(errors));
        parser.definitions();
        return Flux.concat(Flux.fromIterable(errors), lintTransitionStateRefs(document));
	}

	public Flux<ReconcileProblem> lintTransitionStateRefs(Document document) {
		List<ReconcileProblem> errors = new ArrayList<>();
		Test2Grammar parser = getParser(document.content());

		ParseTree tree = parser.definitions();
		Test2Visitor visitor = new Test2Visitor();
		AntlrParseResult<Object> result = visitor.visit(tree);
		SymbolTable symbolTable = result.getSymbolTable();

		List<String> sss = symbolTable.getAllSymbols().stream()
			.filter(s -> s.getScope().getName() == "org.springframework.statemachine.state.State")
			.map(s -> s.getName())
			.collect(Collectors.toList());

		symbolTable.getAllSymbols().stream().forEach(s -> {
			if (s.getScope().getName() == "org.springframework.statemachine.transition.Transition") {
				if (s instanceof ClassSymbol) {
					((ClassSymbol)s).getFields().stream().forEach(f -> {
						if (!sss.contains(f.getName())) {
							errors.add(new DefaultReconcileProblem("missing", f.getRange()));
						}
					});
				}
			}
		});

		return Flux.fromIterable(errors);
	}

}
