package com.temenos.interaction.core.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandController {

	private final Logger logger = LoggerFactory.getLogger(CommandController.class);

	private String resourcePath = null;
	private Map<String, ResourceCommand> commands = new HashMap<String, ResourceCommand>();

	public CommandController(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	public CommandController(String resourcePath, ResourceGetCommand getCommand, Set<ResourceStateTransitionCommand> stateTransitionCommands) {
		this.resourcePath = resourcePath;
		setGetCommand(getCommand);
		for(ResourceStateTransitionCommand stc : stateTransitionCommands) {
			addStateTransitionCommand(stc);
		}
	}
	
	public void setGetCommand(ResourceGetCommand c) {
		commands.put("GET", c);
	}

	/**
	 * Add a command to transition a resources state.
	 * @precondition {@link ResourceStateTransitionCommand} not null
	 */
	public void addStateTransitionCommand(ResourceStateTransitionCommand c) {
		assert(c != null);
		commands.put(c.getMethod() + "+" + c.getPath(), c);
	}

	/*
	 * Return a #ResourceGetCommand for a RESTful GET of the supplied path to a resource.
	 *
	 * @precondition String path is a non null path to a resource
	 * @postcondition ResourceGetCommand class previously registered for GET operations by #addGetCommand
	 * @invariant getCommands is not null
	 */
	public ResourceGetCommand fetchGetCommand() {
		logger.info("Looking up get command for [" + resourcePath + "]");
		ResourceCommand getCommand = commands.get("GET");
		if (getCommand == null) {
			logger.warn("No GET command bound for [" + resourcePath + "]");
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
	public ResourceCommand fetchStateTransitionCommand(String httpMethod, String path) {
		logger.info("Looking up state transition [" + httpMethod + "+" + path + "] command for [" + resourcePath + "]");
		ResourceCommand command = commands.get(httpMethod + "+" + path);
		if (command == null) {
			logger.warn("No command bound to [" + httpMethod + "+" + path + "]");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return command;
	}

}
