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
package org.springframework.dsl.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dsl.lsp.service.DefaultDocumentStateTracker;
import org.springframework.dsl.lsp.service.DocumentStateTracker;
import org.springframework.dsl.lsp.service.Reconciler;
import org.springframework.dsl.reconcile.DefaultReconciler;
import org.springframework.dsl.reconcile.Linter;

@Configuration
public class DslAutoConfiguration {

	@ConditionalOnProperty(prefix = "spring.dsl.lsp.server", name = "mode")
//	@Import({ GenericLanguageServerController.class })
	public static class DslServicesConfig {

		@Bean
		public DocumentStateTracker documentStateTracker() {
			return new DefaultDocumentStateTracker();
		}

		@Bean
		public Reconciler reconciler(Linter linter) {
			return new DefaultReconciler(linter);
		}
	}

}
