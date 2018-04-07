package org.springframework.dsl.lsp.service;

import java.util.Collection;

import org.springframework.dsl.lsp.domain.Diagnostic;
import org.springframework.dsl.lsp.domain.TextDocumentIdentifier;

public interface DiagnosticPublisher {

	void publishDiagnostics(TextDocumentIdentifier docId, Collection<Diagnostic> diagnostics);

}
