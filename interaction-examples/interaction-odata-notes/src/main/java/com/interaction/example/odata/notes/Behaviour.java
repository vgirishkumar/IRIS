package com.interaction.example.odata.notes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.temenos.interaction.commands.odata.ODataUriSpecification;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class Behaviour {

	// entities
	private final static String NOTE = "Note";
	private final static String PERSON = "Person";
	
	private final static String PERSONS_PATH = "/Person";
	private final static String PERSON_ITEM_PATH = "/Person({id})";
	private final static String NOTES_PATH = "/Note";
	private final static String NOTE_PERSON_PATH = "/Note({id})/NotePerson";
	private final static String PERSON_NOTES_PATH = "/Person({id})/PersonNotes";
	
	public ResourceState getSimpleODataInteractionModel() {
		// the service root
		ResourceState initialState = new ResourceState("ServiceDocument", "begin", createActionSet(new Action("GETServiceDocument", Action.TYPE.VIEW), null), "/");

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

		// links to entities of the same type, therefore same id linkage
		uriLinkageMap.clear();
		// link NotePerson to PersonNotes
		root.getResourceStateByName("NotePerson").addTransition("GET", root.getResourceStateByName("PersonNotes"), uriLinkageMap);
		// link PersonNotes NotePerson
		((CollectionResourceState) root.getResourceStateByName("PersonNotes")).addTransitionForEachItem("GET", root.getResourceStateByName("NotePerson"), uriLinkageMap);
		// link back to person
		root.getResourceStateByName("NotePerson").addTransition("GET", root.getResourceStateByName("person"), uriLinkageMap);
		// link back to note
		((CollectionResourceState) root.getResourceStateByName("PersonNotes")).addTransitionForEachItem("GET", root.getResourceStateByName("note"), uriLinkageMap);

		// Links from a note to a person
		uriLinkageMap.clear();
		uriLinkageMap.put("id", "personId");
		// link from each person's notes back to person
		((CollectionResourceState) root.getResourceStateByName("PersonNotes")).addTransitionForEachItem("GET", root.getResourceStateByName("person"), uriLinkageMap);
		// link from each note to their person
		((CollectionResourceState) root.getResourceStateByName("notes")).addTransitionForEachItem("GET", root.getResourceStateByName("person"), uriLinkageMap);

		uriLinkageMap.clear();
	
	}

	public ResourceStateMachine getNotesSM() {
		CollectionResourceState notes = new CollectionResourceState(NOTE, "notes", createActionSet(new Action("GETEntities", Action.TYPE.VIEW), null), NOTES_PATH);
		ResourceState pseudoCreated = new ResourceState(notes, "PseudoCreated", createActionSet(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		// Option 1 for configuring the interaction - use another state as a parent
		ResourceState note = new ResourceState(notes, 
				"note", 
				createActionSet(new Action("GETEntity", Action.TYPE.VIEW), null), 
				"({id})",
				NOTE.split(" "));
		ResourceState noteUpdated = new ResourceState(note, 
				"updated", 
				createActionSet(null, new Action("UpdateEntity", Action.TYPE.ENTRY)),
				null,
				"edit".split(" ")
				);
		ResourceState noteDeleted = new ResourceState(note, 
				"deleted", 
				createActionSet(null, new Action("DeleteEntity", Action.TYPE.ENTRY)),
				null,
				"delete".split(" ")
				);
		/* 
		 * this navigation property demonstrates an Action properties and 
		 * uri specification to get conceptual configuration into a Command
		 */
		Properties personNotesNavProperties = new Properties();
		personNotesNavProperties.put("entity", NOTE);

		/*
		 * The link relation for a NavProperty must match the NavProperty name to keep ODataExplorer happy
		 */
		ResourceState notePerson = new ResourceState(PERSON, 
				"NotePerson", 
				createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, personNotesNavProperties), null), 
				NOTE_PERSON_PATH, 
				"NotePerson".split(" "),
				new ODataUriSpecification().getTemplate(NOTES_PATH, ODataUriSpecification.NAVPROPERTY_URI_TYPE));
		
		// add collection transition to individual items
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "id");
		// edit
		notes.addTransitionForEachItem("PUT", noteUpdated, uriLinkageMap);
		notes.addTransitionForEachItem("GET", note, uriLinkageMap);
		notes.addTransitionForEachItem("GET", notePerson, uriLinkageMap);
		notes.addTransition("POST", pseudoCreated);
		// auto transition to new note that was just created
		pseudoCreated.addTransition(note);
		note.addTransition("GET", notePerson, uriLinkageMap);
		note.addTransition("PUT", noteUpdated);
		note.addTransition("DELETE", noteDeleted);

		return new ResourceStateMachine(notes);
	}

	public ResourceStateMachine getPersonsSM() {
		CollectionResourceState persons = new CollectionResourceState(PERSON, "persons", createActionSet(new Action("GETEntities", Action.TYPE.VIEW), null), PERSONS_PATH);
		ResourceState pseudo = new ResourceState(persons, "PseudoCreated", createActionSet(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		// Option 2 for configuring the interaction - specify the entity, state, and fully qualified path
		ResourceState person = new ResourceState(PERSON, 
				"person", 
				createActionSet(new Action("GETEntity", Action.TYPE.VIEW), null), 
				PERSON_ITEM_PATH,
				PERSON.split(" "));
		/* 
		 * this navigation property demostrates an Action properties and 
		 * uri specification to get conceptual configuration into a Command
		 */
		Properties personNotesNavProperties = new Properties();
		personNotesNavProperties.put("entity", PERSON);
		
		/*
		 * The link relation for a NavProperty must match the NavProperty name to keep ODataExplorer happy
		 */
		CollectionResourceState personNotes = new CollectionResourceState(NOTE, 
				"PersonNotes", 
				createActionSet(new Action("GETNavProperty", Action.TYPE.VIEW, personNotesNavProperties), null), 
				PERSON_NOTES_PATH, 
				"PersonNotes".split(" "),
				new ODataUriSpecification().getTemplate(PERSONS_PATH, ODataUriSpecification.NAVPROPERTY_URI_TYPE));
		
		// add collection transition to individual items
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "id");
		persons.addTransitionForEachItem("GET", person, uriLinkageMap);
		persons.addTransitionForEachItem("GET", personNotes, uriLinkageMap);
		persons.addTransition("POST", pseudo);
		// add auto transition to new person that was just created
		pseudo.addTransition(person);
		person.addTransition("GET", personNotes, uriLinkageMap);

		return new ResourceStateMachine(persons);
	}

	private Set<Action> createActionSet(Action view, Action entry) {
		Set<Action> actions = new HashSet<Action>();
		if (view != null)
			actions.add(view);
		if (entry != null)
			actions.add(entry);
		return actions;
	}

}
