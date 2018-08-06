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

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;

public class SymbolTable extends ScopedSymbol {

	private final Set<SymbolTable> dependencies = new HashSet<>();

	public SymbolTable(String name) {
		super(name);
	}

	@Override
	public void clear() {
		super.clear();
		this.dependencies.clear();
	}

    public void addDependencies(SymbolTable[]... symbolTables) {
    	for (SymbolTable[] symbolTable : symbolTables) {
    		for (SymbolTable table : symbolTable) {
    			dependencies.add(table);
    		}
    	}
    }

    public void removeDependency(SymbolTable symbolTable) {
    	dependencies.remove(symbolTable);
    }

    public <T extends Symbol> void addNewSymbolOfType(T symbol, ScopedSymbol parent) {

    	if (parent == null ||parent == this) {
    		this.addSymbol(symbol);
    	} else {
    		parent.addSymbol(symbol);
    	}
    }

    public Symbol symbolWithContext(ParseTree context) {

//        let symbols = this.getAllSymbols(Symbol);
//        for (let symbol of symbols) {
//            let result = findRecursive(symbol);
//            if (result) {
//                return result;
//            }
//        }
//
//        for (let dependency of this.dependencies) {
//            symbols = dependency.getAllSymbols(Symbol);
//            for (let symbol of symbols) {
//                let result = findRecursive(symbol);
//                if (result) {
//                    return result;
//                }
//            }
//        }


    	return null;
    }

    @Override
	public Symbol resolve(String name, boolean localOnly) {

    	Symbol result = super.resolve(name, localOnly);
    	if (result == null && !localOnly) {
    		for (SymbolTable dependency : dependencies) {
    			result = dependency.resolve(name, false);
    			if (result != null) {
    				break;
    			}
    		}
    	}
    	return result;
    }

}
