package com.temenos.interaction.example.note;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.state.HTTPResourceInteractionModel;

/**
 * Define the Note Resource Interaction Model
 * Interactions with Notes are simple.  You can put them, you can put them, you can get them, and you can delete them.
 * @author aphethean
 */
@Path("/notes/{id}")
public class OEntityNoteRIM extends HTTPResourceInteractionModel {

	public final static String RESOURCE_PATH = "/notes/{id}";
	public final static String ENTITY_NAME = "note";

	public OEntityNoteRIM() {
		super(ENTITY_NAME, RESOURCE_PATH);
		/*
		 * Not required when wired with Spring
		 */
		NoteProducerFactory npf = new NoteProducerFactory();
		initialise(npf.getFunctionsProducer());
	}
	
	public OEntityNoteRIM(ODataProducer producer) {
		super(ENTITY_NAME, RESOURCE_PATH);
		initialise(producer);
	}
	
	public void initialise(ODataProducer producer) {
		CommandController commandController = getCommandController();
		commandController.setGetCommand(new OEntityGetNoteCommand(producer));
		commandController.addStateTransitionCommand(new OEntityPutNoteCommand("PUT", RESOURCE_PATH, producer));
		commandController.addStateTransitionCommand(new DeleteNoteCommand("DELETE", RESOURCE_PATH, producer));
	}

	public static Set<String> getValidNextStates() {
		Set<String> states = new HashSet<String>();
		states.add("GET");
		states.add("PUT");
		states.add("DELETE");
		states.add("OPTIONS");
		states.add("HEAD");
		return states;
	}

}
