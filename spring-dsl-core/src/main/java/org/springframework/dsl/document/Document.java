/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.dsl.document;

import org.springframework.dsl.lsp.domain.Position;

/**
 * Interface for accessing information about a document including its content.
 * <p>
 * A {@code Document} instance is immutable and represents a snapshot of the
 * document state.
 *
 * @author Kris De Volder
 * @author Janne Valkealahti
 *
 */
public interface Document {

	// TODO: Cleanup this interface and get rid of
	// methods using IRegion
	// methods using IRegion-like access based on offset + length
	// (should only use methods based on start - end type character
	// ranges)

	String uri();

	String content();

	/**
	 * Gets an absolute position in a content representing a {@link Position}. As
	 * {@link Position} work on a character position in a particular line, this
	 * method gives a {@code caret} position in a content string. Useful for cases
	 * where user of a document content parses content as a raw string and cares
	 * only about position in a raw content without worrying about line delimiters.
	 *
	 * @param position the position
	 * @return the caret position
	 */
	int caret(Position position);

//	Region getLineInformationOfOffset(int offset);

	int length();

	String content(int start, int len) throws BadLocationException;

	int getNumberOfLines();

	String getDefaultLineDelimiter();

	char getChar(int offset) throws BadLocationException;

	int getLineOfOffset(int offset) throws BadLocationException;

//	Region getLineInformation(int line);

	int getLineOffset(int line) throws BadLocationException;

	void replace(int start, int len, String text) throws BadLocationException;

	String textBetween(int start, int end) throws BadLocationException;

	LanguageId getLanguageId();

	int getVersion();

//	Range toRange(Region asRegion) throws BadLocationException;

	Position toPosition(int offset) throws BadLocationException;

}
