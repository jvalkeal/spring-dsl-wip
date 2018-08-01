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
package org.springframework.dsl.jsonrpc.session;

import org.springframework.dsl.jsonrpc.ServerJsonRpcExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Default implementation of a {@link JsonRpcSessionManager}.
 *
 * @author Janne Valkealahti
 *
 */
public class DefaultJsonRpcSessionManager implements JsonRpcSessionManager {

	private JsonRpcSessionIdResolver sessionIdResolver = new RequestSessionIdJsonRpcSessionIdResolver();
	private JsonRpcSessionStore sessionStore = new InMemoryJsonRpcSessionStore();

	@Override
	public Mono<JsonRpcSession> getSession(ServerJsonRpcExchange exchange) {
		return Mono.defer(() -> retrieveSession(exchange)
				.switchIfEmpty(this.sessionStore.createSession(exchange.getRequest().getSessionId().block()))
				.doOnNext(session -> exchange.getResponse().beforeCommit(() -> save(exchange, session))));
	}

	public JsonRpcSessionIdResolver getSessionIdResolver() {
		return this.sessionIdResolver;
	}

	private Mono<JsonRpcSession> retrieveSession(ServerJsonRpcExchange exchange) {
		return Flux.fromIterable(getSessionIdResolver().resolveSessionIds(exchange))
				.concatMap(this.sessionStore::retrieveSession)
				.next();
	}

	private Mono<Void> save(ServerJsonRpcExchange exchange, JsonRpcSession session) {
		if (!session.isStarted() || session.isExpired()) {
			return Mono.empty();
		}

		return session.save();
	}
}
