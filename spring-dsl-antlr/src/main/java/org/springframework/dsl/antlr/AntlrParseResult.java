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

import org.springframework.dsl.domain.CompletionItem;
import org.springframework.dsl.domain.Position;
import org.springframework.dsl.service.reconcile.ReconcileProblem;
import org.springframework.dsl.symboltable.SymbolTable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface representing a parsing result from a {@link AntlrParseService}.
 *
 * @author Janne Valkealahti
 *
 * @param <T> the type of a result
 */
public interface AntlrParseResult<T> {

	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	Mono<T> getResult();

	/**
	 * Gets the symbol table.
	 *
	 * @return the symbol table
	 */
	default Mono<SymbolTable> getSymbolTable() {
		return Mono.empty();
	}

	/**
	 * Gets the reconcile problems.
	 *
	 * @return the reconcile problems
	 */
	default Flux<ReconcileProblem> getReconcileProblems() {
		return Flux.empty();
	}

	/**
	 * Gets the completion items.
	 *
	 * @param position the position
	 * @return the completion items
	 */
	default Flux<CompletionItem> getCompletionItems(Position position) {
		return Flux.empty();
	}
}
