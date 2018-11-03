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

public class LocalScopeTests {

	@Test
	public void test1() {
		LocalScope scope1 = new LocalScope(null);
		LocalScope scope2 = new LocalScope(scope1);
		scope1.nest(scope2);

		assertThat(scope1.getNestedScopes()).hasSize(1);

		ClassSymbol classA = new ClassSymbol("classA");
		scope2.define(classA);
		assertThat(scope2.getAllSymbols()).hasSize(1);
		assertThat(scope1.getAllSymbols()).hasSize(1);
	}
}
