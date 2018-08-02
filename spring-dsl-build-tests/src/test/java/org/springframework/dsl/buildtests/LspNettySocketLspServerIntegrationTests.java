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
package org.springframework.dsl.buildtests;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.dsl.DslSystemConstants;
import org.springframework.dsl.autoconfigure.LspAutoConfiguration;
import org.springframework.dsl.buildtests.LspNettySocketLspServerIntegrationTests.Config1;
import org.springframework.dsl.jsonrpc.config.EnableJsonRcp;
import org.springframework.dsl.jsonrpc.support.DispatcherJsonRpcHandler;
import org.springframework.dsl.lsp.server.controller.RootLanguageServerController;
import org.springframework.dsl.lsp.server.jsonrpc.LspDomainArgumentResolver;
import org.springframework.dsl.lsp.server.jsonrpc.NettyTcpServer;
import org.springframework.dsl.lsp.server.jsonrpc.ReactorJsonRpcHandlerAdapter;
import org.springframework.dsl.lsp.server.jsonrpc.RpcJsonRpcHandlerAdapter;
import org.springframework.dsl.service.DocumentStateTracker;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.tcp.TcpClient;
import reactor.ipc.netty.tcp.TcpServer;

/**
 * Command flow tests using Netty's socker server integration into generic lsp
 * controllers using language hooks defined in {@link AbstractLspIntegrationTests}.
 *
 * @author Janne Valkealahti
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = Config1.class)
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class LspNettySocketLspServerIntegrationTests extends AbstractLspIntegrationTests {


	private static final byte[] INIT = createContent("{\"jsonrpc\": \"2.0\",\"id\": 1, \"method\": \"initialize\"}");

	@Autowired
	private ApplicationContext context;

	private static byte[] createContent(String... lines) {
		StringBuilder buf = new StringBuilder();
		for (String line : lines) {
			buf.append(line);
			buf.append("\r\n");
		}
		String message = "Content-Length: " + buf.length() + "\r\n\r\n" + buf.toString();
		return message.getBytes();
	}

//	@Test
	public void testInits() throws Exception {
//		context.register(Config1.class);
//		context.refresh();
		NettyTcpServer server = context.getBean(NettyTcpServer.class);

		CountDownLatch dataLatch = new CountDownLatch(1);
		final List<String> responses = new ArrayList<>();

		TcpClient.create(server.getPort())
				.newHandler((in, out) -> {
					in
					.receive()
					.subscribe(c -> {
						responses.add(c.retain().duplicate().toString(Charset.defaultCharset()));
						dataLatch.countDown();
					});

					return out
							.send(Flux.just(Unpooled.copiedBuffer(INIT)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		assertThat(dataLatch.await(1, TimeUnit.SECONDS)).isTrue();

		String response = "{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":{\"capabilities\":{\"textDocumentSyncOptions\":null,\"textDocumentSyncKind\":\"Full\",\"hoverProvider\":false,\"completionProvider\":null}}}";

		assertThat(responses).containsExactlyInAnyOrder(response);

	}

	@EnableJsonRcp
	@Import({LspAutoConfiguration.class, RootLanguageServerController.class})
	static class Config1 {

		@MockBean
		DocumentStateTracker documentStateTracker;

		// TODO: remove
		@Bean
		public LspDomainArgumentResolver lspDomainArgumentResolver() {
			return new LspDomainArgumentResolver();
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
			TcpServer tcpServer = TcpServer.create();
			NettyTcpServer nettyTcpServer = new NettyTcpServer(tcpServer, handlerAdapter, null);
			return nettyTcpServer;
		}
	}

//	@Override
//	protected AnnotationConfigApplicationContext buildContext() {
//		return new AnnotationConfigApplicationContext();
//	}
}
