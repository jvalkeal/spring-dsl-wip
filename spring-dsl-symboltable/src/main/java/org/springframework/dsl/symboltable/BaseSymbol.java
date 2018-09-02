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
package org.springframework.dsl.symboltable;

import java.util.Collections;
import java.util.List;

/**
 * An abstract base class used to house common functionality. You can associate
 * a node in the parse tree that is responsible for defining this symbol.
 *
 * @author Original ANTLR Authors
 * @author Janne Valkealahti
 *
 */
public abstract class BaseSymbol implements Symbol {

	protected final String name; // All symbols at least have a name
	protected Type type; // If language statically typed, record type
	protected Scope scope; // All symbols know what scope contains them.
	protected int lexicalOrder; // order seen or insertion order from 0; compilers often need this

	public BaseSymbol(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Scope getScope() {
		return scope;
	}

	@Override
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
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

	@Override
	public int getInsertionOrderNumber() {
		return lexicalOrder;
	}

	@Override
	public void setInsertionOrderNumber(int i) {
		this.lexicalOrder = i;
	}

	public String getFullyQualifiedName(String scopePathSeparator) {
		List<Scope> path = scope.getEnclosingPathToRoot();
		Collections.reverse(path);
		String qualifier = Utils.joinScopeNames(path, scopePathSeparator);
		return qualifier + scopePathSeparator + name;
	}

	@Override
	public String toString() {
		String s = "";
		if (scope != null)
			s = scope.getName() + ".";
		if (type != null) {
			String ts = type.toString();
			if (type instanceof SymbolWithScope) {
				ts = ((SymbolWithScope) type).getFullyQualifiedName(".");
			}
			return '<' + s + getName() + ":" + ts + '>';
		}
		return s + getName();
	}
}
