package com.temenos.interaction.core.hypermedia.expression;

import javax.ws.rs.core.MultivaluedMap;

public interface Expression {

	/**
	 * Evaluate this expression and return a boolean result.
	 * @param pathParams
	 * @return
	 */
	public boolean evaluate(MultivaluedMap<String, String> pathParams);
}
