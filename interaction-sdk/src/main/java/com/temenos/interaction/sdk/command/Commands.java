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
	
	public void addCommand(String id, String className, String type, Parameter param1, Parameter param2, Parameter param3, Parameter param4) {
		Command command = new Command(id, className);
		command.addParameter(param1);
		command.addParameter(param2);
		command.addParameter(param3);
		command.addParameter(param4);
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
		for(IMResourceStateMachine rsm : interactionModel.getResourceStateMachines()) {
			for(IMTransition transition : rsm.getTransitions()) {
				String id = "cmdGET" + rsm.getEntityName() + "." + transition.getTargetStateName();
				if(transition.isCollectionState()) {
					addCommand(id, getLinkEntitiesCmdClass, GET_LINK_ENTITY, 
							new Parameter(transition.getTargetEntityName(), false),		//target entity
							JPAResponderGen.COMMAND_METADATA_SOURCE_ODATAPRODUCER);		//producer
				}
				else {
					addCommand(id, getLinkEntityCmdClass, GET_LINK_ENTITY, 
							new Parameter(rsm.getEntityName(), false),					//entity
							new Parameter(transition.getLinkProperty(), false),			//linkProperty
							new Parameter(transition.getTargetEntityName(), false),		//linkEntity
							JPAResponderGen.COMMAND_METADATA_SOURCE_ODATAPRODUCER);		//producer
				}
			}
		}
	}
}
