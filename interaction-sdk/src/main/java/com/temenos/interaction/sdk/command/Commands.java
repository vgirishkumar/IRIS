package com.temenos.interaction.sdk.command;

/**
 * This class holds information about IRIS commands
 */
public class Commands {
	public final static String DEFAULT_GET_ENTITY_CMD = "com.temenos.interaction.commands.odata.GETEntityCommand";
	public final static String DEFAULT_GET_ENTITIES_CMD = "com.temenos.interaction.commands.odata.GETEntitiesCommand";
	public final static String DEFAULT_CREATE_ENTITY_CMD = "com.temenos.interaction.commands.odata.CreateEntityCommand";
	
	private String getEntityCommand = DEFAULT_GET_ENTITY_CMD;
	private String getEntitiesCommand = DEFAULT_GET_ENTITIES_CMD;
	private String createEntityCommand = DEFAULT_CREATE_ENTITY_CMD; 

	/**
	 * Create an instance of this class
	 */
	public Commands() {
	}

	public String getGetEntityCommand() {
		return getEntityCommand;
	}

	public void setGetEntityCommand(String getEntityCommand) {
		this.getEntityCommand = getEntityCommand;
	}

	public String getGetEntitiesCommand() {
		return getEntitiesCommand;
	}

	public void setGetEntitiesCommand(String getEntitiesCommand) {
		this.getEntitiesCommand = getEntitiesCommand;
	}

	public String getCreateEntityCommand() {
		return createEntityCommand;
	}

	public void setCreateEntityCommand(String createEntityCommand) {
		this.createEntityCommand = createEntityCommand;
	}
	
	public boolean isDefaultGetEntityCommand() {
		return getEntityCommand != null && getEntityCommand.equals(DEFAULT_GET_ENTITY_CMD);
	}

	public boolean isDefaultGetEntitiesCommand() {
		return getEntitiesCommand != null && getEntitiesCommand.equals(DEFAULT_GET_ENTITIES_CMD);
	}

	public boolean isDefaultCreateEntityCommand() {
		return createEntityCommand != null && createEntityCommand.equals(DEFAULT_CREATE_ENTITY_CMD);
	}
}
