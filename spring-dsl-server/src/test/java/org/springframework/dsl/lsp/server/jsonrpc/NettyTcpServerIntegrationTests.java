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

import java.util.List;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.DataBufferEncoder;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcController;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcRequestMapping;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcResponseBody;
import org.springframework.dsl.jsonrpc.codec.EncoderJsonRpcMessageWriter;
import org.springframework.dsl.jsonrpc.codec.JsonRpcMessageWriter;
import org.springframework.dsl.jsonrpc.config.EnableJsonRcp;
import org.springframework.dsl.jsonrpc.result.method.JsonRpcHandlerMethodArgumentResolver;
import org.springframework.dsl.jsonrpc.result.method.annotation.JsonRpcRequestMappingHandlerAdapter;
import org.springframework.dsl.jsonrpc.result.method.annotation.JsonRpcRequestMappingHandlerMapping;
import org.springframework.dsl.jsonrpc.result.method.annotation.JsonRpcResponseBodyResultHandler;
import org.springframework.dsl.jsonrpc.result.method.annotation.ServerJsonRpcExchangeArgumentResolver;
import org.springframework.dsl.jsonrpc.support.DispatcherJsonRpcHandler;
import org.springframework.util.MimeTypeUtils;

import reactor.ipc.netty.tcp.TcpServer;

public class NettyTcpServerIntegrationTests {

	@Test
	public void test() throws InterruptedException {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
		Thread.sleep(30000);
	}

	@EnableJsonRcp
	static class JsonRcpConfig {

		@Bean
		public ReactiveAdapterRegistry jsonRpcAdapterRegistry() {
			return new ReactiveAdapterRegistry();
		}

		@Bean
		public EncoderJsonRpcMessageWriter<?> encoderJsonRpcMessageWriter() {
			return new EncoderJsonRpcMessageWriter<>(CharSequenceEncoder.allMimeTypes());
		}

		@Bean
		public JsonRpcResponseBodyResultHandler jsonRpcResponseBodyResultHandler(
				List<JsonRpcMessageWriter<?>> messageWriters, ReactiveAdapterRegistry adapterRegistry) {
			return new JsonRpcResponseBodyResultHandler(messageWriters, adapterRegistry);
		}

		@Bean
		public JsonRpcRequestMappingHandlerMapping jsonRpcRequestMappingHandlerMapping() {
			return new JsonRpcRequestMappingHandlerMapping();
		}

		@Bean
		public ServerJsonRpcExchangeArgumentResolver serverJsonRpcExchangeArgumentResolver() {
			return new ServerJsonRpcExchangeArgumentResolver();
		}

		@Bean
		public JsonRpcRequestMappingHandlerAdapter jsonRpcRequestMappingHandlerAdapter(
				List<JsonRpcHandlerMethodArgumentResolver> resolvers) {
			return new JsonRpcRequestMappingHandlerAdapter(resolvers);
		}

		@Bean
		public RpcJsonRpcHandlerAdapter rpcJsonRpcHandlerAdapter(DispatcherJsonRpcHandler dispatcherJsonRpcHandler) {
			return new RpcJsonRpcHandlerAdapter(dispatcherJsonRpcHandler);
		}

		@Bean
		public ReactorJsonRpcHandlerAdapter reactorJsonRpcHandlerAdapter(RpcJsonRpcHandlerAdapter rpcJsonRpcHandlerAdapter) {
			return new ReactorJsonRpcHandlerAdapter(rpcJsonRpcHandlerAdapter);
		}

		@Bean(initMethod = "start")
		public NettyTcpServer nettyTcpServer(ReactorJsonRpcHandlerAdapter handlerAdapter) {
			TcpServer tcpServer = TcpServer.create(9999);
			NettyTcpServer nettyTcpServer = new NettyTcpServer(tcpServer, handlerAdapter, null);
			return nettyTcpServer;
		}
	}

	@JsonRpcController
	private static class TestJsonRcpController {

		@JsonRpcRequestMapping(method = "hi")
		@JsonRpcResponseBody
		public String hi() {
			return Long.toString(System.currentTimeMillis());
//			return "hi";
		}

//		@JsonRpcRequestMapping(method = "bye")
//		public String bye() {
//			return "bye";
//		}
	}
}
