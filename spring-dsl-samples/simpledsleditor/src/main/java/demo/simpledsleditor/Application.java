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
package demo.simpledsleditor;

import java.util.LinkedHashMap;

import org.eclipse.lsp4j.jsonrpc.json.JsonRpcMethod;
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.services.ServiceEndpoints;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.dsl.lsp.server.LspHandler;
import org.springframework.dsl.lsp.server.config.EnableLanguageServer;
import org.springframework.dsl.lsp.server.controller.GenericLanguageServerController;
import org.springframework.dsl.lsp4j.Lsp4jLanguageServerAdapter;
import org.springframework.dsl.lsp4j.rpc.StreamMessageProducerAdapter;
import org.springframework.dsl.websocket.LspTextWebSocketHandler;
import org.springframework.dsl.websocket.LspWebSocketConfig;

import demo.simpledsl.EnableSimpleLanguage;

@EnableLanguageServer
@EnableSimpleLanguage
@Import({ LspWebSocketConfig.class, GenericLanguageServerController.class })
@SpringBootApplication
public class Application {

	@Bean
	public MessageJsonHandler messageJsonHandler() {
		LinkedHashMap<String, JsonRpcMethod> supportedMethods = new LinkedHashMap<String, JsonRpcMethod>();
		supportedMethods.putAll(ServiceEndpoints.getSupportedMethods(LanguageClient.class));
		supportedMethods.putAll(ServiceEndpoints.getSupportedMethods(LanguageServer.class));
		MessageJsonHandler jsonHandler = new MessageJsonHandler(supportedMethods);
		return jsonHandler;
	}

	@Bean
	public LspTextWebSocketHandler lspTextWebSocketHandler(StreamMessageProducerAdapter streamMessageProducerAdapter) {
		return new LspTextWebSocketHandler(streamMessageProducerAdapter);
	}

	@Bean
	public StreamMessageProducerAdapter streamMessageProducerAdapter(MessageJsonHandler jsonHandler, LanguageServer languageServer) {
		return new StreamMessageProducerAdapter(jsonHandler, languageServer);
	}

	@Bean
	public Lsp4jLanguageServerAdapter languageServer(LspHandler lspHandler, @Qualifier("lspConversionService") ConversionService conversionService) {
		return new Lsp4jLanguageServerAdapter(lspHandler, conversionService);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
