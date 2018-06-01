package org.springframework.dsl.lsp.server.jsonrpc;

import java.nio.charset.Charset;

import org.junit.Test;
import org.reactivestreams.Processor;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcRequest;
import org.springframework.dsl.jsonrpc.support.DefaultJsonRpcResponse;
import org.springframework.dsl.lsp.server.jsonrpc.JsonRpcServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.WorkQueueProcessor;

public class JsonRpcServerTests {

	@Test
	public void test() throws InterruptedException {
		Processor<DefaultJsonRpcRequest, DefaultJsonRpcRequest> requestProcessor = WorkQueueProcessor.create();
		WorkQueueProcessor<DefaultJsonRpcResponse> responseProcessor = WorkQueueProcessor.create();
		JsonRpcServer server = new JsonRpcServer(requestProcessor, responseProcessor);
		server.start();

		Flux.from(requestProcessor)
			.log("ddd")
			.doOnNext(request -> {
				DefaultJsonRpcResponse response = new DefaultJsonRpcResponse();
//				response.setId("R" + request.getId());
				responseProcessor.onNext(response);
			})
			.subscribe();
		Thread.sleep(10000);

	}

}
