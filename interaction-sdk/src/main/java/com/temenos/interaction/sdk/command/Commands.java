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
	public final static String GET_ENTITY = "GETEntity";
	public final static String GET_ENTITIES = "GETEntities";
	public final static String POST_ENTITY = "POSTEntity";
	public final static String GET_LINK_ENTITY = "GETLinkEntity";
	
	private List<Command> commands = new ArrayList<Command>();

	public Commands() {
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
		for(IMResourceStateMachine rsm : interactionModel.getResourceStateMachines()) {
			for(IMTransition transition : rsm.getTransitions()) {
				String id = "cmdGET" + rsm.getEntityName() + "." + transition.getTargetStateName();
				List<Parameter> cmdParams = new ArrayList<Parameter>();
				if(transition.isCollectionState()) {
					cmdParams.add(new Parameter(transition.getTargetEntityName(), false));		//target entity
					cmdParams.add(JPAResponderGen.COMMAND_METADATA_SOURCE_ODATAPRODUCER);		//producer
					cmdParams.addAll(params);													//additional params
					addCommand(id, getLinkEntitiesCmdClass, GET_LINK_ENTITY, cmdParams);
				}
				else {
					cmdParams.add(new Parameter(rsm.getEntityName(), false));					//entity
					cmdParams.add(new Parameter(transition.getLinkProperty(), false));			//linkProperty
					cmdParams.add(new Parameter(transition.getTargetEntityName(), false));		//linkEntity
					cmdParams.add(JPAResponderGen.COMMAND_METADATA_SOURCE_ODATAPRODUCER);		//producer
					cmdParams.addAll(params);													//additional params
					addCommand(id, getLinkEntityCmdClass, GET_LINK_ENTITY, cmdParams);
				}
			}
		}
	}
}
