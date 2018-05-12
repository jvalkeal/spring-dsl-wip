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

public class MarkupContent {

	private MarkupKind kind;
	private String value;

	public MarkupContent() {
	}

	public MarkupContent(MarkupKind kind, String value) {
		this.kind = kind;
		this.value = value;
	}

	public MarkupKind getKind() {
		return kind;
	}

	public void setKind(MarkupKind kind) {
		this.kind = kind;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		MarkupContent other = (MarkupContent) obj;
		if (kind != other.kind)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public interface MarkupContentBuilder<P> {
		MarkupContentBuilder<P> kind(MarkupKind kind);
		MarkupContentBuilder<P> value(String value);
		P and();
		MarkupContent build();
	}

	public static <P> MarkupContentBuilder<P> markupContent() {
		return new InternalMarkupContentBuilder<>(null);
	}

	protected static <P> MarkupContentBuilder<P> markupContent(P parent) {
		return new InternalMarkupContentBuilder<>(parent);
	}

	private static class InternalMarkupContentBuilder<P> implements MarkupContentBuilder<P> {

		final P parent;
		MarkupKind kind;
		String value;

		InternalMarkupContentBuilder(P parent) {
			this.parent = parent;
		}

		@Override
		public MarkupContentBuilder<P> kind(MarkupKind kind) {
			this.kind = kind;
			return this;
		}

		@Override
		public MarkupContentBuilder<P> value(String value) {
			this.value = value;
			return this;
		}

		@Override
		public P and() {
			return parent;
		}

		@Override
		public MarkupContent build() {
			return new MarkupContent(kind, value);
		}
	}
}
