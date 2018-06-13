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
package org.springframework.dsl.lsp.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dsl.lsp.server.jsonrpc.NettyTcpServer;
import org.springframework.dsl.lsp.server.jsonrpc.ReactorJsonRpcHandlerAdapter;

import reactor.ipc.netty.tcp.TcpServer;

/**
 *
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@Import(GenericLspConfiguration.class)
public class LspServerSocketConfiguration {

	@Bean(initMethod = "start", destroyMethod = "stop")
	public NettyTcpServer nettyTcpServer(ReactorJsonRpcHandlerAdapter handlerAdapter) {
		TcpServer tcpServer = TcpServer.create();
		NettyTcpServer nettyTcpServer = new NettyTcpServer(tcpServer, handlerAdapter, null);
		return nettyTcpServer;
	}
}
