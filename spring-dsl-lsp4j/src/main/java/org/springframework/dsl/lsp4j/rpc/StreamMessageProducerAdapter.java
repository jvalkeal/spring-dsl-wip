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
package org.springframework.dsl.lsp4j.rpc;

import java.io.ByteArrayInputStream;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.jsonrpc.RemoteEndpoint;
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.json.StreamMessageProducer;
import org.eclipse.lsp4j.jsonrpc.services.ServiceEndpoints;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;

public class StreamMessageProducerAdapter extends StreamMessageProducer implements Function<byte[], Mono<String>> {

	private static final Logger log = LoggerFactory.getLogger(StreamMessageProducerAdapter.class);

	private final LanguageServer languageServer;
	private MessageConsumer messageConsumer;
	private MessageJsonHandler jsonHandler;
	private StreamMessageConsumerAdapter streamMessageConsumerAdapter;

	public StreamMessageProducerAdapter(MessageJsonHandler jsonHandler, LanguageServer languageServer) {
		super(new ByteArrayInputStream(new byte[0]), jsonHandler);
		this.jsonHandler = jsonHandler;
		this.languageServer = languageServer;
		xxx();
	}

	public void handlePayload(byte[] payload) {
		log.debug("Handling payload {}", new String(payload));
		setInput(new ByteArrayInputStream(payload));
		listen(messageConsumer);
		log.debug("Listen for messageConsumer done");
	}

	@Override
	public Mono<String> apply(byte[] t) {
		handlePayload(t);
		return Mono.just(streamMessageConsumerAdapter.getCurrentPayload());
	}

	private void xxx() {
		streamMessageConsumerAdapter = new StreamMessageConsumerAdapter(jsonHandler);
		RemoteEndpoint serverEndpoint = new RemoteEndpoint(streamMessageConsumerAdapter, ServiceEndpoints.toEndpoint(languageServer));
		this.messageConsumer = serverEndpoint;
		jsonHandler.setMethodProvider(serverEndpoint);

		log.debug("Wired serverEndpoint {} to jsonHandler {} with languageServer {}", serverEndpoint, jsonHandler,
				languageServer);
	}
}
