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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.dsl.lsp.server.config.EnableLanguageServer;

/**
 * Meta annotation for enabling all needed services for a {@code simple} sample
 * language.
 * <p>
 * Grammar of a {@code simple} language is:
 * <ul>
 * <li>Every line entry is a key/value pair separated with character {@code =}.</li>
 * <li>Keys can be of type; int, long, double or string.</li>
 * <li>Values needs to be parsable into types defined by a key.</li>
 * <li>Key can only exist once, meaning there can be max of 4 valid lines.</li>
 * <li>Line can be commented out using character {@code #}.</li>
 * </ul>
 *
 * @author Janne Valkealahti
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(SimpleLanguageConfiguration.class)
@EnableLanguageServer
public @interface EnableSimpleLanguage {
}
