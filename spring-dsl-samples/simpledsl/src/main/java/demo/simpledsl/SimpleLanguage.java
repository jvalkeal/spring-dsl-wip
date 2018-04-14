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

/**
 * {@code simple} language representation containing parser to tokenize a dsl
 * which can be used to give various answers to {@code LSP} requests.
 *
 * @author Janne Valkealahti
 *
 */
public class SimpleLanguage {

	private final static Pattern REGEX = Pattern.compile("[\\r\\n]+");

	/**
	 * Parse a document and return a tokenized list of lines.
	 *
	 * @param document the document to parse
	 * @return the a tokenized list of lines
	 */
	public static List<Line> parse(Document document) {
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

		return lines;
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

	public static class KeyToken {

		private final Object key;
		private final int start;
		private final int end;

		public KeyToken(Object key, int start, int end) {
			this.key = key;
			this.start = start;
			this.end = end;
		}

		public Object getKey() {
			return key;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
	}

	public static class ValueToken {

		private final Object value;
		private final int start;
		private final int end;

		public ValueToken(Object value, int start, int end) {
			this.value = value;
			this.start = start;
			this.end = end;
		}

		public Object getValue() {
			return value;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
	}
}
