package com.temenos.interaction.core.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.rim.ResourceInteractionModel;

public class NewCommandController {
	private final Logger logger = LoggerFactory.getLogger(NewCommandController.class);

	private Map<String, InteractionCommand> commands = new HashMap<String, InteractionCommand>();

	/**
	 * Create an empty command controller.
	 */
	public NewCommandController() {
	}

	/**
	 * Create and populate command controller.
	 */
	public NewCommandController(Map<String, InteractionCommand> commands) {
		this.commands.putAll(commands);
	}

	/**
	 * Create a command controller and add the supplied commands to the resource path from {@link ResourceInteractionModel.getResourcePath()}
	 * @param rim
	 * @param Set<InteractionCommand> commands
	 */
	public NewCommandController(ResourceInteractionModel rim, Set<InteractionCommand> commands) {
		this(rim.getFQResourcePath(), commands);
	}
	
	/**
	 * Create a command controller and add the supplied commands to the resource path
	 * @param resourcePath
	 * @param Set<InteractionCommand> commands
	 */
	public NewCommandController(String resourcePath, Set<InteractionCommand> commands) {
		if (commands != null) {
			for(InteractionCommand ic : commands) {
				addCommand(resourcePath, ic);
			}
		}
	}
	

	/**
	 * Add a command to transition a resources state.
	 * @precondition {@link InteractionCommand} not null
	 */
	public void addCommand(String resourcePath, InteractionCommand c) {
		assert(c != null);
		commands.put(c.getMethod() + "+" + resourcePath, c);
	}

	/*
	 * Returns the {@link InteractionCommand} bound to this method and resource path
	 *
	 * @precondition String httpMethod is non null
	 * @precondition String path is a non null path to a resource
	 * @postcondition InteractionCommand class previously registered by #addCommand
	 * @invariant commands is not null and number of commands
	 */
	public InteractionCommand fetchCommand(String method, String resourcePath) {
		logger.info("Looking up interaction command for [" + method + "+" + resourcePath + "]");
		InteractionCommand command = commands.get(method + "+" + resourcePath);
		if (command == null) {
			logger.error("No command bound to [" + method + "+" + resourcePath + "]");
		}
		return command;
	}

	public boolean isValidCommand(String httpMethod, String resourcePath) {
		return (commands.get(httpMethod + "+" + resourcePath) != null);
	}
}
