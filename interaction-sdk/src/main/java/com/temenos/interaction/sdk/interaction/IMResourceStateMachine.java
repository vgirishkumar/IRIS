package com.temenos.interaction.sdk.interaction;

/**
 * This class holds information about a resource state machine
 */
public class IMResourceStateMachine {

	private String entityName;
	private String collectionStateName;
	private String entityStateName;
	private String uriParam;
	private String mappedEntityProperty;
	
	public IMResourceStateMachine(String entityName, String collectionStateName, String entityStateName, String uriParam, String mappedEntityProperty) {
		this.entityName = entityName;
		this.collectionStateName = collectionStateName;
		this.entityStateName = entityStateName;
		this.uriParam = uriParam;
		this.mappedEntityProperty = mappedEntityProperty;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public String getCollectionStateName() {
		return collectionStateName;
	}

	public String getEntityStateName() {
		return entityStateName;
	}
	
	public String getUriParam() {
		return uriParam;
	}
	
	public String getMappedEntityProperty() {
		return mappedEntityProperty;
	}
}
