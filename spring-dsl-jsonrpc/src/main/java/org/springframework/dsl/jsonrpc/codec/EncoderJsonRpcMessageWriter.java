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

import java.util.Map;

import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EncoderJsonRpcMessageWriter<T> implements JsonRpcMessageWriter<T> {

	private final Encoder<T> encoder;

	public EncoderJsonRpcMessageWriter(Encoder<T> encoder) {
		this.encoder = encoder;
	}

	@Override
	public boolean canWrite(ResolvableType elementType) {
		return this.encoder.canEncode(elementType, null);
	}

	@Override
	public Mono<Void> write(Publisher<? extends T> inputStream, ResolvableType elementType,
			JsonRpcOutputMessage message, Map<String, Object> hints) {

		Flux<DataBuffer> body = this.encoder.encode(
				inputStream, message.bufferFactory(), elementType, null, hints);

		return message.writeWith(body);
	}

}
