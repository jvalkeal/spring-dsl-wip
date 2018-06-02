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
package org.springframework.dsl.jsonrpc.support;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyOutbound;

public class DefaultJsonRpcOutputMessage extends AbstractJsonRpcOutputMessage {

	private static final Logger log = LoggerFactory.getLogger(DefaultJsonRpcOutputMessage.class);
	private final NettyOutbound response;

	public DefaultJsonRpcOutputMessage(NettyOutbound response, DataBufferFactory dataBufferFactory) {
		super(dataBufferFactory);
		this.response = response;
	}

	@Override
	public String getJsonrpc() {
		return null;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> publisher) {
		Publisher<ByteBuf> body = toByteBufs(publisher);
		return this.response.send(body).then();
	}

	private static Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
		return Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
	}
}