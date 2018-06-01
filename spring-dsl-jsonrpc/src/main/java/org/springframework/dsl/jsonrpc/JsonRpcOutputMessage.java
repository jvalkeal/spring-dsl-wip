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
package org.springframework.dsl.jsonrpc;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;

import reactor.core.publisher.Mono;

public interface JsonRpcOutputMessage extends JsonRpcMessage {

	/**
	 * Use the given {@link Publisher} to write the body of the message to the
	 * underlying JSONRPC layer.
	 *
	 * @param body the body content publisher
	 * @return a {@link Mono} that indicates completion or error
	 */
	Mono<Void> writeWith(Publisher<? extends DataBuffer> body);

	Mono<Void> setComplete();
}
