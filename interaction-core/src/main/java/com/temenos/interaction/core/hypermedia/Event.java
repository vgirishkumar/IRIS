package com.temenos.interaction.core.hypermedia;

import javax.ws.rs.HttpMethod;

/**
 * An Event has a name and it mapped to an http method that can be used to apply
 * the event to a {@link ResourceState}
 * @author aphethean
 */
public class Event {

	private final String name;
	private final String method;
	
	public Event(String name, String method) {
		this.name = name;
		this.method = method;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMethod() {
		return method;
	}

	public String toString() {
		return "Event(name=\"" + name + "\", method=\"" + method + "\")";
	}

	public boolean isSafe() {
		return getMethod().equals(HttpMethod.GET) || getMethod().equals(HttpMethod.OPTIONS) || getMethod().equals(HttpMethod.HEAD);
	}

	public boolean isUnSafe() {
		return !isSafe();
	}

}
