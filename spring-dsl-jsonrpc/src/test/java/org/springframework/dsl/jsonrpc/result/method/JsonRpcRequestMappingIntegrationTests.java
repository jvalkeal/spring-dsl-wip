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
package org.springframework.dsl.jsonrpc.result.method;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcController;
import org.springframework.dsl.jsonrpc.annotation.JsonRpcRequestMapping;
import org.springframework.dsl.jsonrpc.config.EnableJsonRcp;

public class JsonRpcRequestMappingIntegrationTests {

	@Test
	public void test() throws InterruptedException {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(JsonRcpConfig.class, TestJsonRcpController.class);
		context.refresh();
		Thread.sleep(10000);
	}

	@EnableJsonRcp
	static class JsonRcpConfig {
	}

	@JsonRpcController
	private static class TestJsonRcpController {

		@JsonRpcRequestMapping(method = "hi")
		public String hi() {
			return "hi";
		}

		@JsonRpcRequestMapping(method = "bye")
		public String bye() {
			return "bye";
		}
	}
}
