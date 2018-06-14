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
package org.springframework.dsl.jsonrpc.codec;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Map;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.type.TypeFactory;


import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.dsl.jsonrpc.JsonRpcInputMessage;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.lang.Nullable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Jackson2JsonRpcMessageWriter implements JsonRpcMessageWriter<Object> {

	private static final Logger log = LoggerFactory.getLogger(Jackson2JsonRpcMessageWriter.class);
	private final ObjectMapper objectMapper;
	public static final String JSON_VIEW_HINT = Jackson2JsonRpcMessageWriter.class.getName() + ".jsonView";

	public Jackson2JsonRpcMessageWriter() {
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public boolean canWrite(ResolvableType elementType) {
		return true;
	}

	@Override
	public Mono<Void> write(Publisher<? extends Object> inputStream, ResolvableType elementType,
			JsonRpcInputMessage request, JsonRpcOutputMessage message, Map<String, Object> hints) {
		Flux<DataBuffer> encode = encode(inputStream, message.bufferFactory(), elementType, hints, request);
		return message.writeWith(encode);
	}

	public ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}

	public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory,
			ResolvableType elementType, @Nullable Map<String, Object> hints, JsonRpcInputMessage request) {

		JsonEncoding encoding = JsonEncoding.UTF8;

		return Flux.from(inputStream).map(value -> {
			DataBuffer buffer = encodeValue(value, bufferFactory, elementType, hints, encoding, request);
			return buffer;
		});
	}

	public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory,
			ResolvableType elementType, @Nullable Map<String, Object> hints, JsonEncoding encoding, JsonRpcInputMessage request) {

		JavaType javaType = getJavaType(elementType.getType(), null);
		Class<?> jsonView = (hints != null ? (Class<?>) hints.get(JSON_VIEW_HINT) : null);
		ObjectWriter writer = (jsonView != null ?
				getObjectMapper().writerWithView(jsonView) : getObjectMapper().writer());

		if (javaType.isContainerType()) {
			writer = writer.forType(javaType);
		}


		DataBuffer buffer = bufferFactory.allocateBuffer();
		OutputStream outputStream = buffer.asOutputStream();

		try {
			JsonGenerator generator = getObjectMapper().getFactory().createGenerator(outputStream, encoding);
			generator.writeStartObject();
			generator.writeStringField("jsonrpc", request.getJsonrpc());
			generator.writeNumberField("id", request.getId());
			generator.writeObjectField("result", value);
			generator.writeEndObject();
			generator.flush();
		}
		catch (InvalidDefinitionException ex) {
			log.error("{}", ex);
			throw new CodecException("Type definition error: " + ex.getType(), ex);
		}
		catch (JsonProcessingException ex) {
			log.error("{}", ex);
			throw new EncodingException("JSON encoding error: " + ex.getOriginalMessage(), ex);
		}
		catch (IOException ex) {
			log.error("{}", ex);
			throw new IllegalStateException("Unexpected I/O error while writing to data buffer", ex);
		}

		return buffer;
	}


	public DataBuffer encodeValuex(Object value, DataBufferFactory bufferFactory,
			ResolvableType elementType, @Nullable Map<String, Object> hints, JsonEncoding encoding, JsonRpcInputMessage request) {

		JavaType javaType = getJavaType(elementType.getType(), null);
		Class<?> jsonView = (hints != null ? (Class<?>) hints.get(JSON_VIEW_HINT) : null);
		ObjectWriter writer = (jsonView != null ?
				getObjectMapper().writerWithView(jsonView) : getObjectMapper().writer());

//		if (javaType.isContainerType()) {
//			writer = writer.forType(javaType);
//		}

		DataBuffer buffer = bufferFactory.allocateBuffer();
		OutputStream outputStream = buffer.asOutputStream();

		try {
			JsonGenerator generator = getObjectMapper().getFactory().createGenerator(outputStream, encoding);
			generator.writeStartObject();
			generator.writeStringField("jsonrpc", request.getJsonrpc());
			generator.writeNumberField("id", request.getId());
			generator.writeObjectField("result", value);
			generator.writeEndObject();
			generator.flush();
		}
		catch (InvalidDefinitionException ex) {
			log.error("{}", ex);
			throw new CodecException("Type definition error: " + ex.getType(), ex);
		}
		catch (JsonProcessingException ex) {
			log.error("{}", ex);
			throw new EncodingException("JSON encoding error: " + ex.getOriginalMessage(), ex);
		}
		catch (IOException ex) {
			log.error("{}", ex);
			throw new IllegalStateException("Unexpected I/O error while writing to data buffer", ex);
		}

		return buffer;
	}

	protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
		TypeFactory typeFactory = this.objectMapper.getTypeFactory();
		return typeFactory.constructType(GenericTypeResolver.resolveType(type, contextClass));
	}

}
