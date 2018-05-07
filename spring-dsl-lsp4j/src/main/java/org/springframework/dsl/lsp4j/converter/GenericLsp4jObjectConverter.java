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
import org.springframework.dsl.lsp.domain.CompletionItem;
import org.springframework.dsl.lsp.domain.CompletionParams;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.Hover;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.InitializedParams;
import org.springframework.dsl.lsp.domain.PublishDiagnosticsParams;
import org.springframework.dsl.lsp.domain.TextDocumentPositionParams;
import org.springframework.util.ClassUtils;

/**
 * A {@link GenericConverter} able to convert between {@code LSP} domain classes
 * defined in {@code LSP4J} and {@code Spring DSL}.
 *
 * @author Janne Valkealahti
 *
 */
public class GenericLsp4jObjectConverter implements GenericConverter {

	// needs to be stored in pairs!
	private final static Class<?>[] typePairs = new Class[] {
			InitializeParams.class, org.eclipse.lsp4j.InitializeParams.class,
			InitializedParams.class, org.eclipse.lsp4j.InitializedParams.class,
			InitializeResult.class, org.eclipse.lsp4j.InitializeResult.class,
			DidChangeTextDocumentParams.class, org.eclipse.lsp4j.DidChangeTextDocumentParams.class,
			DidCloseTextDocumentParams.class, org.eclipse.lsp4j.DidCloseTextDocumentParams.class,
			DidOpenTextDocumentParams.class, org.eclipse.lsp4j.DidOpenTextDocumentParams.class,
			DidSaveTextDocumentParams.class, org.eclipse.lsp4j.DidSaveTextDocumentParams.class,
			PublishDiagnosticsParams.class, org.eclipse.lsp4j.PublishDiagnosticsParams.class,
			TextDocumentPositionParams.class, org.eclipse.lsp4j.TextDocumentPositionParams.class,
			Hover.class, org.eclipse.lsp4j.Hover.class,
			CompletionItem.class, org.eclipse.lsp4j.CompletionItem.class
			};

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		Set<ConvertiblePair> convertiblePairs = new HashSet<ConvertiblePair>();

		// iterate as pairs
		for (int i = 0; i < typePairs.length; i += 2) {
			convertiblePairs.add(new ConvertiblePair(typePairs[i], typePairs[i + 1]));
			convertiblePairs.add(new ConvertiblePair(typePairs[i + 1], typePairs[i]));
		}

		return convertiblePairs;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.InitializeParams.class, sourceType.getType())) {
			return ConverterUtils.toInitializeParams((org.eclipse.lsp4j.InitializeParams) source);
		}
		if (ClassUtils.isAssignable(InitializeParams.class, sourceType.getType())) {
			return ConverterUtils.toInitializeParams((InitializeParams) source);
		}

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.InitializedParams.class, sourceType.getType())) {
			return ConverterUtils.toInitializedParams((org.eclipse.lsp4j.InitializedParams) source);
		}
		if (ClassUtils.isAssignable(InitializedParams.class, sourceType.getType())) {
			return ConverterUtils.toInitializedParams((InitializedParams) source);
		}

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.CompletionParams.class, sourceType.getType())) {
			return ConverterUtils.toCompletionParams((org.eclipse.lsp4j.CompletionParams) source);
		}
		if (ClassUtils.isAssignable(CompletionParams.class, sourceType.getType())) {
			return ConverterUtils.toCompletionParams((CompletionParams) source);
		}


		if (ClassUtils.isAssignable(InitializeResult.class, sourceType.getType())) {
			return ConverterUtils.toInitializeResult((InitializeResult) source);
		} else if (ClassUtils.isAssignable(InitializeParams.class, sourceType.getType())) {
			return ConverterUtils.toInitializeParams((InitializeParams) source);
		} else if (ClassUtils.isAssignable(DidChangeTextDocumentParams.class, sourceType.getType())) {
			return ConverterUtils.toDidChangeTextDocumentParams((DidChangeTextDocumentParams) source);
		} else if (ClassUtils.isAssignable(DidSaveTextDocumentParams.class, sourceType.getType())) {
			return ConverterUtils.toDidSaveTextDocumentParams((DidSaveTextDocumentParams) source);

		} else if (ClassUtils.isAssignable(org.eclipse.lsp4j.InitializeParams.class, sourceType.getType())) {
			return ConverterUtils.toInitializeParams((org.eclipse.lsp4j.InitializeParams) source);
		}


		if (ClassUtils.isAssignable(org.eclipse.lsp4j.DidChangeTextDocumentParams.class, sourceType.getType())) {
			return ConverterUtils.toDidChangeTextDocumentParams((org.eclipse.lsp4j.DidChangeTextDocumentParams) source);
		}

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.DidCloseTextDocumentParams.class, sourceType.getType())) {
			return ConverterUtils.toDidCloseTextDocumentParams((org.eclipse.lsp4j.DidCloseTextDocumentParams) source);
		}

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.DidOpenTextDocumentParams.class, sourceType.getType())) {
			return ConverterUtils.toDidOpenTextDocumentParams((org.eclipse.lsp4j.DidOpenTextDocumentParams) source);
		}

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.DidSaveTextDocumentParams.class, sourceType.getType())) {
			return ConverterUtils.toDidSaveTextDocumentParams((org.eclipse.lsp4j.DidSaveTextDocumentParams) source);
		}

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.PublishDiagnosticsParams.class, sourceType.getType())) {
			return ConverterUtils.toPublishDiagnosticsParams((org.eclipse.lsp4j.PublishDiagnosticsParams) source);
		}

		if (ClassUtils.isAssignable(PublishDiagnosticsParams.class, sourceType.getType())) {
			return ConverterUtils.toPublishDiagnosticsParams((PublishDiagnosticsParams) source);
		}

		if (ClassUtils.isAssignable(TextDocumentPositionParams.class, sourceType.getType())) {
			return ConverterUtils.toTextDocumentPositionParams((TextDocumentPositionParams) source);
		}

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.TextDocumentPositionParams.class, sourceType.getType())) {
			return ConverterUtils.toTextDocumentPositionParams((org.eclipse.lsp4j.TextDocumentPositionParams) source);
		}

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.Hover.class, sourceType.getType())) {
			return ConverterUtils.toHover((org.eclipse.lsp4j.Hover) source);
		}
		if (ClassUtils.isAssignable(Hover.class, sourceType.getType())) {
			return ConverterUtils.toHover((Hover) source);
		}

		if (ClassUtils.isAssignable(org.eclipse.lsp4j.CompletionItem.class, sourceType.getType())) {
			return ConverterUtils.toCompletionItem((org.eclipse.lsp4j.CompletionItem) source);
		}
		if (ClassUtils.isAssignable(CompletionItem.class, sourceType.getType())) {
			return ConverterUtils.toCompletionItem((CompletionItem) source);
		}

		throw new IllegalArgumentException();
	}
}
