package com.temenos.interaction.sdk.command;

/**
 * This class holds information about an IRIS command
 */
public class Parameter {
	private String value;
	private boolean isByRef;
	private String refId;

	public Parameter(String value, boolean isByRef, String refId) {
		this.value = value;
		this.isByRef = isByRef;
		this.refId = refId;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean isByReference() {
		return isByRef;
	}
	
	public String getRefId() {
		return refId;
	}
}
