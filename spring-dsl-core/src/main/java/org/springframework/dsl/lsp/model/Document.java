package org.springframework.dsl.lsp.model;

import org.springframework.dsl.lsp.domain.Range;

public interface Document {

	String getUri();

	String get();

	Region getLineInformationOfOffset(int offset);

	int getLength();

	String get(int start, int len) throws BadLocationException;

	int getNumberOfLines();

	String getDefaultLineDelimiter();

	char getChar(int offset) throws BadLocationException;

	int getLineOfOffset(int offset) throws BadLocationException;

	Region getLineInformation(int line);

	int getLineOffset(int line) throws BadLocationException;

	void replace(int start, int len, String text) throws BadLocationException;

	String textBetween(int start, int end) throws BadLocationException;

	LanguageId getLanguageId();

	int getVersion();

	Range toRange(Region asRegion) throws BadLocationException;

}
