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
package org.springframework.dsl.lsp.server.result.method.annotation;

import java.lang.reflect.AnnotatedElement;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.dsl.lsp.annotation.LspController;
import org.springframework.dsl.lsp.annotation.LspRequestMapping;
import org.springframework.dsl.lsp.server.HandlerMapping;
import org.springframework.dsl.lsp.server.result.method.LspRequestMappingInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

// TODO: Auto-generated Javadoc
/**
 * An implementation of {@link HandlerMapping} that creates
 * {@link LspRequestMappingInfo} instances from class-level and method-level
 * {@link LspRequestMapping @CoapRequestMapping} annotations.
 *
 * @author Janne Valkealahti
 *
 */
public class LspRequestMappingHandlerMapping extends AbstractHandlerMethodMapping implements EmbeddedValueResolverAware {

	/** The embedded value resolver. */
	@Nullable
	private StringValueResolver embeddedValueResolver;

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.result.method.annotation.AbstractHandlerMethodMapping#isHandler(java.lang.Class)
	 */
	@Override
	protected boolean isHandler(Class<?> beanType) {
		return (AnnotatedElementUtils.hasAnnotation(beanType, LspController.class) ||
				AnnotatedElementUtils.hasAnnotation(beanType, LspRequestMapping.class));
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.result.method.annotation.AbstractHandlerMethodMapping#createRequestMappingInfo(java.lang.reflect.AnnotatedElement)
	 */
	@Override
	protected LspRequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
		LspRequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, LspRequestMapping.class);
		return requestMapping != null ? createRequestMappingInfo(requestMapping) : null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.EmbeddedValueResolverAware#setEmbeddedValueResolver(org.springframework.util.StringValueResolver)
	 */
	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.embeddedValueResolver = resolver;
	}

	/**
	 * Resolve placeholder values in the given array of patterns.
	 *
	 * @param patterns the patterns
	 * @return a new array with updated patterns
	 */
	protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
		if (this.embeddedValueResolver == null) {
			return patterns;
		}
		else {
			String[] resolvedPatterns = new String[patterns.length];
			for (int i = 0; i < patterns.length; i++) {
				resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
			}
			return resolvedPatterns;
		}
	}

	/**
	 * Creates the request mapping info.
	 *
	 * @param requestMapping the request mapping
	 * @return the lsp request mapping info
	 */
	private LspRequestMappingInfo createRequestMappingInfo(LspRequestMapping requestMapping) {
		LspRequestMappingInfo.Builder builder = LspRequestMappingInfo
				.paths(resolveEmbeddedValuesInPatterns(requestMapping.path()))
				.methods(requestMapping.method())
				.headers(requestMapping.headers())
				.consumes(requestMapping.consumes())
				.produces(requestMapping.produces());
		return builder.build();
	}
}
