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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.dsl.antlr.support.AntlrObjectSupport;
import org.springframework.dsl.antlr.support.DefaultAntlrCompletionEngine;
import org.springframework.dsl.antlr.symboltable.SymbolTable;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.domain.CompletionItem;
import org.springframework.dsl.domain.Position;
import org.springframework.dsl.service.Completioner;

import reactor.core.publisher.Flux;

/**
 * Base {@link Completioner} providing low level support for {@code ANTLR}.
 *
 * @author Janne Valkealahti
 *
 * @param <L> the type of lexer
 * @param <P> the type of parser
 *
 */
public abstract class AbstractAntlrCompletioner<L extends Lexer, P extends Parser> extends AntlrObjectSupport<L, P>
		implements Completioner {

	/**
	 * Instantiates a new abstract antlr completioner.
	 *
	 * @param antlrFactory the antlr factory
	 */
	public AbstractAntlrCompletioner(AntlrFactory<L, P> antlrFactory) {
		super(antlrFactory);
	}

	@Override
	public final Flux<CompletionItem> complete(Document document, Position position) {
		return completeInternal(document.content(), position);
	}

	protected abstract Flux<CompletionItem> completeInternal(String content, Position position);

	protected P getParser(String input) {
		L lexer = getAntlrFactory().createLexer(CharStreams.fromString(input));
		P parser = getAntlrFactory().createParser(new CommonTokenStream(lexer));
		return parser;
	}

	protected SymbolTable getSymbolTable(String content, P parser) {
		return null;
	}

	protected AntlrCompletionEngine getAntlrCompletionEngine(P parser) {
		return new DefaultAntlrCompletionEngine(parser);
	}

	protected ParserRuleContext getParserRuleContext(P parser) {
		return null;
	}

	protected Flux<String> completeRules(AntlrCompletionResult completionResult, SymbolTable symbolTable) {
		return Flux.empty();
	}

	protected Flux<String> completeTokens(AntlrCompletionResult completionResult, P parser) {
		return Flux.empty();
	}

	protected Flux<String> getCompletions(String content, Position position) {
		P parser = getParser(content);
		AntlrCompletionEngine completionEngine = getAntlrCompletionEngine(parser);
		AntlrCompletionResult completionResult = completionEngine.collectResults(position,
				getParserRuleContext(parser));
		parser = getParser(content);
		return Flux.concat(completeRules(completionResult, getSymbolTable(content, parser)),
				completeTokens(completionResult, parser));
	}
}
