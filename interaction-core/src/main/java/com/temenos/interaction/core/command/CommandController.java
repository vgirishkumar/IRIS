package com.temenos.interaction.core.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.state.ResourceInteractionModel;

public class CommandController {

	private final Logger logger = LoggerFactory.getLogger(CommandController.class);

	private Map<String, ResourceCommand> commands = new HashMap<String, ResourceCommand>();

	/**
	 * Create an empty command controller.
	 */
	public CommandController() {
	}
	
	/**
	 * Create a command controller and add the supplied commands to the resource path from {@link ResourceInteractionModel.getResourcePath()}
	 * @param rim
	 * @param getCommand
	 * @param stateTransitionCommands
	 */
	public CommandController(ResourceInteractionModel rim, ResourceGetCommand getCommand, Set<ResourceStateTransitionCommand> stateTransitionCommands) {
		this(rim.getResourcePath(), getCommand, stateTransitionCommands);
	}
	
	/**
	 * Create a command controller and add the supplied commands to the resource path
	 * @param rim
	 * @param getCommand
	 * @param stateTransitionCommands
	 */
	public CommandController(String resourcePath, ResourceGetCommand getCommand, Set<ResourceStateTransitionCommand> stateTransitionCommands) {
		setGetCommand(resourcePath, getCommand);
		if (stateTransitionCommands != null) {
			for(ResourceStateTransitionCommand stc : stateTransitionCommands) {
				addStateTransitionCommand(resourcePath, stc);
			}
		}
	}
	
	public void setGetCommand(String resourcePath, ResourceGetCommand c) {
		commands.put("GET+" + resourcePath, c);
	}

	/**
	 * Add a command to transition a resources state.
	 * @precondition {@link ResourceStateTransitionCommand} not null
	 */
	public void addStateTransitionCommand(String resourcePath, ResourceStateTransitionCommand c) {
		assert(c != null);
		commands.put(c.getMethod() + "+" + resourcePath, c);
	}

	/*
	 * Return a #ResourceGetCommand for a RESTful GET of the supplied path to a resource.
	 *
	 * @precondition String path is a non null path to a resource
	 * @postcondition ResourceGetCommand class previously registered for GET operations by #addGetCommand
	 * @invariant getCommands is not null
	 */
	public ResourceGetCommand fetchGetCommand(String resourcePath) {
		logger.info("Looking up get command for [" + resourcePath + "]");
		ResourceCommand getCommand = commands.get("GET+" + resourcePath);
		if (getCommand == null) {
			logger.warn("No command bound for [" + "GET+" + resourcePath + "]");
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
		logger.info("Looking up state transition command for [" + httpMethod + "+" + resourcePath + "]");
		ResourceCommand command = commands.get(httpMethod + "+" + resourcePath);
		if (command == null) {
			logger.error("No command bound to [" + httpMethod + "+" + resourcePath + "]");
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return command;
	}

}
