package org.springframework.dsl.lsp.model;

import org.springframework.dsl.document.TextDocument;

public class TrackedDocument {

	private final TextDocument doc;
	private int openCount = 0;

	public TrackedDocument(TextDocument doc) {
		this.doc = doc;
	}

	public TextDocument getDocument() {
		return doc;
	}

	public TrackedDocument open() {
		openCount++;
		return this;
	}

	public boolean close() {
		openCount--;
		return openCount <= 0;
	}

	public int getOpenCount() {
		return openCount;
	}

}
