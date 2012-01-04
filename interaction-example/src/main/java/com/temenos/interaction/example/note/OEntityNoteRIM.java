package com.temenos.interaction.example.note;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.state.CRUDResourceInteractionModel;

/**
 * Define the Note Resource Interaction Model
 * Interactions with Notes are simple.  You can put them, you can put them, you can get them, and you can delete them.
 * @author aphethean
 */
@Path("/notes/{id}")
public class OEntityNoteRIM extends CRUDResourceInteractionModel {

	public final static String RESOURCE_PATH = "/notes/{id}";
	public final static String ENTITY_NAME = "note";
	private ODataProducer producer;

	public OEntityNoteRIM() {
		super(RESOURCE_PATH);

		/*
		 * Not required when wired with Spring
		 */
		NoteProducerFactory npf = new NoteProducerFactory();
		producer = npf.getFunctionsProducer();
		/*
		 * Not required when wired with Spring
		 * 		NoteProducerFactory npf = new NoteProducerFactory();
		 * 		producer = npf.getFunctionsProducer();
		 * 		edmDataServices = producer.getMetadata();
		 */

		CommandController commandController = getCommandController();
		commandController.addGetCommand(RESOURCE_PATH, new OEntityGetNoteCommand(producer));
		commandController.addStateTransitionCommand("PUT", RESOURCE_PATH, new OEntityPutNoteCommand(producer));
		commandController.addStateTransitionCommand("DELETE", RESOURCE_PATH, new DeleteNoteCommand(producer));
	}

	public ODataProducer getProducer() {
		return producer;
	}

	public void setProducer(ODataProducer producer) {
		this.producer = producer;
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
