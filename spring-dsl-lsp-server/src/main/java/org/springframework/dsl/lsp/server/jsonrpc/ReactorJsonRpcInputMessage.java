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

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.dsl.jsonrpc.support.AbstractJsonRpcInputMessage;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyInbound;

public class ReactorJsonRpcInputMessage extends AbstractJsonRpcInputMessage {

	private final NettyInbound in;
	private final NettyDataBufferFactory bufferFactory;

	public ReactorJsonRpcInputMessage(NettyInbound in, NettyDataBufferFactory bufferFactory) {
		this.in = in;
		this.bufferFactory = bufferFactory;
	}

	@Override
	public Mono<String> getJsonrpc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Integer> getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<String> getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<String> getParams() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public Flux<DataBuffer> getBody() {
//		return in.receive().retain().map(bufferFactory::wrap);
//	}


//	@Override
//	public String getMethod() {
//		return null;
//	}
//
//	@Override
//	public String getParams() {
//		return null;
//	}
}
