package com.temenos.interaction.sdk;

/**
 * This class holds information about the resource to generate 
 */
public class ResourceInfo {

	private EntityInfo entityInfo;
	private String resourcePath;
	private String commandType;
	
	public ResourceInfo(String resourcePath, EntityInfo entityInfo, String commandType) {
		this.resourcePath = resourcePath;
		this.entityInfo = entityInfo;
		this.commandType = commandType;
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
}
