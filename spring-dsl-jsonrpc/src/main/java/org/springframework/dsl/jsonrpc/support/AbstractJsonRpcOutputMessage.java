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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.dsl.jsonrpc.JsonRpcOutputMessage;
import org.springframework.lang.Nullable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractJsonRpcOutputMessage implements JsonRpcOutputMessage {

	private static final Logger log = LoggerFactory.getLogger(AbstractJsonRpcOutputMessage.class);
	private final DataBufferFactory dataBufferFactory;

	/**
	 * COMMITTING -> COMMITTED is the period after doCommit is called but before
	 * the response status and headers have been applied to the underlying
	 * response during which time pre-commit actions can still make changes to
	 * the response status and headers.
	 */
	private enum State {NEW, COMMITTING, COMMITTED}

	private final AtomicReference<State> state = new AtomicReference<>(State.NEW);
	private final List<Supplier<? extends Mono<Void>>> commitActions = new ArrayList<>(4);

	public AbstractJsonRpcOutputMessage(DataBufferFactory dataBufferFactory) {
		this.dataBufferFactory = dataBufferFactory;
	}

	@Override
	public final DataBufferFactory bufferFactory() {
		return this.dataBufferFactory;
	}

//	@Override
	public void beforeCommit(Supplier<? extends Mono<Void>> action) {
		this.commitActions.add(action);
	}

//	@Override
	public boolean isCommitted() {
		return this.state.get() != State.NEW;
	}

	@Override
	public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
		return new ChannelSendOperator<>(body,
				writePublisher -> doCommit(() -> writeWithInternal(writePublisher)));
	}

	@Override
	public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
		return new ChannelSendOperator<>(body,
				writePublisher -> doCommit(() -> writeAndFlushWithInternal(writePublisher)));
	}

//	@Override
	@Override
	public Mono<Void> setComplete() {
		return !isCommitted() ? doCommit(null) : Mono.empty();
	}

	protected abstract Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body);

	protected abstract Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body);

	/**
	 * A variant of {@link #doCommit(Supplier)} for a response without no body.
	 * @return a completion publisher
	 */
	protected Mono<Void> doCommit() {
		return doCommit(null);
	}

	/**
	 * Apply {@link #beforeCommit(Supplier) beforeCommit} actions, apply the
	 * response status and headers/cookies, and write the response body.
	 * @param writeAction the action to write the response body (may be {@code null})
	 * @return a completion publisher
	 */
	protected Mono<Void> doCommit(@Nullable Supplier<? extends Mono<Void>> writeAction) {
		if (!this.state.compareAndSet(State.NEW, State.COMMITTING)) {
			if (log.isDebugEnabled()) {
				log.debug("Skipping doCommit (response already committed).");
			}
			return Mono.empty();
		}

		this.commitActions.add(() ->
				Mono.fromRunnable(() -> {
//					applyStatusCode();
//					applyHeaders();
//					applyCookies();
					this.state.set(State.COMMITTED);
				}));

		if (writeAction != null) {
			this.commitActions.add(writeAction);
		}

		List<? extends Mono<Void>> actions = this.commitActions.stream()
				.map(Supplier::get).collect(Collectors.toList());

		return Flux.concat(actions).then();
	}

}
