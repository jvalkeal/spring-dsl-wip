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

import java.util.ArrayList;
import java.util.List;

public class PublishDiagnosticsParams {

	private String uri;

	private List<Diagnostic> diagnostics;

	public PublishDiagnosticsParams() {
		this.diagnostics = new ArrayList<>();
	}

	public PublishDiagnosticsParams(String uri) {
		this.uri = uri;
		this.diagnostics = new ArrayList<>();
	}

	public PublishDiagnosticsParams(String uri, List<Diagnostic> diagnostics) {
		this.uri = uri;
		this.diagnostics = diagnostics;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<Diagnostic> getDiagnostics() {
		return diagnostics;
	}

	public void setDiagnostics(List<Diagnostic> diagnostics) {
		this.diagnostics = diagnostics;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((diagnostics == null) ? 0 : diagnostics.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		PublishDiagnosticsParams other = (PublishDiagnosticsParams) obj;
		if (diagnostics == null) {
			if (other.diagnostics != null)
				return false;
		} else if (!diagnostics.equals(other.diagnostics))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
