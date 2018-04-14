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

public class InitializeParams {

	private Integer processId;

	private String rootUri;

	private Object initializationOptions;

	private ClientCapabilities capabilities;

	private String trace;

	public Integer getProcessId() {
		return processId;
	}

	public void setProcessId(Integer processId) {
		this.processId = processId;
	}

	public String getRootUri() {
		return rootUri;
	}

	public void setRootUri(String rootUri) {
		this.rootUri = rootUri;
	}

	public Object getInitializationOptions() {
		return initializationOptions;
	}

	public void setInitializationOptions(Object initializationOptions) {
		this.initializationOptions = initializationOptions;
	}

	public ClientCapabilities getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(ClientCapabilities capabilities) {
		this.capabilities = capabilities;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((capabilities == null) ? 0 : capabilities.hashCode());
		result = prime * result + ((initializationOptions == null) ? 0 : initializationOptions.hashCode());
		result = prime * result + ((processId == null) ? 0 : processId.hashCode());
		result = prime * result + ((rootUri == null) ? 0 : rootUri.hashCode());
		result = prime * result + ((trace == null) ? 0 : trace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InitializeParams other = (InitializeParams) obj;
		if (capabilities == null) {
			if (other.capabilities != null)
				return false;
		} else if (!capabilities.equals(other.capabilities))
			return false;
		if (initializationOptions == null) {
			if (other.initializationOptions != null)
				return false;
		} else if (!initializationOptions.equals(other.initializationOptions))
			return false;
		if (processId == null) {
			if (other.processId != null)
				return false;
		} else if (!processId.equals(other.processId))
			return false;
		if (rootUri == null) {
			if (other.rootUri != null)
				return false;
		} else if (!rootUri.equals(other.rootUri))
			return false;
		if (trace == null) {
			if (other.trace != null)
				return false;
		} else if (!trace.equals(other.trace))
			return false;
		return true;
	}
}
