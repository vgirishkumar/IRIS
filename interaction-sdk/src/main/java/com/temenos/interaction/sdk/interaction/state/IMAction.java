package com.temenos.interaction.sdk.interaction.state;

import java.util.Set;

public interface IMAction {

	/**
	 * Indicates whether a state has link relations
	 * @return true or false
	 */
	public boolean hasRelations();

	/**
	 * Returns the link relations
	 * @return link relations
	 */
	public String getRelations();
	
	/**
	 * Indicates if a state has actions
	 * @return true or false
	 */
	public boolean hasActions();
	
	/**
	 * Returns the actions
	 * @return actions
	 */
	public Set<String> getActions();	
}
