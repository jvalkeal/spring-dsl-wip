/*
 * Copyright 2017 the original author or authors.
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
package org.springframework.statemachine.dsl.lsp.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.dsl.lsp.server.result.method.annotation.LspRequestMappingHandlerAdapter;
import org.springframework.statemachine.dsl.lsp.server.result.method.annotation.LspRequestMappingHandlerMapping;
import org.springframework.statemachine.dsl.lsp.server.result.method.annotation.LspResponseBodyResultHandler;
import org.springframework.statemachine.dsl.lsp.server.support.DispatcherHandler;

@Configuration
public class DelegatingLspConfiguration {

	@Bean
	public DispatcherHandler lspDispatcherHandler() {
		return new DispatcherHandler();
	}

	@Bean
	public LspResponseBodyResultHandler lspResponseBodyResultHandler() {
		return new LspResponseBodyResultHandler();
	}

	@Bean
	public LspRequestMappingHandlerMapping lspRequestMappingHandlerMapping() {
		return new LspRequestMappingHandlerMapping();
	}

	@Bean
	public LspRequestMappingHandlerAdapter lspRequestMappingHandlerAdapter() {
		return new LspRequestMappingHandlerAdapter();
	}

}
