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

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcController;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcNotification;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcRequestMapping;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcResponseBody;
import org.springframework.dsl.jsonrpc.config.EnableJsonRcp;
import org.springframework.dsl.jsonrpc.support.DispatcherJsonRpcHandler;
import org.springframework.dsl.lsp.domain.InitializeParams;

import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.tcp.TcpClient;
import reactor.ipc.netty.tcp.TcpServer;

public class NettyTcpServerIntegrationTests {

	private static final byte[] CONTENT1 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 1}");
	private static final byte[] CONTENT2 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 2, \"method\": \"hi\"}");
	private static final byte[] CONTENT3 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 3, \"method\": \"bye\"}");
	private static final byte[] CONTENT4 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 3, \"method\": \"shouldnotexist\"}");
	private static final byte[] CONTENT5 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 4, \"method\": \"pojo1\"}");
	private static final byte[] CONTENT6 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 4, \"method\": \"methodparams\", \"params\":\"hi\"}");
	private static final byte[] CONTENT7 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 4, \"method\": \"monovoid\", \"params\":null}");
	private static final byte[] CONTENT8 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 4, \"method\": \"void\", \"params\":null}");
	private static final byte[] CONTENT9 = createContent("{\"jsonrpc\": \"2.0\", \"method\": \"notificationsingleresponse\"}");
	private static final byte[] CONTENT10 = createContent("{\"jsonrpc\": \"2.0\", \"method\": \"notificationmultiresponse\"}");

	private static final String initializeParams = "{" +
			"\"processId\":1," +
			"\"rootUri\":\"rootUri\"," +
			"\"initializationOptions\":\"initializationOptions\"," +
			"\"trace\":\"trace\"," +
			"\"capabilities\":{\"experimental\":\"experimental\"}" +
			"}";

	private static final byte[] CONTENT11 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 4, \"method\": \"initializeparams\", \"params\":" + initializeParams + "}");

	private static byte[] createContent(String... lines) {
		StringBuilder buf = new StringBuilder();
		for (String line : lines) {
			buf.append(line);
			buf.append("\r\n");
		}
		String message = "Content-Length: " + buf.length() + "\r\n\r\n" + buf.toString();
		return message.getBytes();
	}

	private AnnotationConfigApplicationContext context;

	@Before
	public void init() {
		context = null;
	}

	@After
	public void clean() {
		if (context != null) {
			context.close();
		}
		context = null;
	}

	@Test
	public void testOk1() throws InterruptedException {
		context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
		NettyTcpServer server = context.getBean(NettyTcpServer.class);

		CountDownLatch dataLatch = new CountDownLatch(2);
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
							.send(Flux.just(Unpooled.copiedBuffer(CONTENT2), Unpooled.copiedBuffer(CONTENT3)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		assertThat(dataLatch.await(1, TimeUnit.SECONDS)).isTrue();

		// {"jsonrpc": "2.0", "result": "bye", "id": 3}
		// {"jsonrpc": "2.0", "result": "hi", "id": 4}
		String response1 = "Content-Length: 38\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":2,\"result\":\"hi\"}";
		String response2 = "Content-Length: 39\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"bye\"}";

		assertThat(responses).containsExactlyInAnyOrder(response1, response2);
	}

	@Test
	public void testOk2() throws InterruptedException {
		context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
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
							.send(Flux.just(Unpooled.copiedBuffer(CONTENT5)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		assertThat(dataLatch.await(1, TimeUnit.SECONDS)).isTrue();

		String response = "Content-Length: 50\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":4,\"result\":{\"message\":\"hi\"}}";

		assertThat(responses).containsExactlyInAnyOrder(response);
	}

	@Test
	public void testOk4() throws InterruptedException {
		context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
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
							.send(Flux.just(Unpooled.copiedBuffer(CONTENT7)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		assertThat(dataLatch.await(1, TimeUnit.SECONDS)).isTrue();

		String response = "Content-Length: 38\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":4,\"result\":null}";

		assertThat(responses).containsExactlyInAnyOrder(response);
	}

	@Test
	public void testOk5() throws InterruptedException {
		context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
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
							.send(Flux.just(Unpooled.copiedBuffer(CONTENT8)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		assertThat(dataLatch.await(1, TimeUnit.SECONDS)).isTrue();

		String response = "Content-Length: 38\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":4,\"result\":null}";

		assertThat(responses).containsExactlyInAnyOrder(response);
	}

	@Test
	public void testOk3() throws InterruptedException {
		context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
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
							.send(Flux.just(Unpooled.copiedBuffer(CONTENT6)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		assertThat(dataLatch.await(1, TimeUnit.SECONDS)).isTrue();

		String response = "Content-Length: 38\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":4,\"result\":\"hi\"}";

		assertThat(responses).containsExactlyInAnyOrder(response);
	}

	@Test
	public void testOk6() throws InterruptedException {
		context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
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
							.send(Flux.just(Unpooled.copiedBuffer(CONTENT11)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		assertThat(dataLatch.await(1, TimeUnit.SECONDS)).isTrue();

		String response = "Content-Length: 196\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":4,\"result\":{\"processId\":1,\"rootPath\":null,\"rootUri\":\"rootUri\",\"initializationOptions\":\"initializationOptions\",\"capabilities\":{\"experimental\":\"experimental\"},\"trace\":\"trace\"}}";

		assertThat(responses).containsExactlyInAnyOrder(response);
	}

	@Test
	public void testSingleNotification() throws InterruptedException {
		context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
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
							.send(Flux.just(Unpooled.copiedBuffer(CONTENT9)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		assertThat(dataLatch.await(1, TimeUnit.SECONDS)).isTrue();

		String response = "Content-Length: 31\r\n\r\n{\"jsonrpc\":\"2.0\",\"result\":\"hi\"}";

		assertThat(responses).containsExactlyInAnyOrder(response);
	}

	@Test
	public void testMultiNotification() throws InterruptedException {
		context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
		NettyTcpServer server = context.getBean(NettyTcpServer.class);

		CountDownLatch dataLatch = new CountDownLatch(2);
		final List<String> responses = new ArrayList<>();

		TcpClient.create(server.getPort())
				.newHandler((in, out) -> {
					in.context().addHandlerLast(new LspJsonRpcDecoder());
					in.receiveObject()
						.ofType(String.class)
						.subscribe(c -> {
							responses.add(c);
							dataLatch.countDown();
						});

					return out
							.send(Flux.just(Unpooled.copiedBuffer(CONTENT10)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		assertThat(dataLatch.await(1, TimeUnit.SECONDS)).isTrue();

		String response1 = "{\"jsonrpc\":\"2.0\",\"result\":\"hi\"}";
		String response2 = "{\"jsonrpc\":\"2.0\",\"result\":\"bye\"}";

		assertThat(responses).containsExactlyInAnyOrder(response1, response2);
	}

	@Test
	public void testMethodNotMatchedError() throws InterruptedException {
		context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
		NettyTcpServer server = context.getBean(NettyTcpServer.class);

		CountDownLatch dataLatch = new CountDownLatch(1);
		final List<String> responses = new ArrayList<>();

		TcpClient.create(server.getPort())
				.newHandler((in, out) -> {
					in
					.receive()
					.log()
					.subscribe(c -> {
						responses.add(c.retain().duplicate().toString(Charset.defaultCharset()));
						dataLatch.countDown();
					});

					return out
							.send(Flux.just(Unpooled.copiedBuffer(CONTENT4)))
							.neverComplete();
				})
				.block(Duration.ofSeconds(30));

		String response = "Content-Length: 86\r\n\r\n{\"jsonrpc\":\"2.0\", \"id\":3, \"error\":{\"code\":-32603, \"message\": \"internal server error\"}}";

		assertThat(dataLatch.await(2, TimeUnit.SECONDS)).isTrue();

		assertThat(responses).contains(response);
	}

	@EnableJsonRcp
	static class JsonRcpConfig {

		@Bean
		public LspDomainArgumentResolver lspDomainArgumentResolver() {
			return new LspDomainArgumentResolver();
		}

		@Bean
		public ReactiveAdapterRegistry jsonRpcAdapterRegistry() {
			return new ReactiveAdapterRegistry();
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

	@JsonRpcController
	private static class TestJsonRcpController {

		@JsonRpcRequestMapping(method = "hi")
		@JsonRpcResponseBody
		public String hi() {
			return "hi";
		}

		@JsonRpcRequestMapping(method = "bye")
		@JsonRpcResponseBody
		public String bye() {
			return "bye";
		}

		@JsonRpcRequestMapping(method = "pojo1")
		@JsonRpcResponseBody
		public Pojo1 pojo1() {
			return new Pojo1();
		}

		@JsonRpcRequestMapping(method = "methodparams")
		@JsonRpcResponseBody
		public String methodparams(String params) {
			return params;
		}

		@JsonRpcRequestMapping(method = "monovoid")
		@JsonRpcResponseBody
		public Mono<Void> monovoid() {
			return Mono.empty();
		}

		@JsonRpcRequestMapping(method = "void")
		@JsonRpcResponseBody
		public void justvoid() {
		}

		@JsonRpcRequestMapping(method = "notificationsingleresponse")
		@JsonRpcNotification
		public String notificationsingleresponse() {
			return "hi";
		}

		@JsonRpcRequestMapping(method = "notificationmultiresponse")
		@JsonRpcNotification
		public Flux<String> notificationmultiresponse() {
			return Flux.just("hi", "bye");
		}

		@JsonRpcRequestMapping(method = "initializeparams")
		@JsonRpcResponseBody
		public InitializeParams initializeparams(InitializeParams params) {
			return params;
		}
	}

	private static class Pojo1 {
		private String message = "hi";

		public String getMessage() {
			return message;
		}
	}
}
