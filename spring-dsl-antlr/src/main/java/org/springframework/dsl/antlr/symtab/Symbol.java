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
package org.springframework.dsl.antlr.symtab;

/**
 * A generic programming language symbol. A symbol has to have a name and a
 * scope in which it lives. It also helps to know the order in which symbols are
 * added to a scope because this often translates to register or parameter
 * numbers.
 */
public interface Symbol {

	String getName();

	Scope getScope();

	void setScope(Scope scope); // set scope (not enclosing) for this symbol; who contains it?

	int getInsertionOrderNumber(); // index showing insertion order from 0

	void setInsertionOrderNumber(int i);

	// to satisfy adding symbols to sets, hashtables
	@Override
	int hashCode();

	@Override
	boolean equals(Object o);
}
