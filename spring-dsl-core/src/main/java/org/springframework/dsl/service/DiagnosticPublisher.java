package org.springframework.dsl.service;

import java.util.Collection;

import org.springframework.dsl.domain.Diagnostic;
import org.springframework.dsl.domain.TextDocumentIdentifier;

public interface DiagnosticPublisher {

	void publishDiagnostics(TextDocumentIdentifier docId, Collection<Diagnostic> diagnostics);

}
