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

import java.util.function.Function;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.springframework.dsl.antlr.AntlrFactory;
import org.springframework.dsl.antlr.AntlrParseResult;
import org.springframework.dsl.document.Document;

import reactor.core.publisher.Mono;

/**
 * Base implementation of a function creating {@link AntlrParseResult} from a
 * {@link Document}.
 *
 * @author Janne Valkealahti
 *
 * @param <T> the type of a result in {@link AntlrParseResult}
 * @param <L> the type of lexer
 * @param <P> the type of parser
 */
public abstract class AbstractAntlrParseResultFunction<T, L extends Lexer, P extends Parser>
		extends AntlrObjectSupport<L, P>
		implements Function<Document, Mono<? extends AntlrParseResult<T>>> {

	/**
	 * Instantiates a new abstract antlr parse result function.
	 *
	 * @param antlrFactory the antlr factory
	 */
	public AbstractAntlrParseResultFunction(AntlrFactory<L, P> antlrFactory) {
		super(antlrFactory);
	}

	@Override
	public Mono<? extends AntlrParseResult<T>> apply(Document document) {
		return Mono.empty();
	}
}
