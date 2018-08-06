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

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

public class ScopedSymbol extends Symbol {

	private List<Symbol> children = new ArrayList<>();

	public ScopedSymbol(String name) {
		super(name);
	}

	public void clear() {
		children.clear();
	}

	public Symbol resolve(String name, boolean localOnly) {
		for (Symbol child : children) {
			if (ObjectUtils.nullSafeEquals(child.getName(), name)) {
				return child;
			}
		}

		if(!localOnly) {
			if (getParent() instanceof ScopedSymbol) {
				return ((ScopedSymbol)getParent()).resolve(name, false);
			}
		}

		return null;
	}

	public void addSymbol(Symbol symbol) {
//		symbol.removeFromParent();

		children.add(symbol);
		symbol.setParent(this);
	}

//    public addSymbol(symbol: Symbol) {
//        symbol.removeFromParent();
//
//        // Check for duplicates first.
//        let symbolTable = this.symbolTable;
//        if (!symbolTable || !symbolTable.options.allowDuplicateSymbols) {
//            for (let child of this._children) {
//                if (child == symbol || (symbol.name.length > 0 && child.name == symbol.name)) {
//                    let name = symbol.name;
//                    if (name.length == 0)
//                        name = "<anonymous>";
//                    throw new DuplicateSymbolError("Attempt to add duplicate symbol '" + name + "'");
//                }
//            }
//        }
//
//        this._children.push(symbol);
//        symbol.setParent(this);
//    }


}
