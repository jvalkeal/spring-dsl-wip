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

import org.springframework.dsl.domain.Range.RangeBuilder;
import org.springframework.dsl.support.AbstractDomainBuilder;
import org.springframework.dsl.support.DomainBuilder;

public class TextEdit {

	private Range range;
	private String newText;

	public TextEdit() {
	}

	public TextEdit(Range range, String newText) {
		this.range = range;
		this.newText = newText;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public String getNewText() {
		return newText;
	}

	public void setNewText(String newText) {
		this.newText = newText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((newText == null) ? 0 : newText.hashCode());
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
		TextEdit other = (TextEdit) obj;
		if (newText == null) {
			if (other.newText != null)
				return false;
		} else if (!newText.equals(other.newText))
			return false;
		if (range == null) {
			if (other.range != null)
				return false;
		} else if (!range.equals(other.range))
			return false;
		return true;
	}

	public interface TextEditBuilder<P> extends DomainBuilder<TextEdit, P> {

		RangeBuilder<TextEditBuilder<P>> range();
		TextEditBuilder<P> newText(String newText);
	}

	public static <P> TextEditBuilder<P> textEdit() {
		return new InternalTextEditBuilder<>(null);
	}

	protected static <P> TextEditBuilder<P> textEdit(P parent) {
		return new InternalTextEditBuilder<>(parent);
	}

	private static class InternalTextEditBuilder<P>
			extends AbstractDomainBuilder<TextEdit, P> implements TextEditBuilder<P> {

		String newText;
		RangeBuilder<TextEditBuilder<P>> range;

		InternalTextEditBuilder(P parent) {
			super(parent);
		}

		@Override
		public RangeBuilder<TextEditBuilder<P>> range() {
			this.range = Range.range(this);
			return range;
		}

		@Override
		public TextEditBuilder<P> newText(String newText) {
			this.newText = newText;
			return this;
		}

		@Override
		public TextEdit build() {
			TextEdit textEdit = new TextEdit();
			textEdit.setNewText(newText);
			if (range != null) {
				textEdit.setRange(range.build());
			}
			return textEdit;
		}
	}
}
