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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dsl.reconcile.Linter;
import org.springframework.dsl.service.Completioner;
import org.springframework.dsl.service.Hoverer;

/**
 * Configuration for a {@code simple} sample language supporting
 * {@link Hoverer}, {@link Completioner} and {@link Linter}.
 *
 * @author Janne Valkealahti
 * @see EnableSimpleLanguage
 *
 */
@Configuration
public class SimpleLanguageConfiguration {

	@Bean
	public Hoverer simpleLanguageHoverer() {
		return new SimpleLanguageHoverer();
	}

	@Bean
	public Completioner simpleLanguageCompletioner() {
		return new SimpleLanguageCompletioner();
	}

	@Bean
	public Linter simpleLanguageLinter() {
		return new SimpleLanguageLinter();
	}
}
