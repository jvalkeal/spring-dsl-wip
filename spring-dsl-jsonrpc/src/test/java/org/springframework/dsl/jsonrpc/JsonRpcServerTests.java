package org.springframework.dsl.jsonrpc;

import java.nio.charset.Charset;

import org.junit.Test;
import org.reactivestreams.Processor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.WorkQueueProcessor;

public class JsonRpcServerTests {

	@Test
	public void test() throws InterruptedException {
		Processor<JsonRpcRequest, JsonRpcRequest> requestProcessor = WorkQueueProcessor.create();
		WorkQueueProcessor<JsonRpcResponse> responseProcessor = WorkQueueProcessor.create();
		JsonRpcServer server = new JsonRpcServer(requestProcessor, responseProcessor);
		server.start();

		Flux.from(requestProcessor)
			.log("ddd")
			.doOnNext(request -> {
				JsonRpcResponse response = new JsonRpcResponse();
//				response.setId("R" + request.getId());
				responseProcessor.onNext(response);
			})
			.subscribe();
		Thread.sleep(10000);

	}

}
