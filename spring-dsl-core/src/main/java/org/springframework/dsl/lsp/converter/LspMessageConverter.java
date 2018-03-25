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
package org.springframework.dsl.lsp.converter;

import org.springframework.dsl.lsp.LspInputMessage;
import org.springframework.dsl.lsp.LspOutputMessage;

// TODO: Auto-generated Javadoc
/**
 * The Interface LspMessageConverter.
 *
 * @param <T> the generic type
 */
public interface LspMessageConverter<T> {

	/**
	 * Can read.
	 *
	 * @param clazz the clazz
	 * @param contentFormat the content format
	 * @return true, if successful
	 */
	boolean canRead(Class<?> clazz, Integer contentFormat);

	/**
	 * Can write.
	 *
	 * @param clazz the clazz
	 * @param contentFormat the content format
	 * @return true, if successful
	 */
	boolean canWrite(Class<?> clazz, Integer contentFormat);

	/**
	 * Gets the supported content format.
	 *
	 * @return the supported content format
	 */
	Integer getSupportedContentFormat();

	/**
	 * Read.
	 *
	 * @param clazz the clazz
	 * @param inputMessage the input message
	 * @return the t
	 */
	T read(Class<? extends T> clazz, LspInputMessage inputMessage);

	/**
	 * Write.
	 *
	 * @param t the t
	 * @param outputMessage the output message
	 */
	void write(T t, LspOutputMessage outputMessage);
}
