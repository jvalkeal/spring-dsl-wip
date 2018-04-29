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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.reconcile.Linter;
import org.springframework.dsl.reconcile.ReconcileProblem;

import reactor.core.publisher.Flux;

/**
 * Base {@link Linter} providing low level support for {@code ANTLR}.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class AbstractAntlrLinter extends AntlrObjectSupport implements Linter {

	private static final Log log = LogFactory.getLog(AbstractAntlrCompletioner.class);

	/**
	 * Instantiates a new abstract antlr linter.
	 *
	 * @param antlrFactory the antlr factory
	 */
	public AbstractAntlrLinter(AntlrFactory antlrFactory) {
		super(antlrFactory);
	}

	@Override
	public Flux<ReconcileProblem> lint(Document document) {
		return lintInternal(document);
	}

	private void xxx(Document document) {
//		Lexer lexer = getAntlrFactory().createLexer(stringToCharStream(document.get()));



	}

	public abstract Flux<ReconcileProblem> lintInternal(Document document);
}
