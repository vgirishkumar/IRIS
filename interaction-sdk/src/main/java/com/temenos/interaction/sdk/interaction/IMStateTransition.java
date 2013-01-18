package com.temenos.interaction.sdk.interaction;

/**
 * This class holds information about a state transition 
 */
public class IMStateTransition {

	private String title;					//Transition label
	private IMResourceState targetState;	//Name of target state
	private String method;					//Method for transition to pseudo state
	private boolean auto;					//Auto transition
	private boolean boundToCollection;		// the pseudo state is to be bound to the collection path 
	
	public IMStateTransition(String title, 
			IMResourceState targetState, 
			String method, 
			boolean auto, 
			boolean boundToCollection) {
		this.title = title;
		this.targetState = targetState;
		this.method = method;
		this.auto = auto;
		this.boundToCollection = boundToCollection;
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
	
	public boolean isAutoTransition() {
		return auto;
	}
	
	public boolean isBoundToCollection() {
		return boundToCollection;
	}
}
