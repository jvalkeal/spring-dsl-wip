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
package org.springframework.dsl.lsp.domain;

/**
 * {@code LSP} domain object for a specification {@code TextDocumentSyncOptions}.
 *
 * @author Janne Valkealahti
 *
 */
public class TextDocumentSyncOptions {

	private Boolean openClose;

	// change?: number;

	private Boolean willSave;

	private Boolean willSaveWaitUntil;

	// save?: SaveOptions;

	public Boolean getOpenClose() {
		return openClose;
	}

	public void setOpenClose(Boolean openClose) {
		this.openClose = openClose;
	}

	public Boolean getWillSave() {
		return willSave;
	}

	public void setWillSave(Boolean willSave) {
		this.willSave = willSave;
	}

	public Boolean getWillSaveWaitUntil() {
		return willSaveWaitUntil;
	}

	public void setWillSaveWaitUntil(Boolean willSaveWaitUntil) {
		this.willSaveWaitUntil = willSaveWaitUntil;
	}
}
