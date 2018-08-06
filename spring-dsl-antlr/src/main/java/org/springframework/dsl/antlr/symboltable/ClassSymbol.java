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

public class ClassSymbol extends ScopedSymbol implements Type {

	private ReferenceKind referenceKind;
	private List<ClassSymbol> superClasses = new ArrayList<>();

	public ClassSymbol(String name, ReferenceKind referenceKind, ClassSymbol superClass) {
		super(name);
		this.referenceKind = referenceKind;
		if (superClass != null) {
			this.superClasses.add(superClass);
		}
	}

	@Override
	public List<Type> getBaseTypes() {
		return null;
	}

	@Override
	public TypeKind getTypeKind() {
		return TypeKind.Class;
	}

	@Override
	public ReferenceKind getReferenceKind() {
		return referenceKind;
	}

}
