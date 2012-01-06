package com.temenos.interaction.example.note;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;

import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.state.CRUDResourceInteractionModel;

/**
 * Define the Note Resource Interaction Model (implemented with a JAXB provider)
 * Interactions with Notes are simple.  You can put them, you can put them, you can get them, and you can delete them.
 * @author aphethean
 */
@Path("/notesjaxb/{id}")
public class JAXBNoteRIM extends CRUDResourceInteractionModel {

	public final static String RESOURCE_PATH = "/notesjaxb/{id}";
	public final static String ENTITY_NAME = OEntityNoteRIM.ENTITY_NAME;
	
	public JAXBNoteRIM() {
		super(RESOURCE_PATH);

		/*
		 * Not required when wired with Spring
		 */
		NoteProducerFactory npf = new NoteProducerFactory();
		initialise(npf.getFunctionsProducer());
	}

	public JAXBNoteRIM(ODataProducer producer) {
		super(RESOURCE_PATH);
		initialise(producer);
	}
	
	public void initialise(ODataProducer producer) {
		CommandController commandController = getCommandController();
		commandController.setGetCommand(new JAXBGetNoteCommand(producer));
		commandController.addStateTransitionCommand(new JAXBPutNoteCommand("PUT", RESOURCE_PATH, producer));
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
