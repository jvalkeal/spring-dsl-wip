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
package demo.simpledslsocketserver;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.dsl.lsp.controller.GenericLanguageServerController;
import org.springframework.dsl.lsp.server.LspHandler;
import org.springframework.dsl.lsp.server.config.EnableLanguageServer;
import org.springframework.dsl.lsp4j.Lsp4jLanguageServerAdapter;
import org.springframework.dsl.lsp4j.converter.GenericLsp4jObjectConverter;
import org.springframework.dsl.lsp4j.result.method.annotation.Lsp4jDomainArgumentResolver;

import demo.simpledsl.EnableSimpleLanguage;

/**
 * {@code LSP} server implementing support for a {@code simple} sample language.
 *
 * @author Janne Valkealahti
 *
 */
@EnableLanguageServer
@EnableSimpleLanguage
@Import({ GenericLanguageServerController.class })
@SpringBootApplication
public class Application {

//	@Bean
//	public ConversionServiceFactoryBean lspConversionService() {
//		ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
//		Set<Object> converters = new HashSet<>();
//		converters.add(new GenericLsp4jObjectConverter());
//		factoryBean.setConverters(converters);
//		return factoryBean;
//	}
//
//	@Bean
//	public Lsp4jDomainArgumentResolver lsp4jDomainArgumentResolver() {
//		return new Lsp4jDomainArgumentResolver();
//	}

	@Bean
	public Lsp4jLanguageServerAdapter languageServer(LspHandler lspHandler,
			@Qualifier("lspConversionService") ConversionService conversionService) {
		return new Lsp4jLanguageServerAdapter(lspHandler, conversionService);
	}

	@Bean(initMethod = "startListening")
	public Launcher<LanguageClient> lspServerLauncher(LanguageServer languageServer) {
		return LSPLauncher.createServerLauncher(languageServer, System.in, System.out);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
