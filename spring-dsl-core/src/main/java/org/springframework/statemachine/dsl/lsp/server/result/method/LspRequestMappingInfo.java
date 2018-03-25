/*
 * Copyright 2017 the original author or authors.
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
package org.springframework.statemachine.dsl.lsp.server.result.method;

import org.springframework.lang.Nullable;
import org.springframework.statemachine.dsl.lsp.LspMethod;
import org.springframework.statemachine.dsl.lsp.server.LspRequestCondition;
import org.springframework.statemachine.dsl.lsp.server.ServerLspExchange;
import org.springframework.statemachine.dsl.lsp.server.result.condition.LspRequestMethodsRequestCondition;

/**
 * Encapsulates the following request mapping conditions:
 *
 * @author Janne Valkealahti
 *
 */
public class LspRequestMappingInfo implements LspRequestCondition<LspRequestMappingInfo>{

	private final LspRequestMethodsRequestCondition methodsCondition;

	/**
	 * Instantiates a new coap request mapping info.
	 */
	public LspRequestMappingInfo(
			LspRequestMethodsRequestCondition methods) {
		this.methodsCondition = (methods != null ? methods : new LspRequestMethodsRequestCondition());
	}

	@Override
	public LspRequestMappingInfo combine(LspRequestMappingInfo other) {
		LspRequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
		return new LspRequestMappingInfo(methods);
	}

	@Override
	public LspRequestMappingInfo getMatchingCondition(ServerLspExchange exchange) {
		LspRequestMethodsRequestCondition methods = methodsCondition.getMatchingCondition(exchange);
		if (methods == null) {
			return null;
		}
		return new LspRequestMappingInfo(methods);
	}

	@Override
	public int compareTo(LspRequestMappingInfo other, ServerLspExchange exchange) {
		return 0;
	}

	public LspRequestMethodsRequestCondition getMethodsCondition() {
		return methodsCondition;
	}

	/**
	 * Create a new {@code CoapRequestMappingInfo.Builder} with the given paths.
	 * @param paths the paths to use
	 *
	 * @return the builder
	 */
	public static Builder paths(String... paths) {
		return new DefaultBuilder(paths);
	}

	/**
	 * Defines a builder for creating a {@link LspRequestMappingInfo}.
	 */
	public interface Builder {

		/**
		 * Set the coap request method paths.
		 *
		 * @param paths the paths
		 * @return the builder for chaining
		 */
		Builder paths(String... paths);

		/**
		 * Set the coap request method conditions.
		 *
		 * @param methods the methods
		 * @return the builder for chaining
		 */
		Builder methods(LspMethod... methods);

		/**
		 * Set the header conditions.
		 * <p>By default this is not set.
		 *
		 * @param headers the headers
		 * @return the builder for chaining
		 */
		Builder headers(String... headers);

		/**
		 * Set the consumes conditions.
		 *
		 * @param consumes the consumes
		 * @return the builder for chaining
		 */
		Builder consumes(String... consumes);

		/**
		 * Set the produces conditions.
		 *
		 * @param produces the produces
		 * @return the builder for chaining
		 */
		Builder produces(String... produces);

		/**
		 * Builds the {@link LspRequestMappingInfo}.
		 *
		 * @return the coap request mapping info
		 */
		LspRequestMappingInfo build();
	}

	private static class DefaultBuilder implements Builder {

		private String[] paths;

		@Nullable
		private LspMethod[] methods;

		@Nullable
		private String[] headers;

		@Nullable
		private String[] consumes;

		@Nullable
		private String[] produces;

		public DefaultBuilder(String... paths) {
			this.paths = paths;
		}

		@Override
		public Builder paths(String... paths) {
			this.paths = paths;
			return this;
		}

		@Override
		public Builder methods(LspMethod... methods) {
			this.methods = methods;
			return this;
		}

		@Override
		public DefaultBuilder headers(String... headers) {
			this.headers = headers;
			return this;
		}

		@Override
		public DefaultBuilder consumes(String... consumes) {
			this.consumes = consumes;
			return this;
		}

		@Override
		public DefaultBuilder produces(String... produces) {
			this.produces = produces;
			return this;
		}

		@Override
		public LspRequestMappingInfo build() {
			return new LspRequestMappingInfo(new LspRequestMethodsRequestCondition(methods));
		}

//		private static List<PathPattern> parse(String[] paths, PathPatternParser parser) {
//			return Arrays.stream(paths).map(path -> {
//				if (StringUtils.hasText(path) && !path.startsWith("/")) {
//					path = "/" + path;
//				}
//				return parser.parse(path);
//			}).collect(Collectors.toList());
//		}
	}
}
