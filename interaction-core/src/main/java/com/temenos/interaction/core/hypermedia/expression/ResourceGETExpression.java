package com.temenos.interaction.core.hypermedia.expression;

import javax.ws.rs.core.MultivaluedMap;

public class ResourceGETExpression implements Expression {

	public enum Function {
		OK,
		NOT_FOUND
	}
	
	public final Function function;
	public final String state;
	
	public ResourceGETExpression(String state, Function function) {
		this.function = function;
		this.state = state;
	}
	
	public Function getFunction() {
		return function;
	}

	public String getState() {
		return state;
	}
	
	@Override
	public boolean evaluate(MultivaluedMap<String, String> pathParams) {
		return true;
	}

}
