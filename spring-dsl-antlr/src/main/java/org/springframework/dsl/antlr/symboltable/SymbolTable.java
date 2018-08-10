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

import java.util.List;

/**
 * A marginally useful object to track predefined and global scopes.
 * 
 * @author Original ANTLR Authors
 * @author Janne Valkealahti
 * 
 */
public class SymbolTable {

	public static final Type INVALID_TYPE = new InvalidType();

	private BaseScope PREDEFINED = new PredefinedScope();
	private GlobalScope GLOBALS = new GlobalScope(PREDEFINED);

	public SymbolTable() {
	}

	public void initTypeSystem() {
	}

	public void definePredefinedSymbol(Symbol s) {
		PREDEFINED.define(s);
	}

	public void defineGlobalSymbol(Symbol s) {
		GLOBALS.define(s);
	}
	
	/**
	 * Return all {@link Symbol}'s this table knows about.
	 * 
	 * @return all known symbols.
	 */
	public List<? extends Symbol> getAllSymbols() {
		return GLOBALS.getAllSymbols();
	}
	
}
