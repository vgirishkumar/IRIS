package com.temenos.interaction.sdk.command;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information about IRIS commands
 */
public class Commands {
	public final static String GET_SERVICE_DOCUMENT = "GETServiceDocument";
	public final static String GET_METADATA = "GETMetadata";
	public final static String GET_ENTITY = "GETEntity";
	public final static String GET_ENTITIES = "GETEntities";
	public final static String GET_NAV_PROPERTY = "GETNavProperty";
	public final static String CREATE_ENTITY = "CreateEntity";
	public final static String UPDATE_ENTITY = "UpdateEntity";
	public final static String DELETE_ENTITY = "DeleteEntity";
	public final static String GET_LINK_ENTITY = "GETLinkEntity";
	
	
	private List<Command> commands = new ArrayList<Command>();

	public Commands() {
	}

	public void addCommand(String className, String type, Parameter param1) {
		String id = "cmd" + type + param1.getValue();
		Command command = new Command(id, className);
		command.addParameter(param1);
		commands.add(command);
	}
	
	public void addCommand(String id, String className, String type, Parameter param1) {
		Command command = new Command(id, className);
		command.addParameter(param1);
		commands.add(command);
	}
	
	public void addCommand(String className, String type, Parameter param1, Parameter param2) {
		String id = "cmd" + type + param1.getValue();
		addCommand(id, className, type, param1, param2);
	}

	public void addCommand(String id, String className, String type, Parameter param1, Parameter param2) {
		Command command = new Command(id, className);
		command.addParameter(param1);
		command.addParameter(param2);
		commands.add(command);
	}
	
	public void addCommand(String id, String className, String type, List<Parameter> params) {
		Command command = new Command(id, className);
		for(Parameter param : params) {
			command.addParameter(param);
		}
		commands.add(command);
	}
	
	public void addCommand(Command command) {
		commands.add(command);
	}

	public List<Command> getCommands() {
		return commands;
	}
	
	/**
	 * Return a list of RIM commands.
	 * Commands such as GETMetadata are not considered a RIM command.
	 * @return RIM events
	 */
	public List<String> getRIMCommands() {
		List<String> events = new ArrayList<String>();
		for(Command command : commands) {
			String id = command.getId();
			if(!id.equals(GET_METADATA)) {
				events.add(command.getId());
			}
		}
		return events;
	}
}
