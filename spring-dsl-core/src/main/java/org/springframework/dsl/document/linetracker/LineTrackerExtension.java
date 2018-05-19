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
package org.springframework.dsl.document.linetracker;

/**
 * Extension interface for {@code org.eclipse.jface.text.ILineTracker}. Adds the
 * concept of rewrite sessions. A rewrite session is a sequence of replace
 * operations that form a semantic unit.
 *
 */
public interface LineTrackerExtension {

	/**
	 * Tells the line tracker that a rewrite session started. A rewrite session
	 * is a sequence of replace operations that form a semantic unit. The line
	 * tracker is allowed to use that information for internal optimization.
	 *
	 * @param session the rewrite session
	 * @throws IllegalStateException in case there is already an active rewrite
	 *             session
	 */
//	void startRewriteSession(DocumentRewriteSession session) throws IllegalStateException;

	/**
	 * Tells the line tracker that the rewrite session has finished. This method
	 * is only called when <code>startRewriteSession</code> has been called
	 * before. The text resulting from the rewrite session is passed to the line
	 * tracker.
	 *
	 * @param session the rewrite session
	 * @param text the text with which to re-initialize the line tracker
	 */
//	void stopRewriteSession(DocumentRewriteSession session, String text);
}
