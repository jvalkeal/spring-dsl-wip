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
package org.springframework.dsl.lsp.server.jsonrpc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.springframework.beans.BeanUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.dsl.jsonrpc.ServerJsonRpcExchange;
import org.springframework.dsl.jsonrpc.result.method.JsonRpcHandlerMethodArgumentResolver;
import org.springframework.dsl.lsp.domain.CompletionParams;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializedParams;
import org.springframework.dsl.lsp.domain.TextDocumentPositionParams;
//import org.springframework.http.codec.json.Jackson2Tokenizer;
//import org.springframework.http.codec.json.JsonFactory;
//import org.springframework.http.codec.json.TokenBuffer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A {@link JsonRpcHandlerMethodArgumentResolver} implementation resolving
 * {@code LSP} domain objects based on a {@code params} in a
 * {@code message}. Dispatches conversion of an argument type to
 * {@link ConversionService}.
 *
 * @author Janne Valkealahti
 *
 */
public class LspDomainArgumentResolver implements JsonRpcHandlerMethodArgumentResolver {

//	private final ConversionService conversionService;
	private Set<Class<?>> supportedClasses = Arrays.asList(
			InitializeParams.class,
			InitializedParams.class,
			DidChangeTextDocumentParams.class,
			DidCloseTextDocumentParams.class,
			DidOpenTextDocumentParams.class,
			DidSaveTextDocumentParams.class,
			CompletionParams.class,
			TextDocumentPositionParams.class
			).stream().collect(Collectors.toSet());

	/**
	 * Instantiates a new lsp domain argument resolver.
	 *
	 * @param conversionService the conversion service
	 */
	public LspDomainArgumentResolver(/*ConversionService conversionService*/) {
//		Assert.notNull(conversionService, "'conversionService' must be set");
//		this.conversionService = conversionService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		return supportedClasses.contains(type);
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, ServerJsonRpcExchange exchange) {
		Class<?> type = parameter.getParameterType();
		Class<?> contextClass = (parameter != null ? parameter.getContainingClass() : null);

		Flux<DataBuffer> body = exchange.getRequest().getBody();
		Mono<String> bodyAsString = getBodyAsString(body);

		ObjectMapper objectMapper = new ObjectMapper();
//		ObjectWriter writer = objectMapper.writer();
//		ObjectReader reader = objectMapper.reader();
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		JavaType javaType = typeFactory.constructType(GenericTypeResolver.resolveType(type, contextClass));
		ObjectReader forType = objectMapper.readerFor(javaType);
		try {
			return Mono.just(forType.readValue(bodyAsString.block()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Mono.error(e);
		}
//		return Mono.just(BeanUtils.instantiateClass(type));
//		Object body = null;
//		if (exchange.getRequest() != null) {
//		}
//		Class<?> clazz = parameter.getParameterType();
//		return Mono.just(conversionService.convert(body, clazz));
	}

	public Mono<String> getBodyAsString(Flux<DataBuffer> body) {
		Charset charset = Charset.defaultCharset();
		DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
		return Flux.from(body)
				.reduce(bufferFactory.allocateBuffer(), (previous, current) -> {
					previous.write(current);
					DataBufferUtils.release(current);
					return previous;
				})
				.map(buffer -> dumpString(buffer, charset));
	}

	private static String dumpString(DataBuffer buffer, Charset charset) {
		Assert.notNull(charset, "'charset' must not be null");
		byte[] bytes = new byte[buffer.readableByteCount()];
		buffer.read(bytes);
		return new String(bytes, charset);
	}


//	private Flux<TokenBuffer> tokenize(Publisher<DataBuffer> input, boolean tokenizeArrayElements) {
//		Flux<DataBuffer> inputFlux = Flux.from(input);
//		JsonFactory factory = getObjectMapper().getFactory();
//		return Jackson2Tokenizer.tokenize(inputFlux, factory, tokenizeArrayElements);
//	}

}
