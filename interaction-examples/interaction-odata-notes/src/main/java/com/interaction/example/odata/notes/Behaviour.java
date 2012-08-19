package com.interaction.example.odata.notes;

import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class Behaviour {

	public ResourceState getSimpleODataInteractionModel() {
		// the service root
		ResourceState initialState = new ResourceState("ServiceDocument", "begin", "/");
		
		/*
		 * create a resource to the $metadata link, this will also require use to 
		 * create a GET command for the $metadata
		 */
		ResourceState metadata = new ResourceState("", "metadata", "/$metadata");
		
		initialState.addTransition("GET", metadata);
		// notes service
		ResourceStateMachine notes = getNotesSM();
		initialState.addTransition("GET", notes);
		// persons service
		ResourceStateMachine persons = getPersonsSM();
		initialState.addTransition("GET", persons);
		
		// now link the two entity sets
		addTransitionsBetweenRSMs(new ResourceStateMachine(initialState));
		
		return initialState;
	}

	public void addTransitionsBetweenRSMs(ResourceStateMachine root) {
		Map<String, String> uriLinkageMap = new HashMap<String, String>();

		// A Note has a Person who created it
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "Person.id");
//		root.getState("/Notes({id})/Person").addTransition("GET", root.getState("/Persons({id})"), uriLinkageMap);
//		((CollectionResourceState) root.getState("/Notes")).addTransitionForEachItem("GET", root.getState("/Persons({id})"), uriLinkageMap);
		
		// Persons could all have some Notes
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "personid");
		((CollectionResourceState) root.getState("/Persons({id})/Notes")).addTransitionForEachItem("GET", root.getState("/Notes({id})"), uriLinkageMap);
	}

	public ResourceStateMachine getNotesSM() {
		CollectionResourceState notes = new CollectionResourceState("Notes", "collection", "/Notes");
		ResourceState pseudo = new ResourceState(notes, "PseudoCreated", null);
		// Option 1 for configuring the interaction - use another state as a parent
		ResourceState note = new ResourceState(notes, "item", "({id})");
		ResourceState noteDeleted = new ResourceState(note, "deleted");
		ResourceState notePerson = new ResourceState("Persons", "NotesPerson", "/Notes({id})/Person");
		
		// add collection transition to individual items
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "id");
		notes.addTransitionForEachItem("GET", note, uriLinkageMap);
		notes.addTransitionForEachItem("GET", notePerson, uriLinkageMap);
		notes.addTransition("POST", pseudo);
		note.addTransition("GET", notePerson, uriLinkageMap);
		note.addTransition("DELETE", noteDeleted);

		return new ResourceStateMachine(notes);
	}

	public ResourceStateMachine getPersonsSM() {
		CollectionResourceState persons = new CollectionResourceState("Persons", "collection", "/Persons");
		ResourceState pseudo = new ResourceState(persons, "PseudoCreated", null);
		// Option 2 for configuring the interaction - specify the entity, state, and fully qualified path
		ResourceState person = new ResourceState("Persons", "item", "/Persons({id})");
		CollectionResourceState personNotes = new CollectionResourceState("Notes", "PersonNotes", "/Persons({id})/Notes");
		
		// add collection transition to individual items
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "id");
		persons.addTransitionForEachItem("GET", person, uriLinkageMap);
		persons.addTransitionForEachItem("GET", personNotes, uriLinkageMap);
		persons.addTransition("POST", pseudo);
		person.addTransition("GET", personNotes, uriLinkageMap);

		return new ResourceStateMachine(persons);
	}

}
