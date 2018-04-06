package org.springframework.dsl.lsp.service;

import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;

public interface DocumentStateTracker {

	boolean canHandleIncrementalChanges();

	void didOpen(DidOpenTextDocumentParams params);

	void didChange(DidChangeTextDocumentParams params);

	void didClose(DidCloseTextDocumentParams params);

}
