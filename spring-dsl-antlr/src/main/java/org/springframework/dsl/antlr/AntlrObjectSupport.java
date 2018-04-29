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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dsl.DslException;
import org.springframework.util.Assert;

/**
 * Base class for language support for {@code ANTLR}.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class AntlrObjectSupport {

	private static final Log log = LogFactory.getLog(AntlrObjectSupport.class);
	private final AntlrFactory antlrFactory;

	/**
	 * Instantiates a new abstract antlr linter.
	 *
	 * @param antlrFactory the antlr factory
	 */
	public AntlrObjectSupport(AntlrFactory antlrFactory) {
		Assert.notNull(antlrFactory, "antlrFactory must be set");
		this.antlrFactory = antlrFactory;
	}

	/**
	 * Gets the antlr factory.
	 *
	 * @return the antlr factory
	 */
	protected AntlrFactory getAntlrFactory() {
		return antlrFactory;
	}

	protected static CharStream stringToCharStream(String content) {
		try {
			return CharStreams.fromReader(new StringReader(content));
		} catch (IOException e) {
			throw new DslException(e);
		}
	}

}
