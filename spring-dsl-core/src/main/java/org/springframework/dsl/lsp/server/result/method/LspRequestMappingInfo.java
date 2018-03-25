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
package org.springframework.dsl.lsp.server.result.method;

import org.springframework.dsl.lsp.LspMethod;
import org.springframework.dsl.lsp.server.LspRequestCondition;
import org.springframework.dsl.lsp.server.ServerLspExchange;
import org.springframework.dsl.lsp.server.result.condition.LspRequestMethodsRequestCondition;
import org.springframework.lang.Nullable;

/**
 * Encapsulates the following request mapping conditions:.
 *
 * @author Janne Valkealahti
 */
public class LspRequestMappingInfo implements LspRequestCondition<LspRequestMappingInfo>{

	/** The methods condition. */
	private final LspRequestMethodsRequestCondition methodsCondition;

	/**
	 * Instantiates a new coap request mapping info.
	 *
	 * @param methods the methods
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

	/**
	 * Gets the methods condition.
	 *
	 * @return the methods condition
	 */
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

	/**
	 * The Class DefaultBuilder.
	 */
	private static class DefaultBuilder implements Builder {

		/** The paths. */
		private String[] paths;

		/** The methods. */
		@Nullable
		private LspMethod[] methods;

		/** The headers. */
		@Nullable
		private String[] headers;

		/** The consumes. */
		@Nullable
		private String[] consumes;

		/** The produces. */
		@Nullable
		private String[] produces;

		/**
		 * Instantiates a new default builder.
		 *
		 * @param paths the paths
		 */
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
