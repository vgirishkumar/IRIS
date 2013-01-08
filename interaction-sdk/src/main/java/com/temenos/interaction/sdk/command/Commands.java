package com.temenos.interaction.sdk.command;

import java.util.ArrayList;
import java.util.List;

import com.temenos.interaction.sdk.JPAResponderGen;
import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.IMTransition;
import com.temenos.interaction.sdk.interaction.InteractionModel;

/**
 * This class holds information about IRIS commands
 */
public class Commands {
	public final static String GET_SERVICE_DOCUMENT = "GETServiceDocument";
	public final static String GET_METADATA = "GETMetadata";
	public final static String GET_ENTITY = "GETEntity";
	public final static String GET_ENTITIES = "GETEntities";
	public final static String GET_ENTITIES_FILTERED = "GETEntitiesFiltered";
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
			if(!id.equals(GET_METADATA) &&
				!id.startsWith(GET_NAV_PROPERTY)) {
				if(id.equals(GET_ENTITIES_FILTERED)) {
					events.add(command.getId() + " filter=filter");
				}
				else {
					events.add(command.getId());
				}
			}
		}
		return events;
	}
	
	/**
	 * Add commands executed when following a link to another resource.
	 * @param interactionModel Interaction model
	 * @param getLinkEntityCmdClass class name of command linking to an entity resource
	 * @param getLinkEntitiesCmdClass class name of command linking to an collection resource 
	 */
	public void addLinkCommands(InteractionModel interactionModel, String getLinkEntityCmdClass, String getLinkEntitiesCmdClass) {
		addLinkCommands(interactionModel, getLinkEntityCmdClass, getLinkEntitiesCmdClass, new ArrayList<Parameter>());
	}

	/**
	 * Add commands executed when following a link to another resource.
	 * @param interactionModel Interaction model
	 * @param getLinkEntityCmdClass class name of command linking to an entity resource
	 * @param getLinkEntitiesCmdClass class name of command linking to an collection resource 
	 * @param params list of additional parameters to pass into the command
	 */
	public void addLinkCommands(InteractionModel interactionModel, String getLinkEntityCmdClass, String getLinkEntitiesCmdClass, List<Parameter> params) {
		addLinkCommands(interactionModel, getLinkEntityCmdClass, true, getLinkEntitiesCmdClass, true, params);
	}
	
	/**
	 * Add commands executed when following a link to another resource.
	 * @param interactionModel Interaction model
	 * @param getLinkEntityCmdClass class name of command linking to an entity resource
	 * @param isLinkEntityCmdOdataProducer true if this command uses an odata producer, false if metadata
	 * @param getLinkEntitiesCmdClass class name of command linking to an collection resource 
	 * @param isLinkEntitiesCmdOdataProducer true if this command uses an odata producer, false if metadata
	 * @param params List of additional parameters
	 */
	public void addLinkCommands(InteractionModel interactionModel, String getLinkEntityCmdClass, boolean isLinkEntityCmdOdataProducer, String getLinkEntitiesCmdClass, boolean isLinkEntitiesCmdOdataProducer, List<Parameter> params) {
		for(IMResourceStateMachine rsm : interactionModel.getResourceStateMachines()) {
			for(IMTransition transition : rsm.getTransitions()) {
				String id = "GETNavProperty" + transition.getTargetStateName();
				List<Parameter> cmdParams = new ArrayList<Parameter>();
				if(transition.isCollectionState()) {
					cmdParams.add(isLinkEntitiesCmdOdataProducer ? JPAResponderGen.COMMAND_METADATA_SOURCE_ODATAPRODUCER : JPAResponderGen.COMMAND_METADATA_SOURCE_MODEL);		//producer
					cmdParams.addAll(params);													//additional params
					addCommand(id, getLinkEntitiesCmdClass, GET_LINK_ENTITY, cmdParams);
				}
				else {
					cmdParams.add(new Parameter(transition.getLinkProperty(), false, ""));			//linkProperty
					cmdParams.add(new Parameter(transition.getTargetEntityName(), false, ""));		//linkEntity
					cmdParams.add(isLinkEntityCmdOdataProducer ? JPAResponderGen.COMMAND_METADATA_SOURCE_ODATAPRODUCER : JPAResponderGen.COMMAND_METADATA_SOURCE_MODEL);		//producer
					cmdParams.addAll(params);													//additional params
					addCommand(id, getLinkEntityCmdClass, GET_LINK_ENTITY, cmdParams);
				}
			}
		}
	}
}
