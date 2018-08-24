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
package org.springframework.dsl.lsp.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.dsl.jsonrpc.JsonRpcInputMessage;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.dsl.jsonrpc.JsonRpcResponse;
import org.springframework.dsl.jsonrpc.session.JsonRpcSession.JsonRpcSessionCustomizer;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcRequest;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcResponse;
import org.springframework.dsl.lsp.server.jsonrpc.LspJsonRpcDecoder;
import org.springframework.dsl.lsp.server.jsonrpc.LspJsonRpcEncoder;
import org.springframework.dsl.lsp.server.jsonrpc.ReactorJsonRpcInputMessage;
import org.springframework.dsl.lsp.server.jsonrpc.ReactorJsonRpcOutputMessage;
import org.springframework.dsl.lsp.server.jsonrpc.RpcHandler;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.netty.buffer.ByteBuf;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;
import reactor.ipc.netty.NettyPipeline;

/**
 *
 * @author Janne Valkealahti
 *
 */
public class ClientReactorJsonRpcHandlerAdapter implements BiFunction<NettyInbound, NettyOutbound, Mono<Void>> {

	private static final Logger log = LoggerFactory.getLogger(ClientReactorJsonRpcHandlerAdapter.class);
	private final RpcHandler rpcHandler;
	public EmitterProcessor<JsonRpcResponse> responses = EmitterProcessor.create();
	public EmitterProcessor<ByteBuf> requests = EmitterProcessor.create();

	public ClientReactorJsonRpcHandlerAdapter(RpcHandler rpcHandler) {
		this.rpcHandler = rpcHandler;
	}

	@Override
	public Mono<Void> apply(NettyInbound in, NettyOutbound out) {
		Map<String, Disposable> disposables = new HashMap<>();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(DefaultJsonRpcRequest.class, new DefaultJsonRpcRequestDeserializer());
		module.addDeserializer(DefaultJsonRpcResponse.class, new DefaultJsonRpcResponseJsonDeserializer());
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(out.alloc());

		Function<String, DefaultJsonRpcRequest> jsonDecoder = s -> {
			try {
				return mapper.readValue(s, DefaultJsonRpcRequest.class);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		};

		Function<String, DefaultJsonRpcResponse> jsonDecoder2 = s -> {
			try {
				return mapper.readValue(s, DefaultJsonRpcResponse.class);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		};

		in.context().addHandlerLast(new LspJsonRpcDecoder());
		out.context().addHandlerLast(new LspJsonRpcEncoder());

		// we can only have one subscriber to NettyInbound, so need to dispatch
		// relevant responses to client.
		NettyBoundedLspClient lspClient = new NettyBoundedLspClient(out);
		JsonRpcSessionCustomizer customizer = session -> session.getAttributes().put("lspClient", lspClient);


		requests.doOnNext(bb -> {
			out.sendObject(bb).then().subscribe();
		}).subscribe();

		Flux<String> shared = in.receiveObject()
			.ofType(String.class)
			.share();

		shared
			.map(jsonDecoder2)
			.filter(response -> response.getResult() != null || response.getError() != null)
			.subscribe(bb -> {
				lspClient.getResponses().onNext(bb);
				responses.onNext(bb);
			});

//		in.receiveObject()
//		.ofType(String.class)
//		.map(jsonDecoder)
		shared
			.map(jsonDecoder)
			.filter(request -> request.getMethod() != null)
			.subscribe(bb -> {
				log.info("Receive request {}", bb);

				JsonRpcInputMessage i = new JsonRpcInputMessage() {

					@Override
					public Mono<String> getJsonrpc() {
						return Mono.justOrEmpty(bb.getJsonrpc());
					}

					@Override
					public Mono<Integer> getId() {
						return Mono.justOrEmpty(bb.getId());
					}

					@Override
					public Mono<String> getMethod() {
						return Mono.justOrEmpty(bb.getMethod());
					}

					@Override
					public Mono<String> getParams() {
						return Mono.justOrEmpty(bb.getParams().toString());
					}

					@Override
					public Mono<String> getSessionId() {
						return Mono.fromSupplier(() -> in.context().channel().id().asLongText());
					}
				};

				JsonRpcInputMessage adaptedRequest = new ReactorJsonRpcInputMessage(in, bufferFactory);
				JsonRpcOutputMessage adaptedResponse = new ReactorJsonRpcOutputMessage(out, bufferFactory);

				String disposableId = null;
				Disposable disposable = null;
				if (bb.getId() != null) {
					disposableId = in.context().channel().id().asLongText() + bb.getId();
				}

				if (ObjectUtils.nullSafeEquals("$/cancelRequest", bb.getMethod())) {
					if (disposableId != null) {
						disposable = disposables.remove(disposableId);
						if (disposable != null) {
							disposable.dispose();
							String error = "{\"jsonrpc\":\"2.0\", \"id\":" + bb.getId() + ", \"error\":{\"code\":-32800, \"message\": \"cancel\"}}";
							DataBuffer buffer = bufferFactory.wrap(error.getBytes(Charset.defaultCharset()));
							Flux<DataBuffer> body = Flux.just(buffer);
							adaptedResponse.writeWith(body).subscribe();
							adaptedResponse.setComplete().subscribe();
						}
					} else {
						log.error("Cancel request but no existing disposable");
					}
					return;
				}

				disposable = rpcHandler.handle(i, adaptedResponse, customizer)
						.doOnError(ex -> {
							log.error("Handling completed with error", ex);

							String error = "{\"jsonrpc\":\"2.0\", \"id\":" + bb.getId() + ", \"error\":{\"code\":-32603, \"message\": \"internal server error\"}}";

							DataBuffer buffer = bufferFactory.wrap(error.getBytes(Charset.defaultCharset()));
							Flux<DataBuffer> body = Flux.just(buffer);
							adaptedResponse.writeWith(body).subscribe();
							adaptedResponse.setComplete().subscribe();
						})
						.doOnSuccess(aVoid -> log.debug("Handling completed with success"))
						.subscribe();

				if (disposableId != null) {
					disposables.put(disposableId, disposable);
				}
			});


		return out.options(NettyPipeline.SendOptions::flushOnEach)
				.neverComplete();
	}

	private static class DefaultJsonRpcRequestDeserializer extends JsonDeserializer<DefaultJsonRpcRequest> {

		@Override
		public DefaultJsonRpcRequest deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			ObjectCodec c = p.getCodec();
		    JsonNode node = c.readTree(p);
		    String jsonrpc = node.get("jsonrpc").asText();
		    JsonNode jsonNodeParams = node.get("params");
		    String params = null;
		    if (jsonNodeParams != null) {
			    if (jsonNodeParams.isValueNode()) {
			    	params = jsonNodeParams.asText();
			    } else {
			    	params = jsonNodeParams.toString();
			    }
		    }
		    JsonNode jsonNodeId = node.get("id");
		    Integer id = jsonNodeId != null ? jsonNodeId.asInt() : null;
		    String method = node.get("method").asText();
			return new DefaultJsonRpcRequest(jsonrpc, id, method, params);
		}

	}

	private static class DefaultJsonRpcResponseJsonDeserializer extends JsonDeserializer<DefaultJsonRpcResponse> {

		@Override
		public DefaultJsonRpcResponse deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			DefaultJsonRpcResponse response = new DefaultJsonRpcResponse();
			JsonNode node = p.getCodec().readTree(p);
			JsonNode jsonrpcNode = node.get("jsonrpc");
			response.setJsonrpc(jsonrpcNode.asText());
			JsonNode idNode = node.get("id");
			response.setId(idNode.asInt());
			JsonNode resultsNode = node.get("result");
			if (resultsNode != null) {
				response.setResult(resultsNode.asText());
			}
			JsonNode errorNode = node.get("error");
			if (errorNode != null) {
				response.setError(errorNode.asText());
			}
			return response;
		}
	}
}
