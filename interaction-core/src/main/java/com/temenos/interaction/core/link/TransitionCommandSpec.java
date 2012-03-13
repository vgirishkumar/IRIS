package com.temenos.interaction.core.link;

import java.util.List;

/**
 * Define how a transition from one state to another should occur.
 * @author aphethean
 */
public class TransitionCommandSpec {

	private String path;
	private String method;
	@SuppressWarnings("unused")
	private List<String> queryParams;
	
	protected TransitionCommandSpec(String method, String path) {
		this.path = path;
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public String getMethod() {
		return method;
	}

	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof TransitionCommandSpec)) return false;
		TransitionCommandSpec otherObj = (TransitionCommandSpec) other;
		return ((this.getPath() == null && otherObj.getPath() == null) || (this.getPath() != null && this.getPath().equals(otherObj.getPath()))) &&
			this.getMethod().equals(otherObj.getMethod());
	}
	
	public int hashCode() {
		return (this.path != null ? this.path.hashCode() : 0) + this.method.hashCode();
	}
	
	public String toString() {
		return method + ((path != null && path.length() > 0) ? " " + path : "");
	}
}
