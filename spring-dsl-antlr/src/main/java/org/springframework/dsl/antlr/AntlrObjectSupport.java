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

import java.io.IOException;
import java.io.StringReader;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.springframework.dsl.DslException;
import org.springframework.util.Assert;

/**
 * Base class for language support for {@code ANTLR}.
 *
 * @author Janne Valkealahti
 *
 * @param <L> the type of lexer
 * @param <P> the type of parser
 *
 */
public abstract class AntlrObjectSupport<L extends Lexer, P extends Parser> {

	private final AntlrFactory<L, P> antlrFactory;

	/**
	 * Instantiates a new abstract antlr linter.
	 *
	 * @param antlrFactory the antlr factory
	 */
	public AntlrObjectSupport(AntlrFactory<L, P> antlrFactory) {
		Assert.notNull(antlrFactory, "antlrFactory must be set");
		this.antlrFactory = antlrFactory;
	}

	/**
	 * Gets the antlr factory.
	 *
	 * @return the antlr factory
	 */
	protected AntlrFactory<L, P> getAntlrFactory() {
		return antlrFactory;
	}

	/**
	 * Utility method to convert a {@link String} to a {@link CharStream}.
	 *
	 * @param content the content
	 * @return the char stream
	 */
	protected static CharStream stringToCharStream(String content) {
		try {
			return CharStreams.fromReader(new StringReader(content));
		} catch (IOException e) {
			throw new DslException(e);
		}
	}
}
