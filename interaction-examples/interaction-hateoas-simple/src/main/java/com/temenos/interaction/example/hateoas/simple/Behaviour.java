package com.temenos.interaction.example.hateoas.simple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;

public class Behaviour {

	// the entity that generates new IDs
	private final static String NEW_ENTITY_NAME = "ID";
	// the entity that stores notes
	private final static String NOTE_ENTITY_NAME = "note";

	public ResourceState getInteractionModel() {
		// this will be the service root
		ResourceState initialState = new ResourceState("", "initial", "");
		
		ResourceState profile = new ResourceState("Profile", "profile", "/profile");
		ResourceState preferences = new ResourceState("Preferences", "preferences", "/preferences");
		
		initialState.addTransition("GET", profile);
		initialState.addTransition("GET", new ResourceStateMachine(preferences));
		initialState.addTransition("GET", getNotesInteractionModel());
		return initialState;
	}

	public ResourceStateMachine getNotesInteractionModel() {
		
		ResourceState initialState = new ResourceState(NOTE_ENTITY_NAME, "initial", "/notes");
		ResourceState newNoteState = new ResourceState(NEW_ENTITY_NAME, "new", "/notes/new");
		ResourceState exists = new ResourceState(NOTE_ENTITY_NAME, "exists", "/notes/{id}");
		ResourceState finalState = new ResourceState(NOTE_ENTITY_NAME, "end");

		// a linkage map (target URI element, source entity element)
		Map<String, String> uriLinkageMap = new HashMap<String, String>();

		// a link from notes collection to create a new note (no arguments to this link)
		initialState.addTransition("POST", newNoteState);		

		// link from new note id to save the note
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "lastId");
		Set<String> relations = new HashSet<String>();
		relations.add("_new");
		newNoteState.addTransition("PUT", exists, uriLinkageMap);
		
		// a link on each note in the collection to delete the note
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "noteID");
//		initialState.addTransition("DELETE", exists);		

		// update / delete note item (same linkage map)
		exists.addTransition("PUT", exists, uriLinkageMap);		
		exists.addTransition("DELETE", finalState, uriLinkageMap);
		return new ResourceStateMachine(initialState);
	}

}
