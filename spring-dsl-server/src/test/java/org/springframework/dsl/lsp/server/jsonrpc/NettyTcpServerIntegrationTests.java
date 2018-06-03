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
import java.util.function.Function;

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

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.json.JsonObjectDecoder;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.tcp.TcpClient;
import reactor.ipc.netty.tcp.TcpServer;

public class NettyTcpServerIntegrationTests {

	private static final byte[] CONTENT1 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 1}");
	private static final byte[] CONTENT2 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 2, \"method\": \"hi\"}");
	private static final byte[] CONTENT3 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 3, \"method\": \"bye\"}");

	private static byte[] createContent(String... lines) {
		StringBuilder buf = new StringBuilder();
		for (String line : lines) {
			buf.append(line);
			buf.append("\r\n");
		}
		String message = "Content-Length: " + buf.length() + "\r\n\r\n" + buf.toString();
		return message.getBytes();
	}

	@Test
	public void test() throws InterruptedException {

//		Function<List<Pojo>, ByteBuf> jsonEncoder = pojo -> {
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			try {
//				mapper.writeValue(out, pojo);
//			} catch(Exception e) {
//				throw new RuntimeException(e);
//			}
//			return Unpooled.copiedBuffer(out.toByteArray());
//		};


		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
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

		assertThat(responses).containsExactlyInAnyOrder("hi", "bye");
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
	}
}
