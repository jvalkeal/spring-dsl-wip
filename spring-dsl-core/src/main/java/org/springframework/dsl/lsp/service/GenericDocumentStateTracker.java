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
package org.springframework.dsl.lsp.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dsl.document.BadLocationException;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.document.LanguageId;
import org.springframework.dsl.document.TextDocument;
import org.springframework.dsl.document.TextDocumentContentChange;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.TextDocumentContentChangeEvent;
import org.springframework.dsl.lsp.domain.TextDocumentIdentifier;
import org.springframework.dsl.lsp.domain.TextDocumentItem;
import org.springframework.dsl.lsp.domain.VersionedTextDocumentIdentifier;
import org.springframework.dsl.lsp.model.TrackedDocument;

import reactor.core.publisher.Mono;

/**
 *
 * @author Kris De Volder
 * @author Janne Valkealahti
 *
 */
public class GenericDocumentStateTracker implements DocumentStateTracker/*, DocumentListenerManager*/ {

	private static final Logger log = LoggerFactory.getLogger(GenericDocumentStateTracker.class);
	private Map<String, TrackedDocument> documents = new HashMap<>();

//	private ListenerList<TextDocumentContentChange> documentChangeListeners = new ListenerList<>();
//	private ListenerList<TextDocument> documentCloseListeners = new ListenerList<>();

	@Override
	public Document getDocument(String uri) {
		TrackedDocument trackedDocument = documents.get(uri);
		return trackedDocument != null ? trackedDocument.getDocument() : null;
	}

	@Override
	public boolean isIncrementalChangesSupported() {
		return true;
	}

	@Override
	public Mono<TextDocumentContentChange> didOpen(DidOpenTextDocumentParams params) {

		TextDocumentItem textDocument = params.getTextDocument();
		String uri = textDocument.getUri();
		LanguageId languageId = LanguageId.of(textDocument.getLanguageId());
		int version = textDocument.getVersion();

		String text = params.getTextDocument().getText();
		TrackedDocument td = createDocument(uri, languageId, version, text).open();
		TextDocument doc = td.getDocument();

		TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent(null, null, text);
		TextDocumentContentChange evt = new TextDocumentContentChange(doc, Arrays.asList(change));
		return Mono.just(evt);

	}

	@Override
	public final Mono<TextDocumentContentChange> didChange(DidChangeTextDocumentParams params) {
		VersionedTextDocumentIdentifier identifier = params.getTextDocument();
		String url = identifier.getUri();
		if (url != null) {
			TrackedDocument trackedDocument = documents.get(url);

			try {
				TextDocument doc = trackedDocument.getDocument();
				doc.apply(params);

//				TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent(null, null, doc.get());
				List<TextDocumentContentChangeEvent> changes = params.getContentChanges();
				TextDocumentContentChange evt = new TextDocumentContentChange(doc, changes);
				return Mono.just(evt);

			} catch (BadLocationException e) {
				log.error("", e);
			}
		}

		return Mono.empty();
	}

	@Override
	public Mono<TextDocumentContentChange> didClose(DidCloseTextDocumentParams params) {
		TextDocumentIdentifier identifier = params.getTextDocument();
		String url = identifier.getUri();
		if (url != null) {
			TrackedDocument trackedDocument = documents.get(url);
//			try {
//				trackedDocument.getDocument().apply(params);
//			} catch (BadLocationException e) {
//				log.error("", e);
//			}
		}

//	  async.execute(() -> {
//		String url = params.getTextDocument().getUri();
//		if (url!=null) {
//			TrackedDocument doc = documents.get(url);
//			if (doc!=null) {
//				if (doc.close()) {
//					log.info("Closed: {}", url);
//					//Clear diagnostics when a file is closed. This makes the errors disapear when the language is changed for
//					// a document (this resulst in a dicClose even as being sent to the language server if that changes make the
//					// document go 'out of scope'.
//					documentCloseListeners.fire(doc.getDocument());
//					documents.remove(url);
//				} else {
//					log.warn("Close event ignored! Assuming document still open because openCount = {}", doc.getOpenCount());
//				}
//			} else {
//				log.warn("Document closed, but it didn't exist! Close event ignored");
//			}
//		}
//	  });
		return Mono.empty();
	}

	@Override
	public Mono<TextDocumentContentChange> didSave(DidSaveTextDocumentParams params) {
		return Mono.empty();
	}

//	void fireDidChangeContent(TextDocument doc, List<TextDocumentContentChangeEvent> changes) {
//		documentChangeListeners.fire(new TextDocumentContentChange(doc, changes));
//	}
//
//	@Override
//	public Disposable onDidChangeContent(Consumer<TextDocumentContentChange> l) {
//		return documentChangeListeners.add(l);
//	}
//
//	@Override
//	public Disposable onDidClose(Consumer<TextDocument> l) {
//		return documentCloseListeners.add(l);
//	}

	private synchronized TextDocument getOrCreateDocument(String url) {
		TrackedDocument doc = documents.get(url);
		if (doc==null) {
			log.warn("Trying to get document ["+url+"] but it did not exists. Creating it with language-id 'plaintext'");
			doc = createDocument(url, LanguageId.PLAINTEXT, 0, "");
		}
		return doc.getDocument();
	}

	private synchronized TrackedDocument createDocument(String url, LanguageId languageId, int version, String text) {
		TrackedDocument existingDoc = documents.get(url);
		if (existingDoc!=null) {
			log.warn("Creating document ["+url+"] but it already exists. Reusing existing!");
			return existingDoc;
		}
		TrackedDocument doc = new TrackedDocument(new TextDocument(url, languageId, version, text));
		documents.put(url, doc);
		return doc;
	}
}
