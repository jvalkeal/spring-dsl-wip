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

import org.springframework.dsl.domain.Position;
import org.springframework.dsl.model.LanguageId;

/**
 * Interface for accessing information about a document including its content.
 * <p>
 * A {@code Document} instance is immutable and represents a snapshot of the
 * document state.
 * <p>
 * {@code Document} works using both position concepts as {@link Range} and
 * {@link Position} from {@code LSP} and raw {@code start} and {@code offset}
 * for supporting editors using those concepts. Difference is that {@code LSP}
 * don't care about line delimiters while some other editors care about position
 * having line delimiters included in a position information. Depending on a
 * system, line delimiter can be either one or two characters.
 *
 * @author Kris De Volder
 * @author Janne Valkealahti
 *
 */
public interface Document {

	/**
	 * Gets the {@code uri} of a document.
	 *
	 * @return the uri
	 */
	String uri();

	/**
	 * Gets the version of a document.
	 *
	 * @return the version
	 */
	int getVersion();

	/**
	 * Gets the full content of a document.
	 *
	 * @return the content
	 */
	String content();

	/**
	 * Gets a length of a document.
	 *
	 * @return the length
	 */
	int length();

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

	/**
	 * Gets a number of lines in a document.
	 *
	 * @return the line count
	 */
	int lineCount();

	/**
	 * Gets a language id of a document.
	 *
	 * @return the language id
	 */
	LanguageId languageId();

	/**
	 * Get a character at an offset position.
	 *
	 * @param offset the offset
	 * @return the char
	 */
	char charAt(int offset);

	String content(int start, int len);

	Position toPosition(int offset);

//	Region getLineInformationOfOffset(int offset);
//	String getDefaultLineDelimiter();
//	int getLineOfOffset(int offset);
//	Region getLineInformation(int line);
//	int getLineOffset(int line);
//	void replace(int start, int len, String text);
//	String textBetween(int start, int end);
//	Range toRange(Region asRegion) throws BadLocationException;


	// TODO: Cleanup this interface and get rid of
	// methods using IRegion
	// methods using IRegion-like access based on offset + length
	// (should only use methods based on start - end type character
	// ranges)

}
