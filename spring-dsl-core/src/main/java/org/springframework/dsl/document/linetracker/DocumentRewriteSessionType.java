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
 * A document rewrite session type.
 * <p>
 * Allowed values are:
 * <ul>
 * 	<li>{@code DocumentRewriteSessionType#UNRESTRICTED}</li>
 * 	<li>{@code DocumentRewriteSessionType#UNRESTRICTED_SMALL} (since 3.3)</li>
 * 	<li>{@code DocumentRewriteSessionType#SEQUENTIAL}</li>
 * 	<li>{@code DocumentRewriteSessionType#STRICTLY_SEQUENTIAL}</li>
 * </ul>
 *
 */
public class DocumentRewriteSessionType {

	/**
	 * An unrestricted rewrite session is a sequence of unrestricted replace operations. This
	 * session type should only be used for <em>large</em> operations that touch more than about
	 * fifty lines. Use {@link #UNRESTRICTED_SMALL} for small operations.
	 */
	public final static DocumentRewriteSessionType UNRESTRICTED= new DocumentRewriteSessionType();
	/**
	 * An small unrestricted rewrite session is a short sequence of unrestricted replace operations.
	 * This should be used for changes that touch less than about fifty lines.
	 *
	 * @since 3.3
	 */
	public final static DocumentRewriteSessionType UNRESTRICTED_SMALL= new DocumentRewriteSessionType();
	/**
	 * A sequential rewrite session is a sequence of non-overlapping replace
	 * operations starting at an arbitrary document offset.
	 */
	public final static DocumentRewriteSessionType SEQUENTIAL= new DocumentRewriteSessionType();
	/**
	 * A strictly sequential rewrite session is a sequence of non-overlapping
	 * replace operations from the start of the document to its end.
	 */
	public final static DocumentRewriteSessionType STRICTLY_SEQUENTIAL= new DocumentRewriteSessionType();


	/**
	 * Prohibit external object creation.
	 */
	private DocumentRewriteSessionType() {
	}
}
