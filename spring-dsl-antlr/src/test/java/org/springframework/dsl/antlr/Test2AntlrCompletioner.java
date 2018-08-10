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
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.dsl.Test2Grammar;
import org.springframework.dsl.Test2Lexer;
import org.springframework.dsl.antlr.support.DefaultAntlrCompletionEngine;
import org.springframework.dsl.antlr.symboltable.SymbolTable;
import org.springframework.dsl.domain.CompletionItem;
import org.springframework.dsl.domain.Position;
import org.springframework.dsl.model.LanguageId;
import org.springframework.dsl.service.Completioner;
import org.springframework.util.ObjectUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link Completioner} for {@code ANTLR test2 language}.
 *
 * @author Janne Valkealahti
 *
 */
public class Test2AntlrCompletioner extends AbstractAntlrCompletioner<Test2Lexer, Test2Grammar> {

	public Test2AntlrCompletioner() {
		super(TestAntrlUtils.TEST2_ANTRL_FACTORY);
	}

	@Override
	public List<LanguageId> getSupportedLanguageIds() {
		return Arrays.asList(TestAntrlUtils.TEST2_LANGUAGE_ID);
	}

	@Override
	protected Flux<CompletionItem> completeInternal(String content, Position position) {
		return getCompletions(content, position)
				.flatMap(c -> {
					CompletionItem item = new CompletionItem();
					item.setLabel(c);
					return Mono.just(item);
				});
	}

	@Override
	protected AntlrCompletionEngine getAntlrCompletionEngine(Test2Grammar parser) {
		HashSet<Integer> preferredRules = new HashSet<>(Arrays.asList(Test2Grammar.RULE_sourceId, Test2Grammar.RULE_targetId));
		return new DefaultAntlrCompletionEngine(parser, preferredRules, null);
	}

	@Override
	protected ParserRuleContext getParserRuleContext(Test2Grammar parser) {
		return parser.definitions();
	}

	@Override
	protected Flux<String> completeRules(AntlrCompletionResult completionResult, SymbolTable symbolTable) {
		return Flux.defer(() -> {
			ArrayList<String> completions = new ArrayList<String>();
			for (Entry<Integer, List<Integer>> e : completionResult.getRules().entrySet()) {
				if (e.getKey() == Test2Grammar.RULE_sourceId) {
					symbolTable.getAllSymbols().stream().forEach(s -> {
						if (ObjectUtils.nullSafeEquals(s.getScope().getName(), "org.springframework.statemachine.state.State")) {
							completions.add(s.getName());
						}
					});
				}
			}
			return Flux.fromIterable(completions);
		});
	}

	@Override
	protected Flux<String> completeTokens(AntlrCompletionResult completionResult, Test2Grammar parser) {
		return Flux.defer(() -> {
			ArrayList<String> completions = new ArrayList<String>();
			for (Entry<Integer, List<Integer>> e : completionResult.getTokens().entrySet()) {
				if (e.getKey() > 0) {
					Vocabulary vocabulary = parser.getVocabulary();
					String displayName = vocabulary.getDisplayName(e.getKey());
					completions.add(displayName);
				}
			}
			return Flux.fromIterable(completions);
		});
	}

	@Override
	protected SymbolTable getSymbolTable(String content, Test2Grammar parser) {
		ParseTree tree = parser.definitions();
		Test2Visitor visitor = new Test2Visitor();
		AntlrParseResult<Object> result = visitor.visit(tree);
		return result.getSymbolTable();
	}
}
