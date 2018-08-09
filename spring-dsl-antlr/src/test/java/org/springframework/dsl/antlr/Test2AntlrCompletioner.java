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

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
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
		ArrayList<String> completions = new ArrayList<String>();
		Test2Grammar parser = getParser(content);

		HashSet<Integer> preferredRules = new HashSet<>(Arrays.asList(Test2Grammar.RULE_sourceId, Test2Grammar.RULE_targetId));
		DefaultAntlrCompletionEngine core = new DefaultAntlrCompletionEngine(parser, preferredRules, null);
		AntlrCompletionResult candidates = core
				.collectResults(new Position(9, 9), parser.definitions());

		parser = getParser(content);
		ParseTree tree = parser.definitions();
		Test2Visitor visitor = new Test2Visitor();
		AntlrParseResult<Object> result = visitor.visit(tree);

		SymbolTable symbolTable = result.getSymbolTable();

		for (Entry<Integer, List<Integer>> e : candidates.getRules().entrySet()) {
			if (e.getKey() == Test2Grammar.RULE_sourceId) {
				symbolTable.GLOBALS.getAllSymbols().stream().forEach(s -> {
					if (ObjectUtils.nullSafeEquals(s.getScope().getName(), "org.springframework.statemachine.state.State")) {
						completions.add(s.getName());
					}
				});
			}
		}

		for (Entry<Integer, List<Integer>> e : candidates.getTokens().entrySet()) {
			if (e.getKey() > 0) {
				Vocabulary vocabulary = parser.getVocabulary();
				String displayName = vocabulary.getDisplayName(e.getKey());
				completions.add(displayName);
			}
		}

		return completions;
	}
}
