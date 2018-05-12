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

import org.springframework.dsl.lsp.domain.MarkupContent.MarkupContentBuilder;
import org.springframework.dsl.lsp.domain.Range.RangeBuilder;

public class Hover {

	private MarkupContent contents;
	private Range range;

	public Hover() {
	}

	public Hover(MarkupContent contents, Range range) {
		this.contents = contents;
		this.range = range;
	}

	public MarkupContent getContents() {
		return contents;
	}

	public void setContents(MarkupContent contents) {
		this.contents = contents;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contents == null) ? 0 : contents.hashCode());
		result = prime * result + ((range == null) ? 0 : range.hashCode());
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
		Hover other = (Hover) obj;
		if (contents == null) {
			if (other.contents != null)
				return false;
		} else if (!contents.equals(other.contents))
			return false;
		if (range == null) {
			if (other.range != null)
				return false;
		} else if (!range.equals(other.range))
			return false;
		return true;
	}

	public interface HoverBuilder<P> {
		MarkupContentBuilder<HoverBuilder<P>> contents();
		RangeBuilder<HoverBuilder<P>> range();
		P and();
		Hover build();
	}

	public static <P> HoverBuilder<P> hover() {
		return new InternalHoverBuilder<>(null);
	}

	protected static <P> HoverBuilder<P> hover(P parent) {
		return new InternalHoverBuilder<>(parent);
	}

	private static class InternalHoverBuilder<P> implements HoverBuilder<P> {

		private final P parent;
		MarkupContentBuilder<HoverBuilder<P>> markupContent;
		RangeBuilder<HoverBuilder<P>> range;

		InternalHoverBuilder(P parent) {
			this.parent = parent;
		}

		@Override
		public RangeBuilder<HoverBuilder<P>> range() {
			this.range = Range.range(this);
			return range;
		}

		@Override
		public MarkupContentBuilder<HoverBuilder<P>> contents() {
			this.markupContent = MarkupContent.markupContent(this);
			return markupContent;
		}

		@Override
		public P and() {
			return parent;
		}

		@Override
		public Hover build() {
			Hover hover = new Hover();
			if (markupContent != null) {
				hover.setContents(markupContent.build());
			}
			if (range != null) {
				hover.setRange(range.build());
			}
			return hover;
		}
	}
}
