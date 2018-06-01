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
package org.springframework.dsl.lsp.server.jsonrpc;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import reactor.ipc.netty.tcp.BlockingNettyContext;
import reactor.ipc.netty.tcp.TcpServer;

public class NettyTcpServer {

	private static final Logger log = LoggerFactory.getLogger(NettyTcpServer.class);
	private final TcpServer tcpServer;
	private final ReactorJsonRpcHandlerAdapter handlerAdapter;
	private final Duration lifecycleTimeout;
	private BlockingNettyContext nettyContext;

	/**
	 * Instantiates a new netty tcp server.
	 *
	 * @param tcpServer the tcp server
	 * @param handlerAdapter the handler adapter
	 * @param lifecycleTimeout the lifecycle timeout
	 */
	public NettyTcpServer(TcpServer tcpServer, ReactorJsonRpcHandlerAdapter handlerAdapter, Duration lifecycleTimeout) {
		Assert.notNull(tcpServer, "TcpServer must not be null");
		Assert.notNull(handlerAdapter, "ReactorJsonRpcHandlerAdapter must not be null");
		this.tcpServer = tcpServer;
		this.handlerAdapter = handlerAdapter;
		this.lifecycleTimeout = lifecycleTimeout;
	}

	public void start() {
		if (this.nettyContext == null) {
			nettyContext = startServer();
		}
		log.info("Netty started on port(s) {}", getPort());
	}

	public void stop() {
		if (nettyContext != null) {
			nettyContext.shutdown();
			nettyContext = null;
		}
	}

	public int getPort() {
		if (nettyContext != null) {
			return nettyContext.getPort();
		}
		return 0;
	}

	private BlockingNettyContext startServer() {
		if (this.lifecycleTimeout != null) {
			return tcpServer.start(handlerAdapter, lifecycleTimeout);
		}
		return tcpServer.start(handlerAdapter);
	}
}
