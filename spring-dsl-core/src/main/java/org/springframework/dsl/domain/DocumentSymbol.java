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
package org.springframework.dsl.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dsl.domain.Range.RangeBuilder;
import org.springframework.dsl.support.AbstractDomainBuilder;
import org.springframework.dsl.support.DomainBuilder;

/**
 * {@code LSP} domain object for a specification {@code DocumentSymbol}.
 *
 * @author Janne Valkealahti
 *
 */
public class DocumentSymbol {

	private String name;
	private String detail;
	private SymbolKind kind;
	private Boolean deprecated;
	private Range range;
	private Range selectionRange;
	private List<DocumentSymbol> children;

	/**
	 * Instantiates a new document symbol.
	 */
	public DocumentSymbol() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public SymbolKind getKind() {
		return kind;
	}

	public void setKind(SymbolKind kind) {
		this.kind = kind;
	}

	public Boolean getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(Boolean deprecated) {
		this.deprecated = deprecated;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public Range getSelectionRange() {
		return selectionRange;
	}

	public void setSelectionRange(Range selectionRange) {
		this.selectionRange = selectionRange;
	}

	public List<DocumentSymbol> getChildren() {
		return children;
	}

	public void setChildren(List<DocumentSymbol> children) {
		this.children = children;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((deprecated == null) ? 0 : deprecated.hashCode());
		result = prime * result + ((detail == null) ? 0 : detail.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((range == null) ? 0 : range.hashCode());
		result = prime * result + ((selectionRange == null) ? 0 : selectionRange.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DocumentSymbol other = (DocumentSymbol) obj;
		if (children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!children.equals(other.children)) {
			return false;
		}
		if (deprecated == null) {
			if (other.deprecated != null) {
				return false;
			}
		} else if (!deprecated.equals(other.deprecated)) {
			return false;
		}
		if (detail == null) {
			if (other.detail != null) {
				return false;
			}
		} else if (!detail.equals(other.detail)) {
			return false;
		}
		if (kind != other.kind) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (range == null) {
			if (other.range != null) {
				return false;
			}
		} else if (!range.equals(other.range)) {
			return false;
		}
		if (selectionRange == null) {
			if (other.selectionRange != null) {
				return false;
			}
		} else if (!selectionRange.equals(other.selectionRange)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets a builder for {@link DocumentSymbol}
	 *
	 * @return the document symbol builder
	 */
	public static <P> DocumentSymbolBuilder<P> documentSymbol() {
		return new InternalDocumentSymbolBuilder<>(null);
	}

	protected static <P> DocumentSymbolBuilder<P> documentSymbol(P parent) {
		return new InternalDocumentSymbolBuilder<>(parent);
	}

	/**
	 * Builder interface for {@link DocumentSymbol}.
	 *
	 * @param <P> the parent builder type
	 */
	public interface DocumentSymbolBuilder<P> extends DomainBuilder<DocumentSymbol, P> {

		DocumentSymbolBuilder<P> name(String name);
		DocumentSymbolBuilder<P> detail(String detail);
		DocumentSymbolBuilder<P> kind(SymbolKind kind);
		DocumentSymbolBuilder<P> deprecated(Boolean deprecated);
		RangeBuilder<DocumentSymbolBuilder<P>> range();
		RangeBuilder<DocumentSymbolBuilder<P>> selectionRange();
		DocumentSymbolBuilder<DocumentSymbolBuilder<P>> child();
	}

	private static class InternalDocumentSymbolBuilder<P>
		extends AbstractDomainBuilder<DocumentSymbol, P> implements DocumentSymbolBuilder<P> {

		private String name;
		private String detail;
		private SymbolKind kind;
		private Boolean deprecated;
		private RangeBuilder<DocumentSymbolBuilder<P>> range;
		private RangeBuilder<DocumentSymbolBuilder<P>> selectionRange;
		private List<DocumentSymbolBuilder<DocumentSymbolBuilder<P>>> children = new ArrayList<>();

		InternalDocumentSymbolBuilder(P parent) {
			super(parent);
		}

		@Override
		public DocumentSymbolBuilder<P> name(String name) {
			this.name = name;
			return this;
		}

		@Override
		public DocumentSymbolBuilder<P> detail(String detail) {
			this.detail = detail;
			return this;
		}

		@Override
		public DocumentSymbolBuilder<P> kind(SymbolKind kind) {
			this.kind = kind;
			return this;
		}

		@Override
		public DocumentSymbolBuilder<P> deprecated(Boolean deprecated) {
			this.deprecated = deprecated;
			return this;
		}

		@Override
		public RangeBuilder<DocumentSymbolBuilder<P>> range() {
			this.range = Range.range(this);
			return range;
		}

		@Override
		public RangeBuilder<DocumentSymbolBuilder<P>> selectionRange() {
			this.selectionRange = Range.range(this);
			return selectionRange;
		}

		@Override
		public DocumentSymbolBuilder<DocumentSymbolBuilder<P>> child() {
			DocumentSymbolBuilder<DocumentSymbolBuilder<P>> builder = documentSymbol(this);
			this.children.add(builder);
			return builder;
		}

		@Override
		public DocumentSymbol build() {
			DocumentSymbol documentSymbol = new DocumentSymbol();
			documentSymbol.setName(name);
			documentSymbol.setDetail(detail);
			documentSymbol.setKind(kind);
			documentSymbol.setDeprecated(deprecated);
			if (range != null) {
				documentSymbol.setRange(range.build());
			}
			if (selectionRange != null) {
				documentSymbol.setSelectionRange(selectionRange.build());
			}
			if (!children.isEmpty()) {
				ArrayList<DocumentSymbol> c = new ArrayList<>();
				for (DocumentSymbolBuilder<DocumentSymbolBuilder<P>> b : children) {
					c.add(b.build());
				}
				documentSymbol.setChildren(c);
			}
			return documentSymbol;
		}
	}
}
