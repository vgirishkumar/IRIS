package com.temenos.interaction.core.hypermedia;

import java.util.Properties;

public class Action {

	public enum TYPE {
		VIEW,
		ENTRY
	}
	
	private final String name;
	private final TYPE type;
	private Properties properties;
	
	public Action(String name, TYPE type) {
		this.name = name;
		this.type = type;
	}

	public Action(String name, TYPE type, Properties props) {
		this.name = name;
		this.type = type;
		this.properties = props;
	}

	public String getName() {
		return name;
	}
	
	public TYPE getType() {
		return type;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public String toString() {
		return "Action(name=\"" + name + "\", type=\"" + type + "\")";
	}
}
