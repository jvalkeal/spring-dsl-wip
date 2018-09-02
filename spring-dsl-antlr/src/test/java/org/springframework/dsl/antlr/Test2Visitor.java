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

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.springframework.dsl.Test2Grammar.DefinitionsContext;
import org.springframework.dsl.Test2Grammar.SourceIdContext;
import org.springframework.dsl.Test2Grammar.TargetIdContext;
import org.springframework.dsl.Test2GrammarBaseVisitor;
import org.springframework.dsl.reconcile.ReconcileProblem;
import org.springframework.dsl.symboltable.ClassSymbol;
import org.springframework.dsl.symboltable.FieldSymbol;
import org.springframework.dsl.symboltable.SymbolTable;

/**
 * {@link ParseTreeVisitor} for {@code ANTLR test2 language}.
 *
 * @author Janne Valkealahti
 *
 */
public class Test2Visitor extends Test2GrammarBaseVisitor<AntlrParseResult<Object>> {

	@Override
	public AntlrParseResult<Object> visitDefinitions(DefinitionsContext ctx) {
		SymbolTable symbolTable = new SymbolTable();
		ClassSymbol stateClassSymbol = new ClassSymbol("org.springframework.statemachine.state.State");
		ClassSymbol transitionClassSymbol = new ClassSymbol("org.springframework.statemachine.transition.Transition");
		symbolTable.defineGlobalSymbol(stateClassSymbol);
		symbolTable.defineGlobalSymbol(transitionClassSymbol);

		ctx.machineObjectList().state().forEach(stateContext -> {
			ClassSymbol classSymbol = new ClassSymbol(stateContext.id().getText());
			stateClassSymbol.define(classSymbol);
		});

		ctx.machineObjectList().transition().forEach(transitionContext -> {
			ClassSymbol classSymbol = new ClassSymbol(transitionContext.id().getText());
			transitionClassSymbol.define(classSymbol);
			transitionContext.transitionParameters().transitionParameter().stream().forEach(transitionParameter -> {
				SourceIdContext sourceId = transitionParameter.transitionType().sourceId();
				TargetIdContext targetId = transitionParameter.transitionType().targetId();

				if (sourceId != null) {
					FieldSymbol fieldSymbol = new FieldSymbol(transitionParameter.transitionType().SOURCE().getText());
					classSymbol.define(fieldSymbol);
				}
				if (targetId != null) {
					FieldSymbol fieldSymbol = new FieldSymbol(transitionParameter.transitionType().TARGET().getText());
					classSymbol.define(fieldSymbol);
				}
			});
		});


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
	}
}
