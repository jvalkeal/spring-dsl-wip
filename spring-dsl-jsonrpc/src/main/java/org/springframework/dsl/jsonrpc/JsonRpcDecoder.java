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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;

public class JsonRpcDecoder extends ByteToMessageDecoder {

    public static final byte CR = 13;
    public static final byte LF = 10;

    private static final String EMPTY_VALUE = "";
    private final HeaderParser headerParser;
    private final LineParser lineParser;
//    private Message<JsonRpcRequest> message;
    private Map<String, String> headers = new HashMap<>(1);
//    private JsonRpcRequest payload;
	private State currentState = State.READ_HEADER;
    private CharSequence name;
    private CharSequence value;
    private long contentLength = Long.MIN_VALUE;
    private volatile boolean resetRequested;

	public enum State {
		READ_HEADER,
		READ_FIXED_LENGTH_CONTENT;
	}

	public JsonRpcDecoder() {
        AppendableCharSequence seq = new AppendableCharSequence(128);
        lineParser = new LineParser(seq, 4096);
        headerParser = new HeaderParser(seq, 256);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (resetRequested) {
            resetNow();
        }

		switch (currentState) {
		case READ_HEADER:

            State nextState = readHeaders(in);
            if (nextState == null) {
                return;
            }
            currentState = nextState;

			break;
		case READ_FIXED_LENGTH_CONTENT:
			int readLimit = in.readableBytes();
            if (readLimit == 0) {
                return;
            }
            ByteBuf content = in.readRetainedSlice(readLimit);
            String payload = content.retain().duplicate().toString(Charset.defaultCharset());
            out.add(MessageBuilder.withPayload(payload).copyHeaders(headers).build());
			break;

		default:
			break;
		}

	}

    private void resetNow() {

        name = null;
        value = null;
        headers.clear();
        contentLength = Long.MIN_VALUE;
        lineParser.reset();
        headerParser.reset();
        resetRequested = false;
        currentState = State.READ_HEADER;
    }

	private State readHeaders(ByteBuf in) {
//		message = createMessage();
//		MessageHeaders headers = message.getHeaders();

        AppendableCharSequence line = headerParser.parse(in);
        if (line == null) {
            return null;
        }
        if (line.length() > 0) {
            do {
                splitHeader(line);

                if (name != null) {
                	headers.put(name.toString(), value.toString());
                }


//                char firstChar = line.charAt(0);
//                if (name != null && (firstChar == ' ' || firstChar == '\t')) {
//                    String trimmedLine = line.toString().trim();
//                    String valueStr = String.valueOf(value);
//                    value = valueStr + ' ' + trimmedLine;
//                } else {
//                    if (name != null) {
//                        headers.put(name.toString(), value.toString());
//                    }
//                    splitHeader(line);
//                }

                line = headerParser.parse(in);
                if (line == null) {
                    return null;
                }
            } while (line.length() > 0);
        }
        name = null;
        value = null;


		State nextState;

		if (contentLength() >= 0) {
            nextState = State.READ_FIXED_LENGTH_CONTENT;
        } else {
            nextState = State.READ_HEADER;
        }

		return nextState;
	}

//	private Message<JsonRpcRequest> createMessage() {
//		JsonRpcRequest payload = new JsonRpcRequest();
//		payload.setJsonrpc("2.0");
//		return MessageBuilder.withPayload(payload).build();
//	}

    private long contentLength() {
        if (contentLength == Long.MIN_VALUE) {
        	String value = headers.get("Content-Length");
            if (value != null) {
                return Long.parseLong(value);
            }
        }
        return contentLength;
    }


    private void splitHeader(AppendableCharSequence sb) {
        final int length = sb.length();
        int nameStart;
        int nameEnd;
        int colonEnd;
        int valueStart;
        int valueEnd;

        nameStart = findNonWhitespace(sb, 0);
        for (nameEnd = nameStart; nameEnd < length; nameEnd ++) {
            char ch = sb.charAt(nameEnd);
            if (ch == ':' || Character.isWhitespace(ch)) {
                break;
            }
        }

        for (colonEnd = nameEnd; colonEnd < length; colonEnd ++) {
            if (sb.charAt(colonEnd) == ':') {
                colonEnd ++;
                break;
            }
        }

        name = sb.subStringUnsafe(nameStart, nameEnd);
        valueStart = findNonWhitespace(sb, colonEnd);
        if (valueStart == length) {
            value = EMPTY_VALUE;
        } else {
            valueEnd = findEndOfString(sb);
            value = sb.subStringUnsafe(valueStart, valueEnd);
        }
    }

    private static int findNonWhitespace(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result;
            }
        }
        return sb.length();
    }

    private static int findWhitespace(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result;
            }
        }
        return sb.length();
    }

    private static int findEndOfString(AppendableCharSequence sb) {
        for (int result = sb.length() - 1; result > 0; --result) {
            if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result + 1;
            }
        }
        return 0;
    }

    private static class HeaderParser implements ByteProcessor {
        private final AppendableCharSequence seq;
        private final int maxLength;
        private int size;

        HeaderParser(AppendableCharSequence seq, int maxLength) {
            this.seq = seq;
            this.maxLength = maxLength;
        }

        public AppendableCharSequence parse(ByteBuf buffer) {
            final int oldSize = size;
            seq.reset();
            int i = buffer.forEachByte(this);
            if (i == -1) {
                size = oldSize;
                return null;
            }
            buffer.readerIndex(i + 1);
            return seq;
        }

        public void reset() {
            size = 0;
        }

        @Override
        public boolean process(byte value) throws Exception {
            char nextByte = (char) (value & 0xFF);
            if (nextByte == CR) {
                return true;
            }
            if (nextByte == LF) {
                return false;
            }

            if (++ size > maxLength) {
                // TODO: Respond with Bad Request and discard the traffic
                //    or close the connection.
                //       No need to notify the upstream handlers - just log.
                //       If decoding a response, just throw an exception.
                throw newException(maxLength);
            }

            seq.append(nextByte);
            return true;
        }

        protected TooLongFrameException newException(int maxLength) {
            return new TooLongFrameException("JSONRCP header is larger than " + maxLength + " bytes.");
        }
    }

    private static final class LineParser extends HeaderParser {

        LineParser(AppendableCharSequence seq, int maxLength) {
            super(seq, maxLength);
        }

        @Override
        public AppendableCharSequence parse(ByteBuf buffer) {
            reset();
            return super.parse(buffer);
        }

        @Override
        protected TooLongFrameException newException(int maxLength) {
            return new TooLongFrameException("An JSONRCP line is larger than " + maxLength + " bytes.");
        }
    }
}
