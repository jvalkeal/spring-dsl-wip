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
package demo.simpledsl;

import org.springframework.dsl.lsp.model.Document;
import org.springframework.dsl.lsp.model.ReconcileProblem;
import org.springframework.dsl.lsp.service.Reconciler;

import reactor.core.publisher.Flux;

/**
 * A {@link Reconciler} implementation for a {@code simple} sample language.
 *
 * @author Janne Valkealahti
 *
 */
public class SimpleLanguageReconciler implements Reconciler {

	@Override
	public Flux<ReconcileProblem> reconcile(Document document) {
		return Flux.empty();
	}
}
