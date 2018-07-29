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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dsl.lsp.domain.DiagnosticSeverity;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.server.domain.DiagnosticSeverityDeserializer;
import org.springframework.dsl.lsp.server.domain.DiagnosticSeveritySerializer;
import org.springframework.dsl.lsp.server.domain.ServerCapabilitiesJsonDeserializer;
import org.springframework.dsl.lsp.server.domain.ServerCapabilitiesJsonSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class LspDomainJacksonConfiguration {

	@Bean(name = "lspJacksonObjectMapper")
	public ObjectMapper lspObjectMapper() {
		SimpleModule module = new SimpleModule();
		module.addSerializer(ServerCapabilities.class, new ServerCapabilitiesJsonSerializer());
		module.addDeserializer(ServerCapabilities.class, new ServerCapabilitiesJsonDeserializer());
		module.addSerializer(DiagnosticSeverity.class, new DiagnosticSeveritySerializer());
		module.addDeserializer(DiagnosticSeverity.class, new DiagnosticSeverityDeserializer());
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper;
	}

}
