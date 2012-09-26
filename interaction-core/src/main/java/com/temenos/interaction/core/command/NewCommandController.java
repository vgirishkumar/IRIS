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
	 * Create a command controller and add the supplied commands to the resource path
	 * @precondition commands not null
	 * @postcondition all supplied commands can be retrieved with {@link fetchCommand}
	 * @param resourcePath
	 * @param Map<String, InteractionCommand> commands
	 */
	public NewCommandController(Map<String, InteractionCommand> commands) {
		assert(commands != null);
		for(String name : commands.keySet()) {
			addCommand(name, commands.get(name));
		}
	}

	/**
	 * Add a command to transition a resources state.
	 * @precondition name not null
	 * @precondition {@link InteractionCommand} not null
	 */
	public void addCommand(String name, InteractionCommand c) {
		assert(name != null);
		assert(c != null);
		commands.put(name, c);
	}

	/*
	 * Returns the {@link InteractionCommand} bound to this name.
	 *
	 * @precondition String name is non null
	 * @postcondition InteractionCommand class previously registered by #addCommand
	 * @invariant commands is not null and number of commands
	 */
	public InteractionCommand fetchCommand(String name) {
		logger.debug("Looking up interaction command by name [" + name + "]");
		InteractionCommand command = commands.get(name);
		if (command == null) {
			logger.error("No command bound to [" + name + "]");
		}
		return command;
	}

	public boolean isValidCommand(String name) {
		return (commands.get(name) != null);
	}
}
