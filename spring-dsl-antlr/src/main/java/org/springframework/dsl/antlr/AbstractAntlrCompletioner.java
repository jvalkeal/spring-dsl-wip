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
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Vocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.antlr.support.AntlrObjectSupport;
import org.springframework.dsl.antlr.support.DefaultAntlrCompletionEngine;
import org.springframework.dsl.antlr.symboltable.SymbolTable;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.domain.CompletionItem;
import org.springframework.dsl.domain.Position;
import org.springframework.dsl.service.Completioner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

	private static final Logger log = LoggerFactory.getLogger(AbstractAntlrCompletioner.class);

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
		return completeInternal(document.content());
	}

	protected abstract Flux<CompletionItem> completeInternal(String content);

	protected P getParser(String input) {
		L lexer = getAntlrFactory().createLexer(CharStreams.fromString(input));
		P parser = getAntlrFactory().createParser(new CommonTokenStream(lexer));
		return parser;
	}

	protected Mono<SymbolTable> getSymbolTable() {
		return Mono.empty();
	}

	protected AntlrCompletionEngine getAntlrCompletionEngine(P parser) {
		return new DefaultAntlrCompletionEngine(parser);
	}

//	protected Collection<String> assistCompletions(String content) {
//		ArrayList<String> combletions = new ArrayList<String>();
//		P parser = getParser(content);
//
//		DefaultAntlrCompletionEngine core = new DefaultAntlrCompletionEngine(parser);
//		DefaultAntlrCompletionEngine.CandidatesCollection candidates = core
//				.collectCandidates(new Position(0, content.length() - 1), null);
//
//		log.debug("Candidates tokens {}", candidates.tokens);
//
//		for (Entry<Integer, List<Integer>> e : candidates.tokens.entrySet()) {
//			if (e.getKey() > 0) {
//				Vocabulary vocabulary = parser.getVocabulary();
//				String displayName = vocabulary.getDisplayName(e.getKey());
//				String literalName = vocabulary.getLiteralName(e.getKey());
//				String symbolicName = vocabulary.getSymbolicName(e.getKey());
//				combletions.add(displayName);
//				log.debug("Candidates token {} {} {} {}", e.getKey(), displayName, literalName, symbolicName);
//			}
//		}
//
//		return combletions;
//	}

}
