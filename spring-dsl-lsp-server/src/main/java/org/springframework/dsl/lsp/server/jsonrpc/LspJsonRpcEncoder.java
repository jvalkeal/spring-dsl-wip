package org.springframework.dsl.lsp.server.jsonrpc;

import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class LspJsonRpcEncoder extends MessageToMessageEncoder<ByteBuf> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		int readableBytes = msg.readableBytes();
		ByteBuf buf = ctx.alloc().buffer();
		buf.writeCharSequence("Content-Length: ", Charset.defaultCharset());
		buf.writeCharSequence(Integer.toString(readableBytes), Charset.defaultCharset());
		buf.writeCharSequence("\r\n", Charset.defaultCharset());
		buf.writeCharSequence("\r\n", Charset.defaultCharset());
		buf.writeBytes(msg);
		out.add(buf);
	}

}
