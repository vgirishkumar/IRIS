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
		initialState.addTransition("GET", getNotesSM());
		// persons service
		initialState.addTransition("GET", getPersonsSM());
		return initialState;
	}

	public ResourceStateMachine getNotesSM() {
		CollectionResourceState notes = new CollectionResourceState("Notes", "collection", "/Notes");
		ResourceState pseudo = new ResourceState(notes, "Notes.pseudo.created");
		ResourceState note = new ResourceState("Notes", "item", "/Notes({id})");
		ResourceState notePerson = new ResourceState("Persons", "NotesPerson", "/Notes({id})/Persons");
		
		// add collection transition to individual items
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "Id");
		notes.addTransitionForEachItem("GET", note, uriLinkageMap);
		notes.addTransition("POST", pseudo);
		note.addTransition("GET", notePerson);
		note.addTransition("DELETE", note);

		return new ResourceStateMachine(notes);
	}

	public ResourceStateMachine getPersonsSM() {
		CollectionResourceState persons = new CollectionResourceState("Persons", "collection", "/Persons");
		ResourceState pseudo = new ResourceState(persons, "Persons.pseudo.created");
		ResourceState person = new ResourceState("Persons", "item", "/Persons({id})");
		ResourceState personNotes = new ResourceState("Notes", "PersonNotes", "/Persons({id})/Notes");
		
		// add collection transition to individual items
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "Id");
		persons.addTransitionForEachItem("GET", person, uriLinkageMap);
		persons.addTransition("POST", pseudo);
		person.addTransition("GET", personNotes, uriLinkageMap);

		return new ResourceStateMachine(persons);
	}

}
