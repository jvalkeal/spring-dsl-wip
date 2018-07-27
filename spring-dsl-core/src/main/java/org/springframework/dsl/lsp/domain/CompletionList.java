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

import org.springframework.dsl.lsp.domain.CompletionItem.CompletionItemBuilder;
import org.springframework.dsl.support.AbstractDomainBuilder;
import org.springframework.dsl.support.DomainBuilder;

/**
 * {@code LSP} domain object for a specification {@code CompletionList}.
 *
 * @author Janne Valkealahti
 *
 */
public class CompletionList {

	private Boolean isIncomplete;
	private List<CompletionItem> items;

	public CompletionList() {
	}

	public Boolean getIsIncomplete() {
		return isIncomplete;
	}

	public void setIsIncomplete(Boolean isIncomplete) {
		this.isIncomplete = isIncomplete;
	}

	public List<CompletionItem> getItems() {
		return items;
	}

	public void setItems(List<CompletionItem> items) {
		this.items = items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isIncomplete == null) ? 0 : isIncomplete.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		CompletionList other = (CompletionList) obj;
		if (isIncomplete == null) {
			if (other.isIncomplete != null)
				return false;
		} else if (!isIncomplete.equals(other.isIncomplete))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	public interface CompletionListBuilder<P> extends DomainBuilder<CompletionList, P>{

		CompletionListBuilder<P> isIncomplete(Boolean isIncomplete);
		CompletionItemBuilder<CompletionListBuilder<P>> item();
	}

	public static <P> CompletionListBuilder<P> completionList() {
		return new InternalCompletionListBuilder<>(null);
	}

	protected static <P> CompletionListBuilder<P> completionList(P parent) {
		return new InternalCompletionListBuilder<>(parent);
	}

	private static class InternalCompletionListBuilder<P>
			extends AbstractDomainBuilder<CompletionList, P> implements CompletionListBuilder<P> {

		private Boolean isIncomplete;
		private List<CompletionItemBuilder<CompletionListBuilder<P>>> items = new ArrayList<>();

		InternalCompletionListBuilder(P parent) {
			super(parent);
		}

		@Override
		public CompletionListBuilder<P> isIncomplete(Boolean isIncomplete) {
			this.isIncomplete = isIncomplete;
			return this;
		}

		@Override
		public CompletionItemBuilder<CompletionListBuilder<P>> item() {
			CompletionItemBuilder<CompletionListBuilder<P>> completionItemBuilder = CompletionItem.completionItem(this);
			this.items.add(completionItemBuilder);
			return completionItemBuilder;
		}

		@Override
		public CompletionList build() {
			CompletionList completionList = new CompletionList();
			completionList.setIsIncomplete(isIncomplete);
			if (!items.isEmpty()) {
				List<CompletionItem> item = new ArrayList<>();
				for (CompletionItemBuilder<CompletionListBuilder<P>> i : items) {
					item.add(i.build());
				}
				completionList.setItems(item);
			}
			return completionList;
		}
	}
}
