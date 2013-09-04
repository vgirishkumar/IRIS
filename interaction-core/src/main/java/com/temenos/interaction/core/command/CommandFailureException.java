package com.temenos.interaction.core.command;

import com.temenos.interaction.core.resource.RESTResource;


/**
 * Interaction command failure exception.
 * This internal exception represents a command failure and should
 * not be used by interaction commands.
 */
public class CommandFailureException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private InteractionCommand.Result result;
	private RESTResource resource;

	/**
	 * Construct a new exception
	 * @param result command result
	 * @param resource resource
	 */
	public CommandFailureException(InteractionCommand.Result result, RESTResource resource) {
		this.result = result;
		this.resource = resource;
	}

	/**
	 * Construct a new exception
	 * @param result command result
	 * @param message error message
	 * @param resource resource
	 */
	public CommandFailureException(InteractionCommand.Result result, RESTResource resource, String message) {
		super(message);
		this.result = result;
		this.resource = resource;
	}

	/**
	 * Return the command result
	 * @return result
	 */
	public InteractionCommand.Result getResult() {
		return result;
	}
	
	/**
	 * Return the resource
	 * @return resource
	 */
	public RESTResource getResource() {
		return resource;
	}
}
