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

public class CompletionOptions {

	private Boolean resolveProvider;

	public Boolean getResolveProvider() {
		return resolveProvider;
	}

	public void setResolveProvider(Boolean resolveProvider) {
		this.resolveProvider = resolveProvider;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resolveProvider == null) ? 0 : resolveProvider.hashCode());
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
		CompletionOptions other = (CompletionOptions) obj;
		if (resolveProvider == null) {
			if (other.resolveProvider != null)
				return false;
		} else if (!resolveProvider.equals(other.resolveProvider))
			return false;
		return true;
	}
}
