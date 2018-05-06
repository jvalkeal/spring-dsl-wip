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
package org.springframework.dsl.document;

import org.springframework.util.Assert;

public class DocumentUtil {

	//TODO: this stuff belongs in IDocument and its implementation, not here. This class should be removed.

	/**
	 * Fetch text between two offsets. Doesn't throw BadLocationException.
	 * If either one or both of the offsets points outside the
	 * document then they will be adjusted to point the appropriate boundary to
	 * retrieve the text just upto the end or beginning of the document instead.
	 *
	 * @param doc the doc
	 * @param start the start
	 * @param end the end
	 * @return the string
	 */
	public static String textBetween(Document doc, int start, int end) {
		Assert.isTrue(start<=end);
		if (start>=doc.getLength()) {
			return "";
		}
		if (start<0) {
			start = 0;
		}
		if (end>doc.getLength()) {
			end = doc.getLength();
		}
		if (end<start) {
			end = start;
		}
		try {
			return doc.get(start, end-start);
		} catch (BadLocationException e) {
			//unless the code above is wrong... this is supposed to be impossible!
			throw new IllegalStateException("Bug!", e);
		}
	}

}
