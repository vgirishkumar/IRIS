package com.temenos.interaction.sdk.interaction.transition;

import com.temenos.interaction.sdk.interaction.state.IMResourceState;

/**
 * This class holds information about a state transition
 */
public class IMTransition {
	private String title;					//Transition label
	private IMResourceState targetState;	//Name of target state
	private String method;					//Method for transition to pseudo state
	
	public IMTransition(String title, IMResourceState targetState,	String method) {
		this.title = title;
		this.targetState = targetState;
		this.method = method;
	}
	
	public String getTitle() {
		return title;
	}

	public IMResourceState getTargetState() {
		return targetState;
	}
	
	public String getTitleAssignment() {
		String title = "";
		if(getTitle() != null) {
			title = "title=\"" + getTitle() + "\" ";
		}
		return title;
	}

	public String getMethod() {
		return method;
	}
}
