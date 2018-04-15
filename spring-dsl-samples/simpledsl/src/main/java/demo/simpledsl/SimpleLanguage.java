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

import demo.simpledsl.SimpleLanguage.KeyToken;

/**
 * {@code simple} language representation containing parser to tokenize a dsl
 * which can be used to give various answers to {@code LSP} requests.
 * <p>
 * This language parser implementation is to showcase a raw ways to provide
 * concepts of ideas how {@code LSP} can be hooked into various services. In
 * a real world, you'd probably be better off with some real language parcer line
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

	public SimpleLanguage(Document document, List<Line> lines) {
		this.document = document;
		this.lines = lines;
	}

	public Document getDocument() {
		return document;
	}

	public List<Line> getLines() {
		return lines;
	}

	public Token getToken(Position position) {
		for (Line line : getLines()) {
			if (line.getLine() == position.getLine()) {
				KeyToken keyToken = line.getKeyToken();
				if (keyToken != null && keyToken.getStart() <= position.getCharacter()
						&& keyToken.getEnd() >= position.getCharacter()) {
					return keyToken;
				}
				ValueToken valueToken = line.getValueToken();
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
		if (split.length == 1) {
			lines.add(new Line(lineIndex, new KeyToken(split[0], 0, split[0].length()), null));
		} else {
			lines.add(new Line(lineIndex, new KeyToken(split[0], 0, split[0].length()),
					new ValueToken(split[1], split[0].length() + 1, line.length())));
		}
	}

	public static class Line {

		private final int line;
		private final KeyToken keyToken;
		private final ValueToken valueToken;

		public Line(int line, KeyToken keyToken, ValueToken valueToken) {
			this.line = line;
			this.keyToken = keyToken;
			this.valueToken = valueToken;
		}

		public int getLine() {
			return line;
		}

		public KeyToken getKeyToken() {
			return keyToken;
		}

		public ValueToken getValueToken() {
			return valueToken;
		}
	}

	public static class Token {

		private final int start;
		private final int end;

		public Token(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
	}

	public static class KeyToken extends Token {

		private final Object key;

		public KeyToken(Object key, int start, int end) {
			super(start, end);
			this.key = key;
		}

		public Object getKey() {
			return key;
		}

	}

	public static class ValueToken extends Token {

		private final Object key;

		public ValueToken(Object key, int start, int end) {
			super(start, end);
			this.key = key;
		}

		public Object getKey() {
			return key;
		}

	}
}
