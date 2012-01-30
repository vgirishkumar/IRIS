package com.temenos.interaction.core.link;

import java.util.List;

/**
 * 
 * @author aphethean
 */
public class CommandSpec {

	private String name;
	private String method;
	private List<String> queryParams;
	
	public CommandSpec(String name, String method) {
		this.name = name;
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public String getMethod() {
		return method;
	}

	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof CommandSpec)) return false;
		CommandSpec otherObj = (CommandSpec) other;
		return this.getName().equals(otherObj.getName()) &&
			this.getMethod().equals(otherObj.getMethod());
	}
	
	public int hashCode() {
		return this.name.hashCode() + this.method.hashCode();
	}
}
