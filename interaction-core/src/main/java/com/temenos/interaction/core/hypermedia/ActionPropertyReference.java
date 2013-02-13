package com.temenos.interaction.core.hypermedia;

import java.util.HashMap;
import java.util.Map;

public class ActionPropertyReference {

	private final String key;
	private Map<String, String> properties = new HashMap<String, String>();
	
	public ActionPropertyReference(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}
	
	public void addProperty(String name, String value) {
		properties.put(name, value);
	}
}
