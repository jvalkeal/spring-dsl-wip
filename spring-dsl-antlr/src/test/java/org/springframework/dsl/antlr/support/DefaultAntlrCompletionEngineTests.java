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
package org.springframework.dsl.antlr.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;
import org.springframework.dsl.Test1Lexer;
import org.springframework.dsl.Test1Parser;
import org.springframework.dsl.Test2Grammar;
import org.springframework.dsl.Test2Lexer;
import org.springframework.dsl.antlr.AntlrFactory;
import org.springframework.dsl.antlr.support.DefaultAntlrCompletionEngine;

/**
 * Tests for {@link DefaultAntlrCompletionEngine}. These test depends on an
 * antlr {@code Test*.g4} test grammars. {@link DefaultAntlrCompletionEngine}
 * depends on a real code generated {@link Parser} and {@link Lexer} classes
 * from a {@code grammar}. This generation is done during a test phase.
 *
 * @author Janne Valkealahti
 *
 */
public class DefaultAntlrCompletionEngineTests {

	@Test
	public void xxx1() {

		AntlrFactory<Test1Lexer, Test1Parser> antlrFactory = new AntlrFactory<Test1Lexer, Test1Parser>() {

			@Override
			public Test1Parser createParser(TokenStream tokenStream) {
				return new Test1Parser(tokenStream);
			}

			@Override
			public Test1Lexer createLexer(CharStream input) {
				return new Test1Lexer(input);
			}
		};

		String input = "";

		Test1Lexer lexer = antlrFactory.createLexer(CharStreams.fromString(input));
		Test1Parser parser = antlrFactory.createParser(new CommonTokenStream(lexer));

		parser.r();

//		CodeCompletionCore core = new CodeCompletionCore(parser, null, null);
//		CodeCompletionCore.CandidatesCollection candidates = core.collectCandidates(0, null);
		DefaultAntlrCompletionEngine core = new DefaultAntlrCompletionEngine(parser);
		DefaultAntlrCompletionEngine.CandidatesCollection candidates = core.collectCandidates(0, null);

		assertThat(candidates).isNotNull();
		assertThat(candidates.tokens).isNotNull();
		assertThat(candidates.tokens.size()).isEqualTo(1);
		assertThat(candidates.tokens.containsKey(Test1Lexer.AB)).isTrue();

	}

	@Test
	public void xxx2() {

		AntlrFactory<Test2Lexer, Test2Grammar> antlrFactory = new AntlrFactory<Test2Lexer, Test2Grammar>() {

			@Override
			public Test2Grammar createParser(TokenStream tokenStream) {
				return new Test2Grammar(tokenStream);
			}

			@Override
			public Test2Lexer createLexer(CharStream input) {
				return new Test2Lexer(input);
			}
		};

		String input = "";

		Test2Lexer lexer = antlrFactory.createLexer(CharStreams.fromString(input));
		Test2Grammar parser = antlrFactory.createParser(new CommonTokenStream(lexer));

		parser.definitions();

		DefaultAntlrCompletionEngine core = new DefaultAntlrCompletionEngine(parser);
		DefaultAntlrCompletionEngine.CandidatesCollection candidates = core.collectCandidates(0, null);

		assertThat(candidates).isNotNull();
		assertThat(candidates.tokens).isNotNull();
		assertThat(candidates.tokens.size()).isEqualTo(4);
		assertThat(candidates.tokens.containsKey(Test2Lexer.STATEMACHINE)).isTrue();
		assertThat(candidates.tokens.containsKey(Test2Lexer.STATE)).isTrue();
		assertThat(candidates.tokens.containsKey(Test2Lexer.TRANSITION)).isTrue();
	}


	@Test
	public void xxx3() {

		AntlrFactory<Test2Lexer, Test2Grammar> antlrFactory = new AntlrFactory<Test2Lexer, Test2Grammar>() {

			@Override
			public Test2Grammar createParser(TokenStream tokenStream) {
				return new Test2Grammar(tokenStream);
			}

			@Override
			public Test2Lexer createLexer(CharStream input) {
				return new Test2Lexer(input);
			}
		};

		String input = "statemachine M1 {";

		Test2Lexer lexer = antlrFactory.createLexer(CharStreams.fromString(input));
		Test2Grammar parser = antlrFactory.createParser(new CommonTokenStream(lexer));

		parser.definitions();

		DefaultAntlrCompletionEngine core = new DefaultAntlrCompletionEngine(parser);
		DefaultAntlrCompletionEngine.CandidatesCollection candidates = core.collectCandidates(16, null);

		assertThat(candidates).isNotNull();
		assertThat(candidates.tokens).isNotNull();
		assertThat(candidates.tokens.size()).isEqualTo(3);
		assertThat(candidates.tokens.containsKey(Test2Lexer.STATE)).isTrue();
		assertThat(candidates.tokens.containsKey(Test2Lexer.TRANSITION)).isTrue();
	}

	@Test
	public void xxx4() {

		AntlrFactory<Test2Lexer, Test2Grammar> antlrFactory = new AntlrFactory<Test2Lexer, Test2Grammar>() {

			@Override
			public Test2Grammar createParser(TokenStream tokenStream) {
				return new Test2Grammar(tokenStream);
			}

			@Override
			public Test2Lexer createLexer(CharStream input) {
				return new Test2Lexer(input);
			}
		};

		String input = "statemachine M1 { state S1 {";

		Test2Lexer lexer = antlrFactory.createLexer(CharStreams.fromString(input));
		Test2Grammar parser = antlrFactory.createParser(new CommonTokenStream(lexer));

		parser.definitions();

		DefaultAntlrCompletionEngine core = new DefaultAntlrCompletionEngine(parser);
		DefaultAntlrCompletionEngine.CandidatesCollection candidates = core.collectCandidates(16, null);

		assertThat(candidates).isNotNull();
		assertThat(candidates.tokens).isNotNull();
		assertThat(candidates.tokens.size()).isEqualTo(3);
		assertThat(candidates.tokens.containsKey(Test2Lexer.INITIAL)).isTrue();
		assertThat(candidates.tokens.containsKey(Test2Lexer.END)).isTrue();
	}

	@Test
	public void xxx5() {

		AntlrFactory<Test2Lexer, Test2Grammar> antlrFactory = new AntlrFactory<Test2Lexer, Test2Grammar>() {

			@Override
			public Test2Grammar createParser(TokenStream tokenStream) {
				return new Test2Grammar(tokenStream);
			}

			@Override
			public Test2Lexer createLexer(CharStream input) {
				return new Test2Lexer(input);
			}
		};

		String input = "statemachine M1 { state S1 {} }";

		Test2Lexer lexer = antlrFactory.createLexer(CharStreams.fromString(input));
		Test2Grammar parser = antlrFactory.createParser(new CommonTokenStream(lexer));

		parser.definitions();

		DefaultAntlrCompletionEngine core = new DefaultAntlrCompletionEngine(parser);
		DefaultAntlrCompletionEngine.CandidatesCollection candidates = core.collectCandidates(1, null);

		assertThat(candidates).isNotNull();
		assertThat(candidates.tokens).isNotNull();
		assertThat(candidates.tokens.size()).isEqualTo(1);
		assertThat(candidates.tokens.containsKey(Test2Lexer.ID)).isTrue();
	}
}
