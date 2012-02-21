package com.temenos.interaction.sdk;

/**
 * This class holds information about the resource to generate 
 */
public class ResourceInfo {

	private JPAEntityInfo jpaEntityInfo;
	private String resourcePath;
	private String commandType;
	
	public ResourceInfo(String resourcePath, JPAEntityInfo jpaEntityInfo, String commandType) {
		this.resourcePath = resourcePath;
		this.jpaEntityInfo = jpaEntityInfo;
		this.commandType = commandType;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
	
	public String getCommandType() {
		return commandType;
	}
	
	public JPAEntityInfo getJPAEntityInfo() {
		return jpaEntityInfo;
	}
}
