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

import org.junit.Test;
import org.springframework.dsl.lsp.server.jsonrpc.LspJsonRpcDecoder;
import org.springframework.messaging.Message;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

import static org.assertj.core.api.Assertions.assertThat;


public class LspJsonRpcDecoderTests {

	private static final byte[] CONTENT1 = createContent("{\"jsonrpc\": \"2.0\",\"id\": 1}");

	private static byte[] createContent(String... lines) {
		StringBuilder buf = new StringBuilder();
		for (String line : lines) {
			buf.append(line);
			buf.append("\r\n");
		}
		String message = "Content-Length: " + buf.length() + "\r\n\r\n" + buf.toString();
		return message.getBytes();
	}

	@Test
	public void test() {
		testDecodeWholeRequestAtOnce(CONTENT1);
	}

	private static void testDecodeWholeRequestAtOnce(byte[] content) {
        EmbeddedChannel channel = new EmbeddedChannel(new LspJsonRpcDecoder());
        assertThat(channel.writeInbound(Unpooled.wrappedBuffer(content))).isTrue();
        Message<String> req = channel.readInbound();
        assertThat(req).isNotNull();
        assertThat(req.getPayload()).isNotNull();
        assertThat(req.getPayload()).contains("2.0");

        assertThat(channel.finish()).isFalse();
        Object readInbound = channel.readInbound();
        assertThat(readInbound).isNull();
//        checkHeaders(req.headers());
//        LastHttpContent c = channel.readInbound();
//        assertEquals(CONTENT_LENGTH, c.content().readableBytes());
//        assertEquals(
//                Unpooled.wrappedBuffer(content, content.length - CONTENT_LENGTH, CONTENT_LENGTH),
//                c.content().readSlice(CONTENT_LENGTH));
//        c.release();
//
//        assertFalse(channel.finish());
//        assertNull(channel.readInbound());
	}
}