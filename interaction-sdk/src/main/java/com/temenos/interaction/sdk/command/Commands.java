package com.temenos.interaction.sdk.command;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information about IRIS commands
 */
public class Commands {
	public final static String GET_ENTITY = "GETEntity";
	public final static String GET_ENTITIES = "GETEntities";
	public final static String POST_ENTITY = "POSTEntity";
	public final static String GET_LINK_ENTITY = "GETLinkEntity";
	
	private List<Command> commands = new ArrayList<Command>();

	public Commands() {
	}

	public void addCommand(String className, String type, Parameter param1, Parameter param2) {
		String id = "cmd" + type + param1.getValue();
		Command command = new Command(id, className);
		command.addParameter(param1);
		command.addParameter(param2);
		commands.add(command);
	}
	
	public void addCommand(Command command) {
		commands.add(command);
	}

	public List<Command> getCommands() {
		return commands;
	}
}
