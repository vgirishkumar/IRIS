package com.temenos.interaction.sdk.command;

/*
 * #%L
 * interaction-sdk
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This class holds information about IRIS commands
 */
public class Commands {
	public final static String GET_SERVICE_DOCUMENT = "GETServiceDocument";
	public final static String GET_METADATA = "GETMetadata";
	public final static String GET_EXCEPTION = "GETException";
	public final static String GET_ENTITY = "GETEntity";
	public final static String GET_ENTITIES = "GETEntities";
	public final static String GET_NAV_PROPERTY = "GETNavProperty";
	public final static String CREATE_ENTITY = "CreateEntity";
	public final static String UPDATE_ENTITY = "UpdateEntity";
	public final static String DELETE_ENTITY = "DeleteEntity";
	public final static String GET_NOOP = "NoopGET";
	
	public final static String HTTP_COMMAND_GET = "GET";
	public final static String HTTP_COMMAND_POST = "POST";
	public final static String HTTP_COMMAND_PUT = "PUT";
	public final static String HTTP_COMMAND_DELETE = "DELETE";
	
	private List<Command> commands = new ArrayList<Command>();					//List of commands
	private SortedMap<String, String> rimEvents = new TreeMap<String, String>();		//Map of <RIM event> to <HTTP command>

	/**
	 * Define a new RIM event
	 * @param rimEvent RIM event
	 * @param httpCommand HTTP command
	 */
	public void addRimEvent(String rimEvent, String httpCommand) {
		rimEvents.put(rimEvent, httpCommand);
	}
	
	/**
	 * Add a new command
	 * @param id command id
	 * @param className command class name
	 */
	public void addCommand(String id, String className) {
		commands.add(new Command(id, className));
	}
	
	/**
	 * Add a new command
	 * @param id command id
	 * @param className command class name
	 * @param param1 command parameter
	 */
	public void addCommand(String id, String className, Parameter param1) {
		Command command = new Command(id, className);
		command.addParameter(param1);
		commands.add(command);
	}

	/**
	 * Add a new command
	 * @param id command id
	 * @param className command class name
	 * @param param1 1st command parameter
	 * @param param2 2nd command parameter
	 */
	public void addCommand(String id, String className, Parameter param1, Parameter param2) {
		Command command = new Command(id, className);
		command.addParameter(param1);
		command.addParameter(param2);
		commands.add(command);
	}
	
	/**
	 * Add a new command
	 * @param id command id
	 * @param className command class name
	 * @param params List of command parameters
	 */
	public void addCommand(String id, String className, List<Parameter> params) {
		Command command = new Command(id, className);
		for(Parameter param : params) {
			command.addParameter(param);
		}
		commands.add(command);
	}
	
	/**
	 * Add an existing command
	 * @param command command
	 */
	public void addCommand(Command command) {
		commands.add(command);
	}

	/**
	 * Obtain a list of commands
	 * @return commands
	 */
	public List<Command> getCommands() {
		return commands;
	}
	
	/**
	 * Return a map between RIM events and HTTP commands
	 * @return rim events
	 */
	public SortedMap<String, String> getRimEvents() {
		return rimEvents;
	}
	
	/**
	 * Return a list of RIM commands.
	 * Commands such as GETMetadata are not considered a RIM command.
	 * @return RIM commands
	 */
	public List<String> getRIMCommands() {
		List<String> events = new ArrayList<String>();
		for(Command command : commands) {
			String id = command.getId();
			if(!id.equals(GET_METADATA)) {
				events.add(command.getId());
			}
		}
		Collections.sort(events);
		return events;
	}
}
