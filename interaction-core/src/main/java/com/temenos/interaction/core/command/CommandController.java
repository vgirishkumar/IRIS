package com.temenos.interaction.core.command;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandController {

	private final Logger logger = LoggerFactory.getLogger(CommandController.class);

	private Map<String, ResourceCommand> commands = new HashMap<String, ResourceCommand>();

	public void addGetCommand(String resourcePath, ResourceGetCommand c) {
		commands.put("GET+" + resourcePath, c);
	}

	public void addStateTransitionCommand(String httpMethod, String resourcePath, ResourceStateTransitionCommand c) {
		commands.put(httpMethod + "+" + resourcePath, c);
	}

	/*
	 * Return a #ResourceGetCommand for a RESTful GET of the supplied path to a resource.
	 *
	 * @precondition String path is a non null path to a resource
	 * @postcondition ResourceGetCommand class previously registered for GET operations by #addGetCommand
	 * @invariant getCommands is not null
	 */
	public ResourceGetCommand fetchGetCommand(String resourcePath) {
		resourcePath = "GET+" + resourcePath;
		logger.info("Looking up command using [" + resourcePath + "]");
		ResourceCommand getCommand = commands.get(resourcePath);
		if (getCommand == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return (ResourceGetCommand) getCommand;
	}

	/*
	 * Return a #ResourceStateTransitionCommand for a REST resource.
	 *
	 * @precondition String httpMethod is a non null path to a resource
	 * @precondition String path is a non null path to a resource
	 * @postcondition ResourceGetCommand class previously registered for GET operations by #addGetCommand
	 * @invariant getCommands is not null
	 */
	public ResourceCommand fetchStateTransitionCommand(String httpMethod, String resourcePath) {
		resourcePath = httpMethod + "+" + resourcePath;
		logger.info("Looking up command using [" + resourcePath + "]");
		ResourceCommand command = commands.get(resourcePath);
		if (command == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return command;
	}

}
