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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.dsl.symboltable.ClassSymbol;
import org.springframework.dsl.symboltable.FieldSymbol;
import org.springframework.dsl.symboltable.PrimitiveType;

public class SymbolTableTests {

	@Test
	public void testClassWithFields() {
		//	private static class TestClass1 {
		//		public int variable1;
		//		protected Long variable2;
		//		private String variable3;
		//	}

		ClassSymbol cSymbol = new ClassSymbol("TestClass1");

		FieldSymbol fSymbol1 = new FieldSymbol("variable1");
		fSymbol1.setType(new PrimitiveType("int"));

		FieldSymbol fSymbol2 = new FieldSymbol("variable2");
		fSymbol2.setType(new ClassSymbol("Long"));

		FieldSymbol fSymbol3 = new FieldSymbol("variable3");
		fSymbol3.setType(new ClassSymbol("String"));

		cSymbol.define(fSymbol1);
		cSymbol.define(fSymbol2);
		cSymbol.define(fSymbol3);

		assertThat(fSymbol1.getScope()).isSameAs(cSymbol);
		assertThat(fSymbol1.getType().getName()).isEqualTo("int");
		assertThat(fSymbol1.getType()).isInstanceOf(PrimitiveType.class);

		assertThat(fSymbol2.getScope()).isSameAs(cSymbol);
		assertThat(fSymbol2.getType().getName()).isEqualTo("Long");
		assertThat(fSymbol2.getType()).isInstanceOf(ClassSymbol.class);

		assertThat(fSymbol3.getScope()).isSameAs(cSymbol);
		assertThat(fSymbol3.getType().getName()).isEqualTo("String");
		assertThat(fSymbol3.getType()).isInstanceOf(ClassSymbol.class);

		assertThat(cSymbol.getAllSymbols()).hasSize(3);
	}

}
