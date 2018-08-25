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
package org.springframework.dsl.lsp.client;

import org.springframework.dsl.jsonrpc.JsonRpcResponse;

import reactor.core.publisher.Mono;

/**
 * A non-blocking, reactive client for performing {@code LSP} related
 * {@code JSONRCP} requests with Reactive Streams back pressure.
 *
 * @author Janne Valkealahti
 *
 */
public interface LspClient {

	/**
	 * Prepare a {@code JSONRCP} request.
	 *
	 * @return a spec for specifying the request
	 */
	RequestSpec request();

	/**
	 * Gets a builder for instances of a {@link LspClient}s.
	 *
	 * @return the builder for building lsp clients
	 */
	static Builder builder() {
		return new DefaultLspClientBuilder();
	}

	/**
	 * Interface for building {@link LspClient}.
	 */
	interface Builder {

		/**
		 * Sets a host address where client should connect to.
		 *
		 * @param host the host
		 * @return the builder
		 */
		Builder host(String host);

		/**
		 * Sets a port where client should connect to.
		 *
		 * @param port the port
		 * @return the builder
		 */
		Builder port(Integer port);

		/**
		 * Builds the {@link LspClient}.
		 *
		 * @return the lsp client
		 */
		LspClient build();
	}

	/**
	 * Defines a contract for a request.
	 */
	interface RequestSpec {

		/**
		 * Sets the message id.
		 *
		 * @param id the id
		 * @return the request spec
		 */
		RequestSpec id(Integer id);

		/**
		 * Sets the message method.
		 *
		 * @param method the method
		 * @return the request spec
		 */
		RequestSpec method(String method);

		/**
		 * Sets the message params.
		 *
		 * @param params the params
		 * @return the request spec
		 */
		RequestSpec params(Object params);

		/**
		 * Gets the exchange.
		 *
		 * @return the mono of a response
		 */
		Mono<JsonRpcResponse> exchange();
	}
}
