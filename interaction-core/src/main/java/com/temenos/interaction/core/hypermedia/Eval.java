package com.temenos.interaction.core.hypermedia;

import javax.ws.rs.core.MultivaluedMap;

public class Eval {

	public enum Function {
		OK,
		NOT_FOUND
	}
	
	public final Function function;
	public final String state;
	
	public Eval(String state, Function function) {
		this.function = function;
		this.state = state;
	}
	
	public Function getFunction() {
		return function;
	}

	public String getState() {
		return state;
	}
	
	public boolean evaluate(MultivaluedMap<String, String> pathParams) {
		return true;
	}
}
