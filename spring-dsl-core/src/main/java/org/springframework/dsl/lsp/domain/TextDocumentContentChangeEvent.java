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

public class TextDocumentContentChangeEvent {

	private Range range;

	private Integer rangeLength;

	private String text;

	public TextDocumentContentChangeEvent() {
	}

	public TextDocumentContentChangeEvent(Range range, Integer rangeLength, String text) {
		this.range = range;
		this.rangeLength = rangeLength;
		this.text = text;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public Integer getRangeLength() {
		return rangeLength;
	}

	public void setRangeLength(Integer rangeLength) {
		this.rangeLength = rangeLength;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((range == null) ? 0 : range.hashCode());
		result = prime * result + ((rangeLength == null) ? 0 : rangeLength.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		TextDocumentContentChangeEvent other = (TextDocumentContentChangeEvent) obj;
		if (range == null) {
			if (other.range != null)
				return false;
		} else if (!range.equals(other.range))
			return false;
		if (rangeLength == null) {
			if (other.rangeLength != null)
				return false;
		} else if (!rangeLength.equals(other.rangeLength))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
}
