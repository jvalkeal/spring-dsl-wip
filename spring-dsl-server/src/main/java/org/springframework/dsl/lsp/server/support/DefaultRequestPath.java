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
package org.springframework.dsl.lsp.server.support;

import java.net.URI;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

// TODO: Auto-generated Javadoc
/**
 * Default implementation of {@link RequestPath}.
 *
 * @author Rossen Stoyanchev
 */
class DefaultRequestPath implements RequestPath {

	/** The full path. */
	private final PathContainer fullPath;

	/** The context path. */
	private final PathContainer contextPath;

	/** The path within application. */
	private final PathContainer pathWithinApplication;


	/**
	 * Instantiates a new default request path.
	 *
	 * @param uri the uri
	 * @param contextPath the context path
	 */
	DefaultRequestPath(URI uri, @Nullable String contextPath) {
		this.fullPath = PathContainer.parsePath(uri.getRawPath());
		this.contextPath = initContextPath(this.fullPath, contextPath);
		this.pathWithinApplication = extractPathWithinApplication(this.fullPath, this.contextPath);
	}

	/**
	 * Instantiates a new default request path.
	 *
	 * @param requestPath the request path
	 * @param contextPath the context path
	 */
	private DefaultRequestPath(RequestPath requestPath, String contextPath) {
		this.fullPath = requestPath;
		this.contextPath = initContextPath(this.fullPath, contextPath);
		this.pathWithinApplication = extractPathWithinApplication(this.fullPath, this.contextPath);
	}

	/**
	 * Inits the context path.
	 *
	 * @param path the path
	 * @param contextPath the context path
	 * @return the path container
	 */
	private static PathContainer initContextPath(PathContainer path, @Nullable String contextPath) {
		if (!StringUtils.hasText(contextPath) || "/".equals(contextPath)) {
			return PathContainer.parsePath("");
		}

		validateContextPath(path.value(), contextPath);

		int length = contextPath.length();
		int counter = 0;

		for (int i=0; i < path.elements().size(); i++) {
			PathContainer.Element element = path.elements().get(i);
			counter += element.value().length();
			if (length == counter) {
				return path.subPath(0, i + 1);
			}
		}

		// Should not happen..
		throw new IllegalStateException("Failed to initialize contextPath '" + contextPath + "'" +
				" for requestPath '" + path.value() + "'");
	}

	/**
	 * Validate context path.
	 *
	 * @param fullPath the full path
	 * @param contextPath the context path
	 */
	private static void validateContextPath(String fullPath, String contextPath) {
		int length = contextPath.length();
		if (contextPath.charAt(0) != '/' || contextPath.charAt(length - 1) == '/') {
			throw new IllegalArgumentException("Invalid contextPath: '" + contextPath + "': " +
					"must start with '/' and not end with '/'");
		}
		if (!fullPath.startsWith(contextPath)) {
			throw new IllegalArgumentException("Invalid contextPath '" + contextPath + "': " +
					"must match the start of requestPath: '" + fullPath + "'");
		}
		if (fullPath.length() > length && fullPath.charAt(length) != '/') {
			throw new IllegalArgumentException("Invalid contextPath '" + contextPath + "': " +
					"must match to full path segments for requestPath: '" + fullPath + "'");
		}
	}

	/**
	 * Extract path within application.
	 *
	 * @param fullPath the full path
	 * @param contextPath the context path
	 * @return the path container
	 */
	private static PathContainer extractPathWithinApplication(PathContainer fullPath, PathContainer contextPath) {
		return fullPath.subPath(contextPath.elements().size());
	}


	// PathContainer methods..

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.support.PathContainer#value()
	 */
	@Override
	public String value() {
		return this.fullPath.value();
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.support.PathContainer#elements()
	 */
	@Override
	public List<Element> elements() {
		return this.fullPath.elements();
	}


	// RequestPath methods..

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.support.RequestPath#contextPath()
	 */
	@Override
	public PathContainer contextPath() {
		return this.contextPath;
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.support.RequestPath#pathWithinApplication()
	 */
	@Override
	public PathContainer pathWithinApplication() {
		return this.pathWithinApplication;
	}

	/* (non-Javadoc)
	 * @see org.springframework.dsl.lsp.server.support.RequestPath#modifyContextPath(java.lang.String)
	 */
	@Override
	public RequestPath modifyContextPath(String contextPath) {
		return new DefaultRequestPath(this, contextPath);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		DefaultRequestPath that = (DefaultRequestPath) other;
		return (this.fullPath.equals(that.fullPath) &&
				this.contextPath.equals(that.contextPath) &&
				this.pathWithinApplication.equals(that.pathWithinApplication));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = this.fullPath.hashCode();
		result = 31 * result + this.contextPath.hashCode();
		result = 31 * result + this.pathWithinApplication.hashCode();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultRequestPath[fullPath='" + this.fullPath + "', " +
				"contextPath='" + this.contextPath.value() + "', " +
				"pathWithinApplication='" + this.pathWithinApplication.value() + "']";
	}
}
