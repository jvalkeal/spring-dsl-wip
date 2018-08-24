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

import org.springframework.dsl.lsp.client.LspClient.Builder;

/**
 * Default implementation of a {@link Builder} currently building instances of a
 * {@link NettyTcpClientLspClient}.
 *
 * @author Janne Valkealahti
 *
 */
public class DefaultLspClientBuilder implements Builder {

	private String host;
	private Integer port;

	@Override
	public Builder host(String host) {
		this.host = host;
		return this;
	}

	@Override
	public Builder port(Integer port) {
		this.port = port;
		return this;
	}

	@Override
	public LspClient build() {
		NettyTcpClientLspClient nettyTcpClientLspClient = new NettyTcpClientLspClient(host, port);
//		nettyTcpClientLspClient.init();
		return nettyTcpClientLspClient;
	}

}
