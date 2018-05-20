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

import java.io.ByteArrayOutputStream;

import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.json.StreamMessageConsumer;
import org.eclipse.lsp4j.jsonrpc.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link StreamMessageConsumer} which does a slight hack to intercept
 * message consuming and then storing payload internally to get exposed to a
 * user of this class. All this is needed for custom communication channels like
 * websockets.
 *
 * @author Janne Valkealahti
 *
 */
class StreamMessageConsumerAdapter extends StreamMessageConsumer {

	private static final Logger log = LoggerFactory.getLogger(StreamMessageConsumerAdapter.class);
	private String currentPayload = null;

	/**
	 * Instantiates a new stream message consumer adapter.
	 *
	 * @param jsonHandler the json handler
	 */
	public StreamMessageConsumerAdapter(MessageJsonHandler jsonHandler) {
		super(new ByteArrayOutputStream(), jsonHandler);
	}

	@Override
	public void consume(Message message) {
		log.debug("Consume message {}", message);
		super.consume(message);
		currentPayload = getOutput().toString();
		setOutput(new ByteArrayOutputStream());
	}

	/**
	 * Gets the current payload.
	 *
	 * @return the current payload
	 */
	public String getCurrentPayload() {
		return currentPayload;
	}
}
