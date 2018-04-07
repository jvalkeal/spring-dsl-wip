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

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dsl.document.Document;
import org.springframework.dsl.reconcile.Linter;
import org.springframework.dsl.reconcile.ReconcileProblem;

import reactor.core.publisher.Flux;

/**
 * A {@link Linter} for a {@code simple} language.
 *
 * @author Janne Valkealahti
 *
 */
public class SimpleLanguageLinter implements Linter {

	@Override
	public Flux<ReconcileProblem> lint(Document doc) {

		String content = doc.get();
		List<String> lines = new BufferedReader(new StringReader(content)).lines().collect(Collectors.toList());

		for (String line : lines) {
			String[] split = line.split("=");


		}
		return Flux.empty();
	}
}
