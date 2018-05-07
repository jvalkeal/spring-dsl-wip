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
package org.springframework.dsl.lsp4j.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dsl.lsp.domain.Command;
import org.springframework.dsl.lsp.domain.CompletionContext;
import org.springframework.dsl.lsp.domain.CompletionItem;
import org.springframework.dsl.lsp.domain.CompletionItemKind;
import org.springframework.dsl.lsp.domain.CompletionOptions;
import org.springframework.dsl.lsp.domain.CompletionParams;
import org.springframework.dsl.lsp.domain.CompletionTriggerKind;
import org.springframework.dsl.lsp.domain.Diagnostic;
import org.springframework.dsl.lsp.domain.DiagnosticSeverity;
import org.springframework.dsl.lsp.domain.DidChangeTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidCloseTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidOpenTextDocumentParams;
import org.springframework.dsl.lsp.domain.DidSaveTextDocumentParams;
import org.springframework.dsl.lsp.domain.Hover;
import org.springframework.dsl.lsp.domain.InitializeParams;
import org.springframework.dsl.lsp.domain.InitializeResult;
import org.springframework.dsl.lsp.domain.InitializedParams;
import org.springframework.dsl.lsp.domain.InsertTextFormat;
import org.springframework.dsl.lsp.domain.MarkedString;
import org.springframework.dsl.lsp.domain.MarkupContent;
import org.springframework.dsl.lsp.domain.MarkupKind;
import org.springframework.dsl.lsp.domain.Position;
import org.springframework.dsl.lsp.domain.PublishDiagnosticsParams;
import org.springframework.dsl.lsp.domain.Range;
import org.springframework.dsl.lsp.domain.ServerCapabilities;
import org.springframework.dsl.lsp.domain.TextDocumentContentChangeEvent;
import org.springframework.dsl.lsp.domain.TextDocumentIdentifier;
import org.springframework.dsl.lsp.domain.TextDocumentItem;
import org.springframework.dsl.lsp.domain.TextDocumentPositionParams;
import org.springframework.dsl.lsp.domain.TextDocumentSyncKind;
import org.springframework.dsl.lsp.domain.TextDocumentSyncOptions;
import org.springframework.dsl.lsp.domain.TextEdit;
import org.springframework.dsl.lsp.domain.VersionedTextDocumentIdentifier;

/**
 * Utilities to convert {@code POJO}s between {@code LSP4J} and
 * {@code Spring DSL} {@code LSP} domain objects.
 *
 * @author Janne Valkealahti
 *
 */
public final class ConverterUtils {

	/**
	 * Convert {@code Spring DSL} {@link InitializeParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.InitializeParams}.
	 *
	 * @param from the {@code Spring DSL InitializeParams}
	 * @return {@code LSP4J InitializeParams}
	 */
	public static org.eclipse.lsp4j.InitializeParams toInitializeParams(InitializeParams from) {
		org.eclipse.lsp4j.InitializeParams to = new org.eclipse.lsp4j.InitializeParams();
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link InitializeParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.InitializeParams}.
	 *
	 * @param from the {@code Spring DSL InitializeParams}
	 * @return {@code LSP4J InitializeParams}
	 */
	public static InitializeParams toInitializeParams(org.eclipse.lsp4j.InitializeParams from) {
		InitializeParams to = new InitializeParams();
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link InitializedParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.InitializedParams}.
	 *
	 * @param from the {@code Spring DSL InitializedParams}
	 * @return {@code LSP4J InitializedParams}
	 */
	public static org.eclipse.lsp4j.InitializedParams toInitializedParams(InitializedParams from) {
		org.eclipse.lsp4j.InitializedParams to = new org.eclipse.lsp4j.InitializedParams();
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link InitializedParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.InitializedParams}.
	 *
	 * @param from the {@code Spring DSL InitializedParams}
	 * @return {@code LSP4J InitializedParams}
	 */
	public static InitializedParams toInitializedParams(org.eclipse.lsp4j.InitializedParams from) {
		InitializedParams to = new InitializedParams();
		return to;
	}

	public static InitializeResult toInitializeResult(org.eclipse.lsp4j.InitializeResult from) {
		InitializeResult to = new InitializeResult();
		if (from.getCapabilities() != null) {
			to.setCapabilities(toServerCapabilities(from.getCapabilities()));
		}
		return to;
	}

	public static org.eclipse.lsp4j.InitializeResult toInitializeResult(InitializeResult from) {
		org.eclipse.lsp4j.InitializeResult to = new org.eclipse.lsp4j.InitializeResult();
		if (from.getCapabilities() != null) {
			to.setCapabilities(toServerCapabilities(from.getCapabilities()));
		}
		return to;
	}

	public static ServerCapabilities toServerCapabilities(org.eclipse.lsp4j.ServerCapabilities from) {
		ServerCapabilities to = new ServerCapabilities();
		to.setHoverProvider(from.getHoverProvider());
		to.setCompletionProvider(toCompletionOptions(from.getCompletionProvider()));
		return to;
	}

	public static org.eclipse.lsp4j.ServerCapabilities toServerCapabilities(ServerCapabilities from) {
		org.eclipse.lsp4j.ServerCapabilities to = new org.eclipse.lsp4j.ServerCapabilities();
		to.setHoverProvider(from.getHoverProvider());
		to.setCompletionProvider(toCompletionOptions(from.getCompletionProvider()));
		if (from.getTextDocumentSyncOptions() != null) {
			to.setTextDocumentSync(toTextDocumentSyncOptions(from.getTextDocumentSyncOptions()));
		} else if (from.getTextDocumentSyncKind() != null) {
			to.setTextDocumentSync(toTextDocumentSyncKind(from.getTextDocumentSyncKind()));
		}
		return to;
	}

	public static TextDocumentSyncOptions toTextDocumentSyncOptions(org.eclipse.lsp4j.TextDocumentSyncOptions from) {
		TextDocumentSyncOptions to = new TextDocumentSyncOptions();
		return to;
	}

	public static org.eclipse.lsp4j.TextDocumentSyncOptions toTextDocumentSyncOptions(TextDocumentSyncOptions from) {
		org.eclipse.lsp4j.TextDocumentSyncOptions to = new org.eclipse.lsp4j.TextDocumentSyncOptions();
		return to;
	}

	public static TextDocumentSyncKind toTextDocumentSyncKind(org.eclipse.lsp4j.TextDocumentSyncKind from) {
		if (from == null) {
			return null;
		}
		return TextDocumentSyncKind.valueOf(from.name());
	}

	public static org.eclipse.lsp4j.TextDocumentSyncKind toTextDocumentSyncKind(TextDocumentSyncKind from) {
		if (from == null) {
			return null;
		}
		return org.eclipse.lsp4j.TextDocumentSyncKind.valueOf(from.name());
	}

	/**
	 * Convert {@code Spring DSL} {@link CompletionOptions} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.CompletionOptions}.
	 *
	 * @param from the {@code Spring DSL CompletionOptions}
	 * @return {@code LSP4J CompletionOptions}
	 */
	public static org.eclipse.lsp4j.CompletionOptions toCompletionOptions(CompletionOptions from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.CompletionOptions to = new org.eclipse.lsp4j.CompletionOptions();
		to.setResolveProvider(from.getResolveProvider());
		if (from.getTriggerCharacters() != null) {
			to.setTriggerCharacters(from.getTriggerCharacters());
		}
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.CompletionOptions} to {@code Spring DSL}
	 * {@link CompletionOptions}.
	 *
	 * @param from the {@code LSP4J CompletionOptions}
	 * @return {@code Spring DSL CompletionOptions}
	 */
	public static CompletionOptions toCompletionOptions(org.eclipse.lsp4j.CompletionOptions from) {
		if (from == null) {
			return null;
		}
		CompletionOptions to = new CompletionOptions();
		to.setResolveProvider(from.getResolveProvider());
		if (from.getTriggerCharacters() != null) {
			to.setTriggerCharacters(from.getTriggerCharacters());
		}
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link TextDocumentContentChangeEvent} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.TextDocumentContentChangeEvent}.
	 *
	 * @param from the {@code Spring DSL TextDocumentContentChangeEvent}
	 * @return {@code LSP4J TextDocumentContentChangeEvent}
	 */
	public static org.eclipse.lsp4j.TextDocumentContentChangeEvent toTextDocumentContentChangeEvent(TextDocumentContentChangeEvent from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.TextDocumentContentChangeEvent to = new org.eclipse.lsp4j.TextDocumentContentChangeEvent();
		to.setRange(toRange(from.getRange()));
		to.setRangeLength(from.getRangeLength());
		to.setText(from.getText());
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.TextDocumentContentChangeEvent} to {@code Spring DSL}
	 * {@link TextDocumentContentChangeEvent}.
	 *
	 * @param from the {@code LSP4J CompletionOptions}
	 * @return {@code Spring DSL CompletionOptions}
	 */
	public static TextDocumentContentChangeEvent toTextDocumentContentChangeEvent(org.eclipse.lsp4j.TextDocumentContentChangeEvent from) {
		if (from == null) {
			return null;
		}
		TextDocumentContentChangeEvent to = new TextDocumentContentChangeEvent();
		to.setRange(toRange(from.getRange()));
		to.setRangeLength(from.getRangeLength());
		to.setText(from.getText());
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link DidChangeTextDocumentParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.DidChangeTextDocumentParams}.
	 *
	 * @param from the {@code Spring DSL DidChangeTextDocumentParams}
	 * @return {@code LSP4J DidChangeTextDocumentParams}
	 */
	public static org.eclipse.lsp4j.DidChangeTextDocumentParams toDidChangeTextDocumentParams(
			DidChangeTextDocumentParams from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.DidChangeTextDocumentParams to = new org.eclipse.lsp4j.DidChangeTextDocumentParams();
		to.setTextDocument(toVersionedTextDocumentIdentifier(from.getTextDocument()));
		if (from.getContentChanges() != null) {
			ArrayList<org.eclipse.lsp4j.TextDocumentContentChangeEvent> changes = new ArrayList<>();
			for (TextDocumentContentChangeEvent event : from.getContentChanges()) {
				changes.add(toTextDocumentContentChangeEvent(event));
			}
			to.setContentChanges(changes);
		}
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.DidChangeTextDocumentParams} to {@code Spring DSL}
	 * {@link DidChangeTextDocumentParams}.
	 *
	 * @param from the {@code LSP4J DidChangeTextDocumentParams}
	 * @return {@code Spring DSL DidChangeTextDocumentParams}
	 */
	public static DidChangeTextDocumentParams toDidChangeTextDocumentParams(
			org.eclipse.lsp4j.DidChangeTextDocumentParams from) {
		if (from == null) {
			return null;
		}
		DidChangeTextDocumentParams to = new DidChangeTextDocumentParams();
		to.setTextDocument(toVersionedTextDocumentIdentifier(from.getTextDocument()));
		if (from.getContentChanges() != null) {
			ArrayList<TextDocumentContentChangeEvent> changes = new ArrayList<>();
			for (org.eclipse.lsp4j.TextDocumentContentChangeEvent event : from.getContentChanges()) {
				changes.add(toTextDocumentContentChangeEvent(event));
			}
			to.setContentChanges(changes);
		}
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link DidCloseTextDocumentParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.DidCloseTextDocumentParams}.
	 *
	 * @param from the {@code Spring DSL DidCloseTextDocumentParams}
	 * @return {@code LSP4J DidCloseTextDocumentParams}
	 */
	public static org.eclipse.lsp4j.DidCloseTextDocumentParams toDidCloseTextDocumentParams(DidCloseTextDocumentParams from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.DidCloseTextDocumentParams to = new org.eclipse.lsp4j.DidCloseTextDocumentParams();
		to.setTextDocument(toTextDocumentIdentifier(from.getTextDocument()));
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.DidCloseTextDocumentParams} to {@code Spring DSL}
	 * {@link DidCloseTextDocumentParams}.
	 *
	 * @param from the {@code LSP4J DidCloseTextDocumentParams}
	 * @return {@code Spring DSL DidCloseTextDocumentParams}
	 */
	public static DidCloseTextDocumentParams toDidCloseTextDocumentParams(org.eclipse.lsp4j.DidCloseTextDocumentParams from) {
		if (from == null) {
			return null;
		}
		DidCloseTextDocumentParams to = new DidCloseTextDocumentParams();
		to.setTextDocument(toTextDocumentIdentifier(from.getTextDocument()));
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link DidOpenTextDocumentParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.DidOpenTextDocumentParams}.
	 *
	 * @param from the {@code Spring DSL DidOpenTextDocumentParams}
	 * @return {@code LSP4J DidOpenTextDocumentParams}
	 */
	public static org.eclipse.lsp4j.DidOpenTextDocumentParams toDidOpenTextDocumentParams(
			DidOpenTextDocumentParams from) {
		org.eclipse.lsp4j.DidOpenTextDocumentParams to = new org.eclipse.lsp4j.DidOpenTextDocumentParams();
		if (from != null) {
			to.setTextDocument(toTextDocumentItem(from.getTextDocument()));
		}
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.DidOpenTextDocumentParams} to {@code Spring DSL}
	 * {@link DidOpenTextDocumentParams}.
	 *
	 * @param from the {@code LSP4J DidOpenTextDocumentParams}
	 * @return {@code Spring DSL DidOpenTextDocumentParams}
	 */
	public static DidOpenTextDocumentParams toDidOpenTextDocumentParams(
			org.eclipse.lsp4j.DidOpenTextDocumentParams from) {
		DidOpenTextDocumentParams to = new DidOpenTextDocumentParams();
		if (from != null) {
			to.setTextDocument(toTextDocumentItem(from.getTextDocument()));
		}
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link DidSaveTextDocumentParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.DidSaveTextDocumentParams}.
	 *
	 * @param from the {@code Spring DSL DidSaveTextDocumentParams}
	 * @return {@code LSP4J DidSaveTextDocumentParams}
	 */
	public static org.eclipse.lsp4j.DidSaveTextDocumentParams toDidSaveTextDocumentParams(
			DidSaveTextDocumentParams from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.DidSaveTextDocumentParams to = new org.eclipse.lsp4j.DidSaveTextDocumentParams();
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.DidSaveTextDocumentParams} to {@code Spring DSL}
	 * {@link DidSaveTextDocumentParams}.
	 *
	 * @param from the {@code LSP4J DidSaveTextDocumentParams}
	 * @return {@code Spring DSL DidSaveTextDocumentParams}
	 */
	public static DidSaveTextDocumentParams toDidSaveTextDocumentParams(
			org.eclipse.lsp4j.DidSaveTextDocumentParams from) {
		if (from == null) {
			return null;
		}
		DidSaveTextDocumentParams to = new DidSaveTextDocumentParams();
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link TextDocumentItem} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.TextDocumentItem}.
	 *
	 * @param from the {@code Spring DSL TextDocumentItem}
	 * @return {@code LSP4J TextDocumentItem}
	 */
	public static org.eclipse.lsp4j.TextDocumentItem toTextDocumentItem(TextDocumentItem from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.TextDocumentItem to = new org.eclipse.lsp4j.TextDocumentItem();
		to.setUri(from.getUri());
		to.setLanguageId(from.getLanguageId());
		to.setVersion(from.getVersion());
		to.setText(from.getText());
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.TextDocumentItem} to {@code Spring DSL}
	 * {@link TextDocumentItem}.
	 *
	 * @param from the {@code LSP4J TextDocumentItem}
	 * @return {@code Spring DSL TextDocumentItem}
	 */
	public static TextDocumentItem toTextDocumentItem(org.eclipse.lsp4j.TextDocumentItem from) {
		if (from == null) {
			return null;
		}
		TextDocumentItem to = new TextDocumentItem();
		to.setUri(from.getUri());
		to.setLanguageId(from.getLanguageId());
		to.setVersion(from.getVersion());
		to.setText(from.getText());
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link PublishDiagnosticsParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.PublishDiagnosticsParams}.
	 *
	 * @param from the {@code Spring DSL PublishDiagnosticsParams}
	 * @return {@code LSP4J PublishDiagnosticsParams}
	 */
	public static org.eclipse.lsp4j.PublishDiagnosticsParams toPublishDiagnosticsParams(PublishDiagnosticsParams from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.PublishDiagnosticsParams to = new org.eclipse.lsp4j.PublishDiagnosticsParams();
		to.setUri(from.getUri());
		if (from.getDiagnostics() != null) {
			List<org.eclipse.lsp4j.Diagnostic> diagnostics = new ArrayList<org.eclipse.lsp4j.Diagnostic>();
			for (Diagnostic d : from.getDiagnostics()) {
				diagnostics.add(toDiagnostic(d));
			}
			to.setDiagnostics(diagnostics);
		}
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.PublishDiagnosticsParams} to
	 * {@code Spring DSL} {@link PublishDiagnosticsParams}.
	 *
	 * @param from the {@code LSP4J PublishDiagnosticsParams}
	 * @return {@code Spring DSL PublishDiagnosticsParams}
	 */
	public static PublishDiagnosticsParams toPublishDiagnosticsParams(org.eclipse.lsp4j.PublishDiagnosticsParams from) {
		if (from == null) {
			return null;
		}
		PublishDiagnosticsParams to = new PublishDiagnosticsParams();
		to.setUri(from.getUri());
		if (from.getDiagnostics() != null) {
			List<Diagnostic> diagnostics = new ArrayList<Diagnostic>();
			for (org.eclipse.lsp4j.Diagnostic d : from.getDiagnostics()) {
				diagnostics.add(toDiagnostic(d));
			}
			to.setDiagnostics(diagnostics);
		}
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link Diagnostic} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.Diagnostic}.
	 *
	 * @param from the {@code Spring DSL Diagnostic}
	 * @return {@code LSP4J Diagnostic}
	 */
	public static org.eclipse.lsp4j.Diagnostic toDiagnostic(Diagnostic from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.Diagnostic to = new org.eclipse.lsp4j.Diagnostic();
		to.setRange(toRange(from.getRange()));
		to.setSeverity(toDiagnosticSeverity(from.getSeverity()));
		to.setCode(from.getCode());
		to.setSource(from.getSource());
		to.setMessage(from.getMessage());
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.Diagnostic} to {@code Spring DSL}
	 * {@link Diagnostic}.
	 *
	 * @param from the {@code LSP4J Diagnostic}
	 * @return {@code Spring DSL Diagnostic}
	 */
	public static Diagnostic toDiagnostic(org.eclipse.lsp4j.Diagnostic from) {
		if (from == null) {
			return null;
		}
		Diagnostic to = new Diagnostic();
		to.setRange(toRange(from.getRange()));
		to.setSeverity(toDiagnosticSeverity(from.getSeverity()));
		to.setCode(from.getCode());
		to.setSource(from.getSource());
		to.setMessage(from.getMessage());
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link Range} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.Range}.
	 *
	 * @param from the {@code Spring DSL Range}
	 * @return {@code LSP4J Range}
	 */
	public static org.eclipse.lsp4j.Range toRange(Range from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.Range to = new org.eclipse.lsp4j.Range();
		to.setStart(toPosition(from.getStart()));
		to.setEnd(toPosition(from.getEnd()));
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.Range} to {@code Spring DSL}
	 * {@link Range}.
	 *
	 * @param from the {@code LSP4J Range}
	 * @return {@code Spring DSL Range}
	 */
	public static Range toRange(org.eclipse.lsp4j.Range from) {
		if (from == null) {
			return null;
		}
		Range to = new Range();
		to.setStart(toPosition(from.getStart()));
		to.setEnd(toPosition(from.getEnd()));
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link Position} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.Position}.
	 *
	 * @param from the {@code Spring DSL Position}
	 * @return {@code LSP4J Position}
	 */
	public static org.eclipse.lsp4j.Position toPosition(Position from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.Position to = new org.eclipse.lsp4j.Position();
		to.setLine(from.getLine());
		to.setCharacter(from.getCharacter());
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.Position} to {@code Spring DSL}
	 * {@link Position}.
	 *
	 * @param from the {@code LSP4J Position}
	 * @return {@code Spring DSL Position}
	 */
	public static Position toPosition(org.eclipse.lsp4j.Position from) {
		if (from == null) {
			return null;
		}
		Position to = new Position();
		to.setLine(from.getLine());
		to.setCharacter(from.getCharacter());
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link DiagnosticSeverity} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.DiagnosticSeverity}.
	 *
	 * @param from the {@code Spring DSL DiagnosticSeverity}
	 * @return {@code LSP4J DiagnosticSeverity}
	 */
	public static org.eclipse.lsp4j.DiagnosticSeverity toDiagnosticSeverity(DiagnosticSeverity from) {
		if (from == null) {
			return null;
		}
		return org.eclipse.lsp4j.DiagnosticSeverity.valueOf(from.name());
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.DiagnosticSeverity} to {@code Spring DSL}
	 * {@link DiagnosticSeverity}.
	 *
	 * @param from the {@code LSP4J DiagnosticSeverity}
	 * @return {@code Spring DSL DiagnosticSeverity}
	 */
	public static DiagnosticSeverity toDiagnosticSeverity(org.eclipse.lsp4j.DiagnosticSeverity from) {
		if (from == null) {
			return null;
		}
		return DiagnosticSeverity.valueOf(from.name());
	}

	/**
	 * Convert {@code Spring DSL} {@link Hover} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.Hover}.
	 *
	 * @param from the {@code Spring DSL Hover}
	 * @return {@code LSP4J Hover}
	 */
	public static org.eclipse.lsp4j.Hover toHover(Hover from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.Hover to = new org.eclipse.lsp4j.Hover();
		to.setRange(toRange(from.getRange()));
		org.eclipse.lsp4j.MarkupContent markupContent = toMarkupContent(from.getContents());
		if (markupContent != null) {
			to.setContents(markupContent);
		}
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.Hover} to {@code Spring DSL}
	 * {@link Hover}.
	 *
	 * @param from the {@code LSP4J Hover}
	 * @return {@code Spring DSL Hover}
	 */
	public static Hover toHover(org.eclipse.lsp4j.Hover from) {
		if (from == null) {
			return null;
		}
		Hover to = new Hover();
		to.setRange(toRange(from.getRange()));
		to.setContents(toMarkupContent(from.getContents() != null ? from.getContents().getRight() : null));
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link MarkupContent} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.MarkupContent}.
	 *
	 * @param from the {@code Spring DSL MarkupContent}
	 * @return {@code LSP4J MarkupContent}
	 */
	public static org.eclipse.lsp4j.MarkupContent toMarkupContent(MarkupContent from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.MarkupContent to = new org.eclipse.lsp4j.MarkupContent();
		to.setValue(from.getValue());
		to.setKind(from.getKind() != null ? from.getKind().toString() : null);
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.MarkupContent} to
	 * {@code Spring DSL} {@link MarkupContent}.
	 *
	 * @param from the {@code LSP4J MarkupContent}
	 * @return {@code Spring DSL MarkupContent}
	 */
	public static MarkupContent toMarkupContent(org.eclipse.lsp4j.MarkupContent from) {
		if (from == null) {
			return null;
		}
		MarkupContent to = new MarkupContent();
		to.setValue(from.getValue());
		to.setKind(from.getKind() != null ? MarkupKind.valueOf(from.getKind()) : null);
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link TextDocumentPositionParams} to
	 * {@code LSP4J} {@link org.eclipse.lsp4j.TextDocumentPositionParams}.
	 *
	 * @param from the {@code Spring DSL TextDocumentPositionParams}
	 * @return {@code LSP4J TextDocumentPositionParams}
	 */
	public static org.eclipse.lsp4j.TextDocumentPositionParams toTextDocumentPositionParams(
			TextDocumentPositionParams from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.TextDocumentPositionParams to = new org.eclipse.lsp4j.TextDocumentPositionParams();
		to.setPosition(toPosition(from.getPosition()));
		to.setTextDocument(toTextDocumentIdentifier(from.getTextDocument()));
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.TextDocumentPositionParams} to
	 * {@code Spring DSL} {@link TextDocumentPositionParams}.
	 *
	 * @param from the {@code LSP4J TextDocumentPositionParams}
	 * @return {@code Spring DSL TextDocumentPositionParams}
	 */
	public static TextDocumentPositionParams toTextDocumentPositionParams(
			org.eclipse.lsp4j.TextDocumentPositionParams from) {
		if (from == null) {
			return null;
		}
		TextDocumentPositionParams to = new TextDocumentPositionParams();
		to.setPosition(toPosition(from.getPosition()));
		to.setTextDocument(toTextDocumentIdentifier(from.getTextDocument()));
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link CompletionItem} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.CompletionItem}.
	 *
	 * @param from the {@code Spring DSL CompletionItem}
	 * @return {@code LSP4J CompletionItem}
	 */
	public static org.eclipse.lsp4j.CompletionItem toCompletionItem(CompletionItem from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.CompletionItem to = new org.eclipse.lsp4j.CompletionItem();
		to.setLabel(from.getLabel());
		to.setKind(from.getKind() != null ? org.eclipse.lsp4j.CompletionItemKind.valueOf(from.getKind().toString())
				: null);
		to.setDetail(from.getDetail());
		if (from.getDocumentation() != null) {
			to.setDocumentation(toMarkupContent(from.getDocumentation()));
		}
		to.setSortText(from.getSortText());
		to.setFilterText(from.getFilterText());
		to.setInsertText(from.getInsertText());
		to.setInsertTextFormat(from.getInsertTextFormat() != null
				? org.eclipse.lsp4j.InsertTextFormat.valueOf(from.getInsertTextFormat().toString())
				: null);
		to.setTextEdit(toTextEdit(from.getTextEdit()));
		if (from.getAdditionalTextEdits() != null) {
			ArrayList<org.eclipse.lsp4j.TextEdit> additional = new ArrayList<>();
			for (TextEdit textEdit : from.getAdditionalTextEdits()) {
				additional.add(toTextEdit(textEdit));
			}
			to.setAdditionalTextEdits(additional);
		}
		if (from.getCommitCharacters() != null) {
			to.setCommitCharacters(from.getCommitCharacters());
		}
		to.setCommand(toCommand(from.getCommand()));
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.CompletionItem} to
	 * {@code Spring DSL} {@link CompletionItem}.
	 *
	 * @param from the {@code LSP4J CompletionItem}
	 * @return {@code Spring DSL CompletionItem}
	 */
	public static CompletionItem toCompletionItem(org.eclipse.lsp4j.CompletionItem from) {
		if (from == null) {
			return null;
		}
		CompletionItem to = new CompletionItem();
		to.setLabel(from.getLabel());
		to.setKind(from.getKind() != null ? CompletionItemKind.valueOf(from.getKind().toString())
				: null);
		to.setDetail(from.getDetail());
		if (from.getDocumentation() != null) {
			if (from.getDocumentation().getRight() != null) {
				to.setDocumentation(toMarkupContent(from.getDocumentation().getRight()));
			}
		}
		to.setSortText(from.getSortText());
		to.setFilterText(from.getFilterText());
		to.setInsertText(from.getInsertText());
		to.setInsertTextFormat(from.getInsertTextFormat() != null
				? InsertTextFormat.valueOf(from.getInsertTextFormat().toString())
				: null);
		to.setTextEdit(toTextEdit(from.getTextEdit()));
		if (from.getAdditionalTextEdits() != null) {
			ArrayList<TextEdit> additional = new ArrayList<>();
			for (org.eclipse.lsp4j.TextEdit textEdit : from.getAdditionalTextEdits()) {
				additional.add(toTextEdit(textEdit));
			}
			to.setAdditionalTextEdits(additional);
		}
		if (from.getCommitCharacters() != null) {
			to.setCommitCharacters(from.getCommitCharacters());
		}
		to.setCommand(toCommand(from.getCommand()));
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link TextDocumentIdentifier} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.TextDocumentIdentifier}.
	 *
	 * @param from the {@code Spring DSL TextDocumentIdentifier}
	 * @return {@code LSP4J TextDocumentIdentifier}
	 */
	public static org.eclipse.lsp4j.TextDocumentIdentifier toTextDocumentIdentifier(TextDocumentIdentifier from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.TextDocumentIdentifier to = new org.eclipse.lsp4j.TextDocumentIdentifier();
		to.setUri(from.getUri());
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.TextDocumentIdentifier} to
	 * {@code Spring DSL} {@link TextDocumentIdentifier}.
	 *
	 * @param from the {@code LSP4J TextDocumentIdentifier}
	 * @return {@code Spring DSL TextDocumentIdentifier}
	 */
	public static TextDocumentIdentifier toTextDocumentIdentifier(org.eclipse.lsp4j.TextDocumentIdentifier from) {
		if (from == null) {
			return null;
		}
		TextDocumentIdentifier to = new TextDocumentIdentifier();
		to.setUri(from.getUri());
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link VersionedTextDocumentIdentifier} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.VersionedTextDocumentIdentifier}.
	 *
	 * @param from the {@code Spring DSL VersionedTextDocumentIdentifier}
	 * @return {@code LSP4J VersionedTextDocumentIdentifier}
	 */
	public static org.eclipse.lsp4j.VersionedTextDocumentIdentifier toVersionedTextDocumentIdentifier(VersionedTextDocumentIdentifier from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.VersionedTextDocumentIdentifier to =  new org.eclipse.lsp4j.VersionedTextDocumentIdentifier();
		to.setUri(from.getUri());
		to.setVersion(from.getVersion());
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.VersionedTextDocumentIdentifier} to
	 * {@code Spring DSL} {@link VersionedTextDocumentIdentifier}.
	 *
	 * @param from the {@code LSP4J VersionedTextDocumentIdentifier}
	 * @return {@code Spring DSL VersionedTextDocumentIdentifier}
	 */
	public static VersionedTextDocumentIdentifier toVersionedTextDocumentIdentifier(org.eclipse.lsp4j.VersionedTextDocumentIdentifier from) {
		if (from == null) {
			return null;
		}
		VersionedTextDocumentIdentifier to = new VersionedTextDocumentIdentifier();
		to.setUri(from.getUri());
		to.setVersion(from.getVersion());
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link MarkedString} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.MarkedString}.
	 *
	 * @param from the {@code Spring DSL MarkedString}
	 * @return {@code LSP4J MarkedString}
	 */
	public static org.eclipse.lsp4j.MarkedString toMarkedString(MarkedString from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.MarkedString to =  new org.eclipse.lsp4j.MarkedString();
		to.setLanguage(from.getLanguage());
		to.setValue(from.getValue());
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.MarkedString} to
	 * {@code Spring DSL} {@link MarkedString}.
	 *
	 * @param from the {@code LSP4J MarkedString}
	 * @return {@code Spring DSL MarkedString}
	 */
	public static MarkedString toMarkedString(org.eclipse.lsp4j.MarkedString from) {
		if (from == null) {
			return null;
		}
		MarkedString to = new MarkedString();
		to.setLanguage(from.getLanguage());
		to.setValue(from.getValue());
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link CompletionContext} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.CompletionContext}.
	 *
	 * @param from the {@code Spring DSL CompletionContext}
	 * @return {@code LSP4J CompletionContext}
	 */
	public static org.eclipse.lsp4j.CompletionContext toCompletionContext(CompletionContext from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.CompletionContext to =  new org.eclipse.lsp4j.CompletionContext();
		to.setTriggerCharacter(from.getTriggerCharacter());
		to.setTriggerKind(from.getTriggerKind() != null
				? org.eclipse.lsp4j.CompletionTriggerKind.valueOf(from.getTriggerKind().toString())
				: null);
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.CompletionContext} to
	 * {@code Spring DSL} {@link CompletionContext}.
	 *
	 * @param from the {@code LSP4J CompletionContext}
	 * @return {@code Spring DSL CompletionContext}
	 */
	public static CompletionContext toCompletionContext(org.eclipse.lsp4j.CompletionContext from) {
		if (from == null) {
			return null;
		}
		CompletionContext to = new CompletionContext();
		to.setTriggerCharacter(from.getTriggerCharacter());
		to.setTriggerKind(
				from.getTriggerKind() != null ? CompletionTriggerKind.valueOf(from.getTriggerKind().toString()) : null);
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link CompletionParams} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.CompletionParams}.
	 *
	 * @param from the {@code Spring DSL CompletionParams}
	 * @return {@code LSP4J CompletionParams}
	 */
	public static org.eclipse.lsp4j.CompletionParams toCompletionParams(CompletionParams from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.CompletionParams to =  new org.eclipse.lsp4j.CompletionParams();
		to.setContext(toCompletionContext(from.getContext()));
		to.setPosition(toPosition(from.getPosition()));
		to.setTextDocument(toTextDocumentIdentifier(from.getTextDocument()));
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.CompletionParams} to
	 * {@code Spring DSL} {@link CompletionParams}.
	 *
	 * @param from the {@code LSP4J CompletionParams}
	 * @return {@code Spring DSL CompletionParams}
	 */
	public static CompletionParams toCompletionParams(org.eclipse.lsp4j.CompletionParams from) {
		if (from == null) {
			return null;
		}
		CompletionParams to = new CompletionParams();
		to.setContext(toCompletionContext(from.getContext()));
		to.setContext(toCompletionContext(from.getContext()));
		to.setPosition(toPosition(from.getPosition()));
		to.setTextDocument(toTextDocumentIdentifier(from.getTextDocument()));
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link TextEdit} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.TextEdit}.
	 *
	 * @param from the {@code Spring DSL TextEdit}
	 * @return {@code LSP4J TextEdit}
	 */
	public static org.eclipse.lsp4j.TextEdit toTextEdit(TextEdit from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.TextEdit to =  new org.eclipse.lsp4j.TextEdit();
		to.setRange(toRange(from.getRange()));
		to.setNewText(from.getNewText());
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.TextEdit} to
	 * {@code Spring DSL} {@link TextEdit}.
	 *
	 * @param from the {@code LSP4J TextEdit}
	 * @return {@code Spring DSL TextEdit}
	 */
	public static TextEdit toTextEdit(org.eclipse.lsp4j.TextEdit from) {
		if (from == null) {
			return null;
		}
		TextEdit to = new TextEdit();
		to.setRange(toRange(from.getRange()));
		to.setNewText(from.getNewText());
		return to;
	}

	/**
	 * Convert {@code Spring DSL} {@link Command} to {@code LSP4J}
	 * {@link org.eclipse.lsp4j.Command}.
	 *
	 * @param from the {@code Spring DSL Command}
	 * @return {@code LSP4J Command}
	 */
	public static org.eclipse.lsp4j.Command toCommand(Command from) {
		if (from == null) {
			return null;
		}
		org.eclipse.lsp4j.Command to =  new org.eclipse.lsp4j.Command();
		to.setTitle(from.getTitle());
		to.setCommand(from.getCommand());
		// TODO: add argument list
		return to;
	}

	/**
	 * Convert {@code LSP4J} {@link org.eclipse.lsp4j.Command} to
	 * {@code Spring DSL} {@link Command}.
	 *
	 * @param from the {@code LSP4J Command}
	 * @return {@code Spring DSL Command}
	 */
	public static Command toCommand(org.eclipse.lsp4j.Command from) {
		if (from == null) {
			return null;
		}
		Command to = new Command();
		to.setTitle(from.getTitle());
		to.setCommand(from.getCommand());
		// TODO: add argument list
		return to;
	}
}
