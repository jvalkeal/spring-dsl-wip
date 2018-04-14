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
package org.springframework.dsl.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link ConfigurationProperties} for settings under {@code spring.dsl}.
 *
 * @author Janne Valkealahti
 *
 */
@ConfigurationProperties(prefix = "spring.dsl")
public class DslConfigurationProperties {

	private LspProperties lsp = new LspProperties();

	public LspProperties getLsp() {
		return lsp;
	}

	public void setLsp(LspProperties lsp) {
		this.lsp = lsp;
	}

	public static class LspProperties {

		private LspServerProperties server = new LspServerProperties();

		public LspServerProperties getServer() {
			return server;
		}

		public void setServer(LspServerProperties server) {
			this.server = server;
		}
	}

	public static class LspServerProperties {

		private LspServerSocketMode mode = LspServerSocketMode.PROCESS;

		public LspServerSocketMode getMode() {
			return mode;
		}

		public void setMode(LspServerSocketMode mode) {
			this.mode = mode;
		}
	}

	public enum LspServerSocketMode {
		PROCESS,
		SOCKET,
		WEBSOCKET;
	}
}
