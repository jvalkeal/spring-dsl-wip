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
package org.springframework.dsl.jsonrpc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dsl.jsonrpc.support.DispatcherJsonRpcHandler;

@Configuration
public class DelegatingJsonRpcConfiguration {

	@Bean
	public DispatcherJsonRpcHandler dispatcherJsonRpcHandler() {
		return new DispatcherJsonRpcHandler();
	}

//	@Bean
//	public RpcJsonRpcHandlerAdapter rpcJsonRpcHandlerAdapter(DispatcherJsonRpcHandler dispatcherJsonRpcHandler) {
//		return new RpcJsonRpcHandlerAdapter(dispatcherJsonRpcHandler);
//	}
//
//	@Bean
//	public ReactorJsonRpcHandlerAdapter reactorJsonRpcHandlerAdapter(RpcJsonRpcHandlerAdapter rpcJsonRpcHandlerAdapter) {
//		return new ReactorJsonRpcHandlerAdapter(rpcJsonRpcHandlerAdapter);
//	}
//
//	@Bean(initMethod = "start")
//	public NettyTcpServer nettyTcpServer(ReactorJsonRpcHandlerAdapter handlerAdapter) {
//		TcpServer tcpServer = TcpServer.create(9999);
//		NettyTcpServer nettyTcpServer = new NettyTcpServer(tcpServer, handlerAdapter, null);
//		return nettyTcpServer;
//	}

}
