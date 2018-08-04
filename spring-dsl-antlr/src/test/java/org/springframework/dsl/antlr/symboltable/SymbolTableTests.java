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

import org.junit.Test;

public class SymbolTableTests {

	@Test
	public void test1() {

	}

	private static SymbolTable createSymbolTable(String name, int[] counts) {
		SymbolTable symbolTable = new SymbolTable(name);

		for (int i = 0; i < counts[0]; ++i) {
			ClassSymbol classSymbol = new ClassSymbol();

//			symbolTable.addSymbol(symbol, parent);

//			let classSymbol = symbolTable.addNewSymbolOfType(c3.ClassSymbol, nsSymbols[nsIndex], "class" + i);
		}


//	    for (let i = 0; i < counts[0]; ++i) {
//	        let classSymbol = symbolTable.addNewSymbolOfType(c3.ClassSymbol, nsSymbols[nsIndex], "class" + i);
//
//	        for (let j = 0; j < counts[2]; ++j) {
//	            let field = symbolTable.addNewSymbolOfType(c3.FieldSymbol, classSymbol, "field" + j);
//	        }
//
//	        for (let j = 0; j < counts[1]; ++j) {
//	            let method = symbolTable.addNewSymbolOfType(c3.MethodSymbol, classSymbol, "method" + j);
//
//	            // Blocks are created and added in an alternative way.
//	            let block1 = symbolTable.addNewSymbolOfType(c3.BlockSymbol, undefined, "block1"); // Block at top level.
//	            symbolTable.addNewSymbolOfType(c3.VariableSymbol, block1, "var1", 17, c3.FundamentalType.integerType);
//	            let block2 = symbolTable.addNewSymbolOfType(c3.BlockSymbol, undefined, "block2");
//	            let symbol = symbolTable.addNewSymbolOfType(c3.VariableSymbol, block2, "var1", 3.142, c3.FundamentalType.floatType);
//	            if (j == 1) {
//	                symbol.context = dummyNode;
//	            }
//
//	            // Now move the blocks from global level to the method.
//	            method.addSymbol(block1);
//	            method.addSymbol(block2);
//	        }
//
//	        ++nsIndex;
//	        if (nsIndex == nsCount)
//	            nsIndex = 0;
//	    }


		return symbolTable;
	}

}
