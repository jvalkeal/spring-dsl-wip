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

import org.springframework.util.ObjectUtils;

/**
 * The Class AbstractLspMessageConverter.
 *
 * @param <T> the generic type
 */
public abstract class AbstractLspMessageConverter<T> implements LspMessageConverter<T> {

	/** The supported content format. */
	private Integer supportedContentFormat;

	/**
	 * Instantiates a new abstract lsp message converter.
	 *
	 * @param supportedContentFormat the supported content format
	 */
	protected AbstractLspMessageConverter(Integer supportedContentFormat) {
		this.supportedContentFormat = supportedContentFormat;
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.converter.LspMessageConverter#getSupportedContentFormat()
	 */
	@Override
	public Integer getSupportedContentFormat() {
		return supportedContentFormat;
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.converter.LspMessageConverter#canRead(java.lang.Class, java.lang.Integer)
	 */
	@Override
	public boolean canRead(Class<?> clazz, Integer contentFormat) {
		return supports(clazz) && canRead(contentFormat);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.converter.LspMessageConverter#canWrite(java.lang.Class, java.lang.Integer)
	 */
	@Override
	public boolean canWrite(Class<?> clazz, Integer contentFormat) {
		return supports(clazz) && canWrite(contentFormat);
	}

	/**
	 * Can read.
	 *
	 * @param contentFormat the content format
	 * @return true, if successful
	 */
	protected boolean canRead(Integer contentFormat) {
		if (contentFormat == null) {
			return true;
		}
		return ObjectUtils.nullSafeEquals(contentFormat, supportedContentFormat);
	}

	/**
	 * Can write.
	 *
	 * @param contentFormat the content format
	 * @return true, if successful
	 */
	protected boolean canWrite(Integer contentFormat) {
		if (contentFormat == null) {
			return true;
		}
		return ObjectUtils.nullSafeEquals(contentFormat, supportedContentFormat);
	}

	/**
	 * Supports.
	 *
	 * @param clazz the clazz
	 * @return true, if successful
	 */
	protected abstract boolean supports(Class<?> clazz);

}
