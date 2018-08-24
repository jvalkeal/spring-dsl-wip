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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.dsl.DslSystemConstants;
import org.springframework.dsl.lsp.server.config.DslConfigurationProperties;
import org.springframework.dsl.lsp.server.config.EnableLanguageServer;
import org.springframework.dsl.lsp.server.config.LspDomainJacksonConfiguration;
import org.springframework.dsl.lsp.server.config.LspServerSocketConfiguration;
import org.springframework.dsl.lsp.server.config.LspServerStdioConfiguration;
import org.springframework.dsl.lsp.server.controller.RootLanguageServerController;
import org.springframework.dsl.lsp.server.controller.TextDocumentLanguageServerController;
import org.springframework.dsl.lsp.server.jsonrpc.LspClientArgumentResolver;
import org.springframework.dsl.lsp.server.jsonrpc.LspDomainArgumentResolver;
import org.springframework.dsl.lsp.server.websocket.LspWebSocketConfig;
import org.springframework.dsl.reconcile.DefaultReconciler;
import org.springframework.dsl.reconcile.Linter;
import org.springframework.dsl.service.Reconciler;
import org.springframework.web.reactive.socket.WebSocketHandler;

/**
 * {@link EnableAutoConfiguration Auto-configuration} integrating into {@code LSP} features.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode")
@EnableConfigurationProperties(DslConfigurationProperties.class)
@EnableLanguageServer
@Import({ RootLanguageServerController.class, TextDocumentLanguageServerController.class,
		LspDomainJacksonConfiguration.class })
public class LspAutoConfiguration {

	@Bean
	public Reconciler reconciler(Optional<List<Linter>> linters) {
		return new DefaultReconciler(linters.orElseGet(ArrayList::new));
	}

	@Bean(name = DslSystemConstants.LSP_CONVERSION_SERVICE_BEAN_NAME)
	public ConversionServiceFactoryBean lspConversionService() {
		ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
		Set<Object> converters = new HashSet<>();
		factoryBean.setConverters(converters);
		return factoryBean;
	}

	@Bean
	public LspDomainArgumentResolver lspDomainArgumentResolver() {
		return new LspDomainArgumentResolver();
	}

	@Bean
	public LspClientArgumentResolver lspClientArgumentResolver() {
		return new LspClientArgumentResolver();
	}

	@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode", havingValue = "WEBSOCKET")
	@ConditionalOnClass(value = WebSocketHandler.class)
	@Import({ LspWebSocketConfig.class })
	public static class LspServerWebsocketConfig {
	}

	@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode", havingValue = "PROCESS")
	@Import({ LspServerStdioConfiguration.class })
	public static class LspServerProcessConfig {

		@Bean
		public ReactiveAdapterRegistry jsonRpcAdapterRegistry() {
			return new ReactiveAdapterRegistry();
		}
	}

	@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode", havingValue = "SOCKET")
	@Import({ LspServerSocketConfiguration.class })
	public static class LspServerSocketConfig {

		@Bean
		public ReactiveAdapterRegistry jsonRpcAdapterRegistry() {
			return new ReactiveAdapterRegistry();
		}
	}
}
