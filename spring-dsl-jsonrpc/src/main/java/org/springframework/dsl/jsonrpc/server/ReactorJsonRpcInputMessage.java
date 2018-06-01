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
package org.springframework.dsl.jsonrpc.server;

import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.dsl.jsonrpc.support.AbstractJsonRpcInputMessage;

import reactor.ipc.netty.NettyInbound;

public class ReactorJsonRpcInputMessage extends AbstractJsonRpcInputMessage {

	private final NettyInbound in;
	private final NettyDataBufferFactory bufferFactory;

	public ReactorJsonRpcInputMessage(NettyInbound in, NettyDataBufferFactory bufferFactory) {
		this.in = in;
		this.bufferFactory = bufferFactory;
	}


}
