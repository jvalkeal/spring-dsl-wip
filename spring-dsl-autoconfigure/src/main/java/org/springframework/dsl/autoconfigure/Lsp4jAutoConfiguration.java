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
import java.util.Set;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
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
import org.springframework.dsl.lsp.controller.GenericLanguageServerController;
import org.springframework.dsl.lsp.server.LspHandler;
import org.springframework.dsl.lsp.server.config.DslConfigurationProperties;
import org.springframework.dsl.lsp.server.config.EnableLanguageServer;
import org.springframework.dsl.lsp.service.DocumentStateTracker;
import org.springframework.dsl.lsp.service.GenericDocumentStateTracker;
import org.springframework.dsl.lsp.service.Reconciler;
import org.springframework.dsl.lsp4j.Lsp4jLanguageServerAdapter;
import org.springframework.dsl.lsp4j.config.Lsp4jServerLauncherConfiguration;
import org.springframework.dsl.lsp4j.converter.GenericLsp4jObjectConverter;
import org.springframework.dsl.lsp4j.result.method.annotation.Lsp4jDomainArgumentResolver;
import org.springframework.dsl.reconcile.Linter;
import org.springframework.dsl.reconcile.SimpleReconciler;

/**
 * {@link EnableAutoConfiguration Auto-configuration} integrating into {@code LSP4J} features.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnClass(LanguageServer.class)
@EnableConfigurationProperties(DslConfigurationProperties.class)
@Import({ GenericLanguageServerController.class, Lsp4jServerLauncherConfiguration.class })
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
	public DocumentStateTracker documentStateTracker() {
		return new GenericDocumentStateTracker();
	}

	@Bean
	public Reconciler reconciler(Linter linter) {
		return new SimpleReconciler(linter);
	}

	@Bean
	public Lsp4jLanguageServerAdapter languageServer(LspHandler lspHandler,
			@Qualifier("lspConversionService") ConversionService conversionService) {
		return new Lsp4jLanguageServerAdapter(lspHandler, conversionService);
	}

//	@Bean(initMethod = "startListening")
//	@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode", havingValue = "PROCESS")
//	public Launcher<LanguageClient> lspServerLauncher(LanguageServer languageServer) {
//		Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(languageServer, System.in, System.out);
//		if (languageServer instanceof LanguageClientAware) {
//			LanguageClient client = launcher.getRemoteProxy();
//			((LanguageClientAware) languageServer).connect(client);
//		}
//		return launcher;
//	}

}
