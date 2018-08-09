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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.Test2Grammar;
import org.springframework.dsl.Test2Lexer;
import org.springframework.dsl.antlr.support.DefaultAntlrCompletionEngine;
import org.springframework.dsl.antlr.symboltable.SymbolTable;
import org.springframework.dsl.domain.CompletionItem;
import org.springframework.dsl.domain.Position;
import org.springframework.dsl.model.LanguageId;
import org.springframework.util.ObjectUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Test2AntlrCompletioner extends AbstractAntlrCompletioner<Test2Lexer, Test2Grammar> {

	private static final Logger log = LoggerFactory.getLogger(Test2AntlrCompletioner.class);

	public Test2AntlrCompletioner() {
		super(TestAntrlUtils.TEST2_ANTRL_FACTORY);
	}

	@Override
	public List<LanguageId> getSupportedLanguageIds() {
		return Arrays.asList(TestAntrlUtils.TEST2_LANGUAGE_ID);
	}

	@Override
	protected Flux<CompletionItem> completeInternal(String content) {
		return Flux.fromIterable(assistCompletions(content))
				.flatMap(c -> {
					CompletionItem item = new CompletionItem();
					item.setLabel(c);
					return Mono.just(item);
				});
	}

	protected Collection<String> assistCompletions(String content) {


		// 1. build engine completions
		// 2. build symbol table
		// 3. tune final completions

		ArrayList<String> combletions = new ArrayList<String>();
		Test2Grammar parser = getParser(content);
//		P parser = getParser(content);

//		parser.getContext()

		HashSet<Integer> preferredRules = new HashSet<>(Arrays.asList(Test2Grammar.RULE_sourceId, Test2Grammar.RULE_targetId));
		DefaultAntlrCompletionEngine core = new DefaultAntlrCompletionEngine(parser, preferredRules, null);
		DefaultAntlrCompletionEngine.CandidatesCollection candidates = core
				.collectCandidates(new Position(9, 9), parser.definitions());

		log.debug("Candidates tokens {}", candidates.tokens);
		log.debug("Candidates rules {}", candidates.rules);

		parser = getParser(content);
		ParseTree tree = parser.definitions();
		Test2Visitor visitor = new Test2Visitor();
		AntlrParseResult<Object> result = visitor.visit(tree);

		SymbolTable symbolTable = result.getSymbolTable();
//		symbolTable.GLOBALS.getAllSymbols().stream().forEach(s -> {
//			log.info("D {}", s);
//		});

		for (Entry<Integer, List<Integer>> e : candidates.rules.entrySet()) {
			if (e.getKey() == Test2Grammar.RULE_sourceId) {
//				Set<String> symbolNames = symbolTable.GLOBALS.getSymbolNames();
//				combletions.addAll(symbolNames);
				symbolTable.GLOBALS.getAllSymbols().stream().forEach(s -> {
					log.info("D {}", s.getScope());
					if (ObjectUtils.nullSafeEquals(s.getScope().getName(), "org.springframework.statemachine.state.State")) {
						combletions.add(s.getName());
					}
				});
			}
		}


//		Test2Grammar.ID;
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
		return combletions;
	}

}
