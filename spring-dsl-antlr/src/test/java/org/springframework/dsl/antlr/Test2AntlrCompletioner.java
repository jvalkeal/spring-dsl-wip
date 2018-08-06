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
import java.util.List;
import java.util.Map.Entry;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.dsl.Test2Grammar;
import org.springframework.dsl.Test2Lexer;
import org.springframework.dsl.antlr.support.DefaultAntlrCompletionEngine;
import org.springframework.dsl.domain.CompletionItem;
import org.springframework.dsl.model.LanguageId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Test2AntlrCompletioner extends AbstractAntlrCompletioner<Test2Lexer, Test2Grammar> {

	public Test2AntlrCompletioner() {
		super(new Test2AntlrFactory());
	}

	@Override
	public List<LanguageId> getSupportedLanguageIds() {
		return Arrays.asList(LanguageId.languageId("test2", "Antlr test2 Language"));
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

	@Override
	protected Test2Grammar getParser(String input) {
		Test2Lexer lexer = getAntlrFactory().createLexer(CharStreams.fromString(input));
		Test2Grammar parser = getAntlrFactory().createParser(new CommonTokenStream(lexer));
		parser.definitions();
		return parser;
	}

	@Override
	protected void symbolTable(Test2Grammar parser) {
		ParseTree tree = parser.definitions();
		Test2Visitor visitor = new Test2Visitor();
		AntlrParseResult<Object> result = visitor.visit(tree);
	}

	protected Collection<String> xxx(String content, Test2Grammar parser) {
		ArrayList<String> combletions = new ArrayList<String>();

		DefaultAntlrCompletionEngine core = new DefaultAntlrCompletionEngine(parser);
		DefaultAntlrCompletionEngine.CandidatesCollection candidates = core.collectCandidates(null, null);


		for (Entry<Integer, List<Integer>> e : candidates.tokens.entrySet()) {
			if (e.getKey() > 0) {
				Vocabulary vocabulary = parser.getVocabulary();
				String displayName = vocabulary.getDisplayName(e.getKey());
				String literalName = vocabulary.getLiteralName(e.getKey());
				String symbolicName = vocabulary.getSymbolicName(e.getKey());
				combletions.add(displayName);
			}
		}

		return combletions;
	}


	private static class Test2AntlrFactory implements AntlrFactory<Test2Lexer, Test2Grammar> {

		@Override
		public Test2Lexer createLexer(CharStream input) {
			return new Test2Lexer(input);
		}

		@Override
		public Test2Grammar createParser(TokenStream tokenStream) {
			return new Test2Grammar(tokenStream);
		}
	}
}
