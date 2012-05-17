package com.temenos.interaction.example.note;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.temenos.interaction.core.link.ResourceState;

public class Behaviour {

	private final static String NEW_ENTITY_NAME = "ID";
	private final static String ENTITY_NAME = "note";
	
	public ResourceState getNotesResoruceInteractionModel() {
		
		ResourceState initialState = new ResourceState("", "begin", "/notes");
		ResourceState newNoteState = new ResourceState(NEW_ENTITY_NAME, "new", "/notes/new");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "/notes/{id}");
		ResourceState finalState = new ResourceState(initialState, "end");

		// notes collection
		initialState.addTransition("PUT", exists);		
		initialState.addTransition("POST", newNoteState);		

		// a link (target URI element, source entity element)
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "lastId");
		Set<String> relations = new HashSet<String>();
		relations.add("_new");
		newNoteState.addTransition("PUT", exists, uriLinkageMap);
		
		// note item
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "noteID");
		exists.addTransition("PUT", exists, uriLinkageMap);		
		exists.addTransition("DELETE", finalState, uriLinkageMap);
		return initialState;
	}

}
