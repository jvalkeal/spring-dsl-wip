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
package demo.simpledsl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dsl.document.Document;
import org.springframework.dsl.lsp.domain.Position;

/**
 * {@code simple} language representation containing parser to tokenize a dsl
 * which can be used to give various answers to {@code LSP} requests.
 * <p>
 * This language parser implementation is to showcase a raw ways to provide
 * concepts of ideas how {@code LSP} can be hooked into various services. In
 * a real world, you'd probably be better off with some real language parser line
 * {@code ANTRL} or similar. But having said that, this gives ideas how things
 * work together without introducing additional logic using external libraries.
 *
 * @author Janne Valkealahti
 *
 */
public class SimpleLanguage {

	private final static Pattern REGEX = Pattern.compile("[\\r\\n]+");
	private final Document document;
	private final List<Line> lines;

	/**
	 * Instantiates a new simple language.
	 *
	 * @param document the document
	 * @param lines the lines
	 */
	public SimpleLanguage(Document document, List<Line> lines) {
		this.document = document;
		this.lines = lines;
	}

	/**
	 * Gets the document known to this language.
	 *
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Gets the {@link Line}s known to this language.
	 *
	 * @return the lines
	 */
	public List<Line> getLines() {
		return lines;
	}

	/**
	 * Gets the token for this language for this particular {@link Position}. If
	 * there are no resolvable token in a specific position {@code NULL} is
	 * returned.
	 *
	 * @param position the position
	 * @return the token
	 */
	public Token getToken(Position position) {
		for (Line line : getLines()) {
			if (line.getLine() == position.getLine()) {
				Token keyToken = line.getKeyToken();
				if (keyToken != null && keyToken.getStart() <= position.getCharacter()
						&& keyToken.getEnd() >= position.getCharacter()) {
					return keyToken;
				}
				Token valueToken = line.getValueToken();
				if (valueToken != null && valueToken.getStart() <= position.getCharacter()
						&& valueToken.getEnd() >= position.getCharacter()) {
					return keyToken;
				}
			}
		}
		return null;
	}

	/**
	 * Parse a document and return a tokenized list of lines.
	 *
	 * @param document the document to parse
	 * @return the simple language structure
	 */
	public static SimpleLanguage build(Document document) {
		ArrayList<Line> lines = new ArrayList<>();

		String content = document.get();
		Matcher matcher = REGEX.matcher(content);

		int position = 0;
		int lineIndex = 0;
		boolean found = false;

		while (matcher.find()) {
			found = true;
			String line = content.substring(position, matcher.start());
			processLine(lines, line, lineIndex);
			position = matcher.end();
			lineIndex++;
		}

		if (!found) {
			processLine(lines, content, lineIndex);
		} else {
			processLine(lines, content.substring(position, content.length()), lineIndex);
		}

		return new SimpleLanguage(document, lines);
	}

	private static void processLine(ArrayList<Line> lines, String line, int lineIndex) {
		String[] split = line.split("=");
		// content before '=' with surrounding white spaces stripped is a key,
		// same for value after '='.
		if (split.length == 1) {
			lines.add(new Line(lineIndex, new KeyToken(split[0], 0, split[0].length(), resolveType(split[0])), null));
		} else {
			lines.add(new Line(lineIndex, new KeyToken(split[0], 0, split[0].length(), resolveType(split[0])),
					new ValueToken(split[1], split[0].length() + 1, line.length())));
		}
	}

	private static TokenType resolveType(String content) {
		content = content.trim();
		TokenType tokenType = TokenType.valueOf(content.toUpperCase());
		return tokenType;
	}

	/**
	 * Represents a {@code line} in this document having a line index, and
	 * optionally existing {@link KeyToken} and {@link ValueToken}.
	 */
	public static class Line {

		private final int line;
		private final Token keyToken;
		private final Token valueToken;

		public Line(int line, Token keyToken, Token valueToken) {
			this.line = line;
			this.keyToken = keyToken;
			this.valueToken = valueToken;
		}

		public int getLine() {
			return line;
		}

		public Token getKeyToken() {
			return keyToken;
		}

		public Token getValueToken() {
			return valueToken;
		}
	}

	/**
	 * Token type shared with both keys and values.
	 */
	public enum TokenType {
		INT,
		LONG,
		DOUBLE,
		STRING,
		VALUE;
	}

	/**
	 * {@code Token} representing a {@code start} and {@code end} position in a
	 * hosting {@link Line} with its hosting {@link TokenType} and actual value.
	 */
	public static class Token {

		private final int start;
		private final int end;
		private final TokenType type;
		private final String value;

		public Token(String value, int start, int end, TokenType type) {
			this.value = value;
			this.start = start;
			this.end = end;
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		public TokenType getType() {
			return type;
		}

		public boolean isKey() {
			return !(type == TokenType.VALUE);
		}
	}

	private static class KeyToken extends Token {

		public KeyToken(String key, int start, int end, TokenType type) {
			super(key, start, end, type);
		}
	}

	private static class ValueToken extends Token {

		public ValueToken(String key, int start, int end) {
			super(key, start, end, TokenType.VALUE);
		}
	}
}
