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
package org.springframework.dsl.lsp.server.domain;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.lsp.domain.ServerCapabilities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ServerCapabilitiesJsonSerializer extends JsonSerializer<ServerCapabilities> {

	private static final Logger log = LoggerFactory.getLogger(ServerCapabilitiesJsonSerializer.class);
	
	@Override
	public void serialize(ServerCapabilities value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException {
		log.trace("UUU2");
		gen.writeStartObject();

		if (value.getTextDocumentSyncOptions() != null) {
			gen.writeObjectField("textDocumentSync", value.getTextDocumentSyncOptions());
		} else if (value.getTextDocumentSyncKind() != null) {
			gen.writeNumberField("textDocumentSync", value.getTextDocumentSyncKind().ordinal());
		}
		
		if (value.getHoverProvider() != null) {
			gen.writeBooleanField("hoverProvider", value.getHoverProvider());
		}
		
		if (value.getCompletionProvider() != null) {
			gen.writeObjectField("completionProvider", value.getCompletionProvider());
		}
		
		gen.writeEndObject();
	}
}
