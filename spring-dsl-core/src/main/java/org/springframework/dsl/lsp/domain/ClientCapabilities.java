package org.springframework.dsl.lsp.domain;

public class ClientCapabilities {

	private Object experimental;

	public Object getExperimental() {
		return experimental;
	}

	public void setExperimental(Object experimental) {
		this.experimental = experimental;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((experimental == null) ? 0 : experimental.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientCapabilities other = (ClientCapabilities) obj;
		if (experimental == null) {
			if (other.experimental != null)
				return false;
		} else if (!experimental.equals(other.experimental))
			return false;
		return true;
	}

}
