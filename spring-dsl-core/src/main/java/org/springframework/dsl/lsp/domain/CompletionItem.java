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

import java.util.List;

public class CompletionItem {

	private String label;
	private CompletionItemKind kind;
	private String detail;
	private MarkupContent documentation;
	private String sortText;
	private String filterText;
	private String insertText;
	private InsertTextFormat insertTextFormat;
	private TextEdit textEdit;
	private List<TextEdit> additionalTextEdits;
	private List<String> commitCharacters;
	private Command command;
	private Object data;

	public CompletionItem() {
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public CompletionItemKind getKind() {
		return kind;
	}

	public void setKind(CompletionItemKind kind) {
		this.kind = kind;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public MarkupContent getDocumentation() {
		return documentation;
	}

	public void setDocumentation(MarkupContent documentation) {
		this.documentation = documentation;
	}

	public String getSortText() {
		return sortText;
	}

	public void setSortText(String sortText) {
		this.sortText = sortText;
	}

	public String getFilterText() {
		return filterText;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	public String getInsertText() {
		return insertText;
	}

	public void setInsertText(String insertText) {
		this.insertText = insertText;
	}

	public InsertTextFormat getInsertTextFormat() {
		return insertTextFormat;
	}

	public void setInsertTextFormat(InsertTextFormat insertTextFormat) {
		this.insertTextFormat = insertTextFormat;
	}

	public TextEdit getTextEdit() {
		return textEdit;
	}

	public void setTextEdit(TextEdit textEdit) {
		this.textEdit = textEdit;
	}

	public List<TextEdit> getAdditionalTextEdits() {
		return additionalTextEdits;
	}

	public void setAdditionalTextEdits(List<TextEdit> additionalTextEdits) {
		this.additionalTextEdits = additionalTextEdits;
	}

	public List<String> getCommitCharacters() {
		return commitCharacters;
	}

	public void setCommitCharacters(List<String> commitCharacters) {
		this.commitCharacters = commitCharacters;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((additionalTextEdits == null) ? 0 : additionalTextEdits.hashCode());
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result + ((commitCharacters == null) ? 0 : commitCharacters.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((detail == null) ? 0 : detail.hashCode());
		result = prime * result + ((documentation == null) ? 0 : documentation.hashCode());
		result = prime * result + ((filterText == null) ? 0 : filterText.hashCode());
		result = prime * result + ((insertText == null) ? 0 : insertText.hashCode());
		result = prime * result + ((insertTextFormat == null) ? 0 : insertTextFormat.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((sortText == null) ? 0 : sortText.hashCode());
		result = prime * result + ((textEdit == null) ? 0 : textEdit.hashCode());
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
		CompletionItem other = (CompletionItem) obj;
		if (additionalTextEdits == null) {
			if (other.additionalTextEdits != null)
				return false;
		} else if (!additionalTextEdits.equals(other.additionalTextEdits))
			return false;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		if (commitCharacters == null) {
			if (other.commitCharacters != null)
				return false;
		} else if (!commitCharacters.equals(other.commitCharacters))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (detail == null) {
			if (other.detail != null)
				return false;
		} else if (!detail.equals(other.detail))
			return false;
		if (documentation == null) {
			if (other.documentation != null)
				return false;
		} else if (!documentation.equals(other.documentation))
			return false;
		if (filterText == null) {
			if (other.filterText != null)
				return false;
		} else if (!filterText.equals(other.filterText))
			return false;
		if (insertText == null) {
			if (other.insertText != null)
				return false;
		} else if (!insertText.equals(other.insertText))
			return false;
		if (insertTextFormat != other.insertTextFormat)
			return false;
		if (kind != other.kind)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (sortText == null) {
			if (other.sortText != null)
				return false;
		} else if (!sortText.equals(other.sortText))
			return false;
		if (textEdit == null) {
			if (other.textEdit != null)
				return false;
		} else if (!textEdit.equals(other.textEdit))
			return false;
		return true;
	}
}