package com.interaction.example.odata.notes;

import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;

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
		ResourceState notes = new ResourceState("Notes", "collection", "/Notes");
		ResourceState pseudo = new ResourceState(notes, "Notes.pseudo.created");
		ResourceState note = new ResourceState("Notes", "item", "/Notes({id})");
		ResourceState notePerson = new ResourceState("Persons", "NotesPerson", "/Notes({id})/Persons");
		
		notes.addTransition("GET", note);
		notes.addTransition("POST", pseudo);
		note.addTransition("GET", notePerson);
		note.addTransition("DELETE", note);

		return new ResourceStateMachine(notes);
	}

	public ResourceStateMachine getPersonsSM() {
		ResourceState persons = new ResourceState("Persons", "collection", "/Persons");
		ResourceState pseudo = new ResourceState(persons, "Persons.pseudo.created");
		ResourceState person = new ResourceState("Persons", "item", "/Persons({id})");
		ResourceState personNotes = new ResourceState("Notes", "PersonNotes", "/Persons({id})/Notes");
		
		persons.addTransition("GET", person);
		persons.addTransition("POST", pseudo);
		person.addTransition("GET", personNotes);

		return new ResourceStateMachine(persons);
	}

}
