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
package org.springframework.dsl.lsp.server.support;

import org.springframework.dsl.lsp.server.ServerLspResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericServerLspResponse.
 */
public class GenericServerLspResponse implements ServerLspResponse {

	/** The content format. */
	private Integer contentFormat;
	
	/** The accept. */
	private Integer accept;
	
	/** The request payload. */
	private Object requestPayload;

//	@Override
//	public void setContentFormat(Integer contentFormat) {
//		this.contentFormat = contentFormat;
//	}
//
//	@Override
//	public void setAccept(Integer accept) {
//		this.accept = accept;
//	}
//
//	@Override
//	public void setRequestPayload(byte[] requestPayload) {
//		this.requestPayload = requestPayload;
//	}
//
//	public Integer getContentFormat() {
//		return contentFormat;
//	}
//
//	public Integer getAccept() {
//		return accept;
//	}
//
//	public byte[] getRequestPayload() {
//		return requestPayload;
//	}

	/* (non-Javadoc)
 * @see org.springframework.dsl.lsp.LspOutputMessage#getBody()
 */
@Override
	public Object getBody() {
		return requestPayload;
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.ServerLspResponse#setBody(java.lang.Object)
	 */
	@Override
	public void setBody(Object body) {
		this.requestPayload = body;
	}

}
