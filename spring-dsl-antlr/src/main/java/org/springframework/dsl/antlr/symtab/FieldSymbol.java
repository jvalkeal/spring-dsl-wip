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
package org.springframework.dsl.antlr.symtab;

/**
 * A field symbol is just a variable that lives inside an aggregate like a class
 * or struct.
 */
public class FieldSymbol extends VariableSymbol implements MemberSymbol {

	protected int slot;

	public FieldSymbol(String name) {
		super(name);
	}

	@Override
	public int getSlotNumber() {
		return slot;
	}
}
