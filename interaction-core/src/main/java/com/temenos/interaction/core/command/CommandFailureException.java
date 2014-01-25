package com.temenos.interaction.core.command;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
