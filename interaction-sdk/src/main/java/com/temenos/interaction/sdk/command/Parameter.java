package com.temenos.interaction.sdk.command;

/**
 * This class holds information about an IRIS command
 */
public class Parameter {
	private String value;
	private boolean isByRef;

	public Parameter(String value, boolean isByRef) {
		this.value = value;
		this.isByRef = isByRef;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean isByReference() {
		return isByRef;
	}
}
