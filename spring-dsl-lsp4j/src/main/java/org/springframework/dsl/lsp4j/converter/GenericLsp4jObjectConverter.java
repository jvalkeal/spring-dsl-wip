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
package org.springframework.dsl.lsp4j.converter;

import java.util.HashSet;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.util.ClassUtils;

/**
 * A {@link GenericConverter} able to convert between {@code LSP} domain classes
 * defined in {@code LSP4J} and {@code Spring DSL}.
 *
 * @author Janne Valkealahti
 *
 */
public class GenericLsp4jObjectConverter implements GenericConverter {

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> convertiblePairs = new HashSet<ConvertiblePair>();

		convertiblePairs.add(new ConvertiblePair(InitializeParams.class, org.eclipse.lsp4j.InitializeParams.class));
		convertiblePairs.add(new ConvertiblePair(org.eclipse.lsp4j.InitializeParams.class, InitializeParams.class));

		convertiblePairs.add(new ConvertiblePair(InitializeResult.class, org.eclipse.lsp4j.InitializeResult.class));
		convertiblePairs.add(new ConvertiblePair(org.eclipse.lsp4j.InitializeResult.class, InitializeResult.class));

		convertiblePairs.add(new ConvertiblePair(DidChangeTextDocumentParams.class,
				org.eclipse.lsp4j.DidChangeTextDocumentParams.class));
		convertiblePairs.add(new ConvertiblePair(org.eclipse.lsp4j.DidChangeTextDocumentParams.class,
				DidChangeTextDocumentParams.class));

		return convertiblePairs;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (ClassUtils.isAssignable(InitializeResult.class, sourceType.getType())) {
			return ConverterUtils.toInitializeResult((InitializeResult) source);
		} else if (ClassUtils.isAssignable(InitializeParams.class, sourceType.getType())) {
			return ConverterUtils.toInitializeParams((InitializeParams) source);
		} else if (ClassUtils.isAssignable(DidChangeTextDocumentParams.class, sourceType.getType())) {
			return ConverterUtils.toDidChangeTextDocumentParams((DidChangeTextDocumentParams) source);
		} else if (ClassUtils.isAssignable(org.eclipse.lsp4j.InitializeParams.class, sourceType.getType())) {
			return ConverterUtils.toInitializeParams((org.eclipse.lsp4j.InitializeParams) source);
		}
		throw new IllegalArgumentException();
	}
}
