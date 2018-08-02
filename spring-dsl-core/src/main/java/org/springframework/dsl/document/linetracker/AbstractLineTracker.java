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

import java.util.List;

import org.springframework.dsl.document.BadLocationException;

/**
 * Abstract implementation of <code>ILineTracker</code>. It lets the definition of line
 * delimiters to subclasses. Assuming that '\n' is the only line delimiter, this abstract
 * implementation defines the following line scheme:
 * <ul>
 * <li> "" - [0,0]
 * <li> "a" - [0,1]
 * <li> "\n" - [0,1], [1,0]
 * <li> "a\n" - [0,2], [2,0]
 * <li> "a\nb" - [0,2], [2,1]
 * <li> "a\nbc\n" - [0,2], [2,3], [5,0]
 * </ul>
 * <p>
 * This class must be subclassed.
 * </p>
 */
public abstract class AbstractLineTracker implements LineTracker/*, LineTrackerExtension*/ {

	/**
	 * Tells whether this class is in debug mode.
	 *
	 * @since 3.1
	 */
	private static final boolean DEBUG= false;

	/**
	 * Combines the information of the occurrence of a line delimiter. <code>delimiterIndex</code>
	 * is the index where a line delimiter starts, whereas <code>delimiterLength</code>,
	 * indicates the length of the delimiter.
	 */
	protected static class DelimiterInfo {

		/** The delimiter index. */
		public int delimiterIndex;

		/** The delimiter length. */
		public int delimiterLength;

		/** The delimiter. */
		public String delimiter;
	}

	/**
	 * Representation of replace and set requests.
	 *
	 * @since 3.1
	 */
	protected static class Request {

		/** The offset. */
		public final int offset;

		/** The length. */
		public final int length;

		/** The text. */
		public final String text;

		/**
		 * Instantiates a new request.
		 *
		 * @param offset the offset
		 * @param length the length
		 * @param text the text
		 */
		public Request(int offset, int length, String text) {
			this.offset= offset;
			this.length= length;
			this.text= text;
		}

		/**
		 * Instantiates a new request.
		 *
		 * @param text the text
		 */
		public Request(String text) {
			this.offset= -1;
			this.length= -1;
			this.text= text;
		}

		/**
		 * Checks if is replace request.
		 *
		 * @return true, if is replace request
		 */
		public boolean isReplaceRequest() {
			return this.offset > -1 && this.length > -1;
		}
	}

	/**
	 * The active rewrite session.
	 *
	 * @since 3.1
	 */
//	private DocumentRewriteSession fActiveRewriteSession;

	/**
	 * The list of pending requests.
	 *
	 * @since 3.1
	 */
	private List<Request> fPendingRequests;
	/**
	 * The implementation that this tracker delegates to.
	 *
	 * @since 3.2
	 */
	private LineTracker fDelegate= new ListLineTracker() {
		@Override
		public String[] getLegalLineDelimiters() {
			return AbstractLineTracker.this.getLegalLineDelimiters();
		}

		@Override
		protected DelimiterInfo nextDelimiterInfo(String text, int offset) {
			return AbstractLineTracker.this.nextDelimiterInfo(text, offset);
		}
	};
	/**
	 * Whether the delegate needs conversion when the line structure is modified.
	 */
	private boolean fNeedsConversion= true;

	/**
	 * Creates a new line tracker.
	 */
	protected AbstractLineTracker() {
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#computeNumberOfLines(java.lang.String)
	 */
	@Override
	public int computeNumberOfLines(String text) {
		return fDelegate.computeNumberOfLines(text);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#getLineDelimiter(int)
	 */
	@Override
	public String getLineDelimiter(int line) throws BadLocationException {
//		checkRewriteSession();
		return fDelegate.getLineDelimiter(line);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#getLineInformation(int)
	 */
	@Override
	public Region getLineInformation(int line) {
//		checkRewriteSession();
		return fDelegate.getLineInformation(line);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#getLineInformationOfOffset(int)
	 */
	@Override
	public Region getLineInformationOfOffset(int offset) throws BadLocationException {
//		checkRewriteSession();
		return fDelegate.getLineInformationOfOffset(offset);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#getLineLength(int)
	 */
	@Override
	public int getLineLength(int line) throws BadLocationException {
//		checkRewriteSession();
		return fDelegate.getLineLength(line);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#getLineNumberOfOffset(int)
	 */
	@Override
	public int getLineNumberOfOffset(int offset) throws BadLocationException {
//		checkRewriteSession();
		return fDelegate.getLineNumberOfOffset(offset);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#getLineOffset(int)
	 */
	@Override
	public int getLineOffset(int line) throws BadLocationException {
//		checkRewriteSession();
		return fDelegate.getLineOffset(line);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#getNumberOfLines()
	 */
	@Override
	public int getNumberOfLines() {
//		try {
//			checkRewriteSession();
//		} catch (BadLocationException x) {
//			// TODO there is currently no way to communicate that exception back to the document
//		}
		return fDelegate.getNumberOfLines();
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#getNumberOfLines(int, int)
	 */
	@Override
	public int getNumberOfLines(int offset, int length) throws BadLocationException {
//		checkRewriteSession();
		return fDelegate.getNumberOfLines(offset, length);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#set(java.lang.String)
	 */
	@Override
	public void set(String text) {
//		if (hasActiveRewriteSession()) {
//			fPendingRequests.clear();
//			fPendingRequests.add(new Request(text));
//			return;
//		}

		fDelegate.set(text);
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTracker#replace(int, int, java.lang.String)
	 */
	@Override
	public void replace(int offset, int length, String text) throws BadLocationException {
//		if (hasActiveRewriteSession()) {
//			fPendingRequests.add(new Request(offset, length, text));
//			return;
//		}

		checkImplementation();

		fDelegate.replace(offset, length, text);
	}

	/**
	 * Converts the implementation to be a {@link TreeLineTracker} if it isn't yet.
	 *
	 * @since 3.2
	 */
	private void checkImplementation() {
		if (fNeedsConversion) {
			fNeedsConversion= false;
			fDelegate= new TreeLineTracker((ListLineTracker) fDelegate) {
				@Override
				protected DelimiterInfo nextDelimiterInfo(String text, int offset) {
					return AbstractLineTracker.this.nextDelimiterInfo(text, offset);
				}

				@Override
				public String[] getLegalLineDelimiters() {
					return AbstractLineTracker.this.getLegalLineDelimiters();
				}
			};
		}
	}

	/**
	 * Returns the information about the first delimiter found in the given text starting at the
	 * given offset.
	 *
	 * @param text the text to be searched
	 * @param offset the offset in the given text
	 * @return the information of the first found delimiter or <code>null</code>
	 */
	protected abstract DelimiterInfo nextDelimiterInfo(String text, int offset);

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTrackerExtension#startRewriteSession(org.springframework.dsl.document.linetracker.DocumentRewriteSession)
	 */
//	@Override
//	public final void startRewriteSession(DocumentRewriteSession session) {
//		if (fActiveRewriteSession != null)
//			throw new IllegalStateException();
//		fActiveRewriteSession= session;
//		fPendingRequests= new ArrayList<>(20);
//	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.document.linetracker.LineTrackerExtension#stopRewriteSession(org.springframework.dsl.document.linetracker.DocumentRewriteSession, java.lang.String)
	 */
//	@Override
//	public final void stopRewriteSession(DocumentRewriteSession session, String text) {
//		if (fActiveRewriteSession == session) {
//			fActiveRewriteSession= null;
//			fPendingRequests= null;
//			set(text);
//		}
//	}

	/**
	 * Tells whether there's an active rewrite session.
	 *
	 * @return <code>true</code> if there is an active rewrite session, <code>false</code>
	 *         otherwise
	 * @since 3.1
	 */
//	protected final boolean hasActiveRewriteSession() {
//		return fActiveRewriteSession != null;
//	}

	/**
	 * Flushes the active rewrite session.
	 *
	 * @throws BadLocationException in case the recorded requests cannot be processed correctly
	 * @since 3.1
	 */
//	protected final void flushRewriteSession() throws BadLocationException {
//		if (DEBUG)
//			System.out.println("AbstractLineTracker: Flushing rewrite session: " + fActiveRewriteSession); //$NON-NLS-1$
//
//		Iterator<Request> e= fPendingRequests.iterator();
//
//		fPendingRequests= null;
//		fActiveRewriteSession= null;
//
//		while (e.hasNext()) {
//			Request request= e.next();
//			if (request.isReplaceRequest())
//				replace(request.offset, request.length, request.text);
//			else
//				set(request.text);
//		}
//	}

	/**
	 * Checks the presence of a rewrite session and flushes it.
	 *
	 * @throws BadLocationException in case flushing does not succeed
	 * @since 3.1
	 */
//	protected final void checkRewriteSession() throws BadLocationException {
//		if (hasActiveRewriteSession())
//			flushRewriteSession();
//	}
}
