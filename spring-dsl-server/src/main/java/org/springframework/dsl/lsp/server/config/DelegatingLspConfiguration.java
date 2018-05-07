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
package org.springframework.dsl.lsp.server.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dsl.lsp.server.HandlerAdapter;
import org.springframework.dsl.lsp.server.HandlerMapping;
import org.springframework.dsl.lsp.server.HandlerResultHandler;
import org.springframework.dsl.lsp.server.result.method.LspHandlerMethodArgumentResolver;
import org.springframework.dsl.lsp.server.result.method.annotation.LspNoResponseBodyResultHandler;
import org.springframework.dsl.lsp.server.result.method.annotation.LspRequestMappingHandlerAdapter;
import org.springframework.dsl.lsp.server.result.method.annotation.LspRequestMappingHandlerMapping;
import org.springframework.dsl.lsp.server.result.method.annotation.LspResponseBodyResultHandler;
import org.springframework.dsl.lsp.server.result.method.annotation.ServerLspExchangeArgumentResolver;
import org.springframework.dsl.lsp.server.support.DispatcherHandler;

/**
 * Generic configuration around {@link DispatcherHandler} and its related
 * {@link HandlerResultHandler}, {@link HandlerMapping} and
 * {@link HandlerAdapter}.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
public class DelegatingLspConfiguration {

	/**
	 * Lsp dispatcher handler.
	 *
	 * @return the dispatcher handler
	 */
	@Bean
	public DispatcherHandler lspDispatcherHandler() {
		return new DispatcherHandler();
	}

	/**
	 * Lsp response body result handler.
	 *
	 * @return the lsp response body result handler
	 */
	@Bean
	public LspResponseBodyResultHandler lspResponseBodyResultHandler() {
		return new LspResponseBodyResultHandler();
	}

	@Bean
	public LspNoResponseBodyResultHandler lspNoResponseBodyResultHandler() {
		return new LspNoResponseBodyResultHandler();
	}

	/**
	 * Lsp request mapping handler mapping.
	 *
	 * @return the lsp request mapping handler mapping
	 */
	@Bean
	public LspRequestMappingHandlerMapping lspRequestMappingHandlerMapping() {
		return new LspRequestMappingHandlerMapping();
	}

	@Bean
	public ServerLspExchangeArgumentResolver serverLspExchangeArgumentResolver() {
		return new ServerLspExchangeArgumentResolver();
	}

	/**
	 * Lsp request mapping handler adapter.
	 *
	 * @param resolvers the resolvers
	 * @return the lsp request mapping handler adapter
	 */
	@Bean
	public LspRequestMappingHandlerAdapter lspRequestMappingHandlerAdapter(List<LspHandlerMethodArgumentResolver> resolvers) {
		return new LspRequestMappingHandlerAdapter(resolvers);
	}
}
