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

import org.springframework.dsl.Test2Grammar.DefinitionsContext;
import org.springframework.dsl.Test2Grammar.IdContext;
import org.springframework.dsl.Test2Grammar.MachineObjectListContext;
import org.springframework.dsl.Test2Grammar.StatemachineContext;

import java.util.List;

import org.springframework.dsl.Test2GrammarBaseVisitor;
import org.springframework.dsl.antlr.symboltable.ClassSymbol;
import org.springframework.dsl.antlr.symboltable.FieldSymbol;
import org.springframework.dsl.antlr.symboltable.SymbolTable;
import org.springframework.dsl.reconcile.ReconcileProblem;

public class Test2Visitor extends Test2GrammarBaseVisitor<AntlrParseResult<Object>> {


	@Override
	public AntlrParseResult<Object> visitDefinitions(DefinitionsContext ctx) {

		SymbolTable symbolTable = new SymbolTable("main");


		StatemachineContext statemachineContext = ctx.statemachine();
		IdContext idContext = statemachineContext.id();
		MachineObjectListContext machineObjectListContext = statemachineContext.machineObjectList();

		ClassSymbol classSymbol = new ClassSymbol("statemachine", null, null);
		FieldSymbol fieldSymbol = new FieldSymbol("id");
		fieldSymbol.setValue(idContext.getText());

		symbolTable.addNewSymbolOfType(classSymbol, null);
		symbolTable.addNewSymbolOfType(fieldSymbol, classSymbol);

		return new AntlrParseResult<Object>() {

			@Override
			public SymbolTable getSymbolTable() {
				return symbolTable;
			}

			@Override
			public Object getResult() {
				return null;
			}

			@Override
			public List<ReconcileProblem> getReconcileProblems() {
				return null;
			}
		};
//		return super.visitDefinitions(ctx);
	}

}
