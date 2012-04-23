package com.temenos.interaction.example.note;

import com.temenos.interaction.core.link.ResourceState;

public class Behaviour {

	private final static String NEW_ENTITY_NAME = "ID";
	private final static String ENTITY_NAME = "note";
	
	public ResourceState getNotesResoruceInteractionModel() {
		
		ResourceState initialState = new ResourceState("", "begin");
		ResourceState newNoteState = new ResourceState(NEW_ENTITY_NAME, "new", "/notes/new");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "/notes/{id}");
		ResourceState finalState = new ResourceState("", "end");

		// notes collection
		initialState.addTransition("PUT", exists);		
		initialState.addTransition("PUT", newNoteState);		

		// a link 
		newNoteState.addTransition("PUT", exists);
		
		// note item
		exists.addTransition("PUT", exists);		
		exists.addTransition("DELETE", finalState);
		return initialState;
	}

}
