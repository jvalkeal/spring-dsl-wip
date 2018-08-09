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
package org.springframework.dsl.antlr.symboltable;

import java.util.Collections;
import java.util.List;

/**
 * An abstract base class that houses common functionality for symbols like
 * classes and functions that are both symbols and scopes. There is some common
 * cut and paste functionality with {@link BaseSymbol} because of a lack of
 * multiple inheritance in Java but it is minimal.
 */
public abstract class SymbolWithScope extends BaseScope implements Symbol, Scope {

	protected final String name; // All symbols at least have a name
	protected int index; // insertion order from 0; compilers often need this

	public SymbolWithScope(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Scope getScope() {
		return enclosingScope;
	}

	@Override
	public void setScope(Scope scope) {
		setEnclosingScope(scope);
	}

	@Override
	public Scope getEnclosingScope() {
		return enclosingScope;
	}

	/**
	 * Return the name prefixed with the name of its enclosing scope using '.' (dot)
	 * as the scope separator.
	 */
	public String getQualifiedName() {
		return enclosingScope.getName() + "." + name;
	}

	/** Return the name prefixed with the name of its enclosing scope. */
	public String getQualifiedName(String scopePathSeparator) {
		return enclosingScope.getName() + scopePathSeparator + name;
	}

	/**
	 * Return the fully qualified name includes all scopes from the root down to
	 * this particular symbol.
	 */
	public String getFullyQualifiedName(String scopePathSeparator) {
		List<Scope> path = getEnclosingPathToRoot();
		Collections.reverse(path);
		return Utils.joinScopeNames(path, scopePathSeparator);
	}

	@Override
	public int getInsertionOrderNumber() {
		return index;
	}

	@Override
	public void setInsertionOrderNumber(int i) {
		this.index = i;
	}

	@Override
	public int getNumberOfSymbols() {
		return symbols.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Symbol)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		return name.equals(((Symbol) obj).getName());
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
