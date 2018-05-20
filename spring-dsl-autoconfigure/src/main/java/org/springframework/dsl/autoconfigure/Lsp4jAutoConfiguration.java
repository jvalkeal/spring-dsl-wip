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
package org.springframework.dsl.autoconfigure;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.eclipse.lsp4j.jsonrpc.json.JsonRpcMethod;
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.services.ServiceEndpoints;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.dsl.DslSystemConstants;
import org.springframework.dsl.lsp.server.LspHandler;
import org.springframework.dsl.lsp.server.config.DslConfigurationProperties;
import org.springframework.dsl.lsp.server.config.DslConfigurationProperties.LspServerSocketMode;
import org.springframework.dsl.lsp.server.config.EnableLanguageServer;
import org.springframework.dsl.lsp.server.support.JvmLspExiter;
import org.springframework.dsl.lsp.server.websocket.LspTextWebSocketHandler;
import org.springframework.dsl.lsp.server.websocket.LspWebSocketConfig;
import org.springframework.dsl.lsp4j.Lsp4jLanguageServerAdapter;
import org.springframework.dsl.lsp4j.config.Lsp4jServerLauncherConfiguration;
import org.springframework.dsl.lsp4j.converter.GenericLsp4jObjectConverter;
import org.springframework.dsl.lsp4j.result.method.annotation.Lsp4jDomainArgumentResolver;
import org.springframework.dsl.lsp4j.rpc.StreamMessageProducerAdapter;
import org.springframework.web.socket.WebSocketHandler;

/**
 * {@link EnableAutoConfiguration Auto-configuration} integrating into {@code LSP4J} features.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnClass(LanguageServer.class)
@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode")
@EnableConfigurationProperties(DslConfigurationProperties.class)
@EnableLanguageServer
public class Lsp4jAutoConfiguration {

	@Bean(name = DslSystemConstants.LSP_CONVERSION_SERVICE_BEAN_NAME)
	public ConversionServiceFactoryBean lspConversionService() {
		ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
		Set<Object> converters = new HashSet<>();
		converters.add(new GenericLsp4jObjectConverter());
		factoryBean.setConverters(converters);
		return factoryBean;
	}

	@Bean
	public Lsp4jDomainArgumentResolver lsp4jDomainArgumentResolver(
			@Qualifier(DslSystemConstants.LSP_CONVERSION_SERVICE_BEAN_NAME) ConversionService conversionService) {
		return new Lsp4jDomainArgumentResolver(conversionService);
	}

	@Bean
	public Lsp4jLanguageServerAdapter languageServer(LspHandler lspHandler,
			@Qualifier("lspConversionService") ConversionService conversionService,
			DslConfigurationProperties properties) {
		Lsp4jLanguageServerAdapter adapter = new Lsp4jLanguageServerAdapter(lspHandler, conversionService);
		if (properties.getLsp().getServer().getMode() != LspServerSocketMode.WEBSOCKET) {
			adapter.setLspExiter(new JvmLspExiter());
		}
		adapter.setForceJvmExitOnShutdown(properties.getLsp().getServer().isForceJvmExitOnShutdown());
		return adapter;
	}

	@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode", havingValue = "WEBSOCKET")
	@ConditionalOnClass(value = WebSocketHandler.class)
	@Import({ LspWebSocketConfig.class })
	public static class DslServerWebsocketConfig {

		@Bean
		public MessageJsonHandler messageJsonHandler() {
			LinkedHashMap<String, JsonRpcMethod> supportedMethods = new LinkedHashMap<String, JsonRpcMethod>();
			supportedMethods.putAll(ServiceEndpoints.getSupportedMethods(LanguageClient.class));
			supportedMethods.putAll(ServiceEndpoints.getSupportedMethods(LanguageServer.class));
			MessageJsonHandler jsonHandler = new MessageJsonHandler(supportedMethods);
			return jsonHandler;
		}

		@Bean
		public StreamMessageProducerAdapter streamMessageProducerAdapter(MessageJsonHandler jsonHandler,
				LanguageServer languageServer) {
			return new StreamMessageProducerAdapter(jsonHandler, languageServer);
		}

		@Bean
		public LspTextWebSocketHandler lspTextWebSocketHandler(
				StreamMessageProducerAdapter streamMessageProducerAdapter) {
			return new LspTextWebSocketHandler(streamMessageProducerAdapter);
		}
	}

	@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode", havingValue = "PROCESS")
	@Import({ Lsp4jServerLauncherConfiguration.class })
	public static class DslServerProcessConfig {
	}

	@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode", havingValue = "SOCKET")
	@Import({ Lsp4jServerLauncherConfiguration.class })
	public static class DslServerSocketConfig {
	}
}
