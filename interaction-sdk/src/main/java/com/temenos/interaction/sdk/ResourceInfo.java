package com.temenos.interaction.sdk;

/**
 * This class holds information about the resource to generate 
 */
public class ResourceInfo {

	private EntityInfo entityInfo;
	private String resourcePath;
	private String commandType;
	private boolean isDefaultCommand;

	public ResourceInfo(String resourcePath, EntityInfo entityInfo, String commandType) {
		this(resourcePath, entityInfo, commandType, true);
	}
	
	public ResourceInfo(String resourcePath, EntityInfo entityInfo, String commandType, boolean isDefaultCommand) {
		this.resourcePath = resourcePath;
		this.entityInfo = entityInfo;
		this.commandType = commandType;
		this.isDefaultCommand = isDefaultCommand;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
	
	public String getCommandType() {
		return commandType;
	}
	
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
	
	public boolean isDefaultCommand() {
		return isDefaultCommand;		
	}
}
