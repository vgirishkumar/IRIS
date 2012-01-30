package com.temenos.interaction.example.note;

import javax.ws.rs.Path;

import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.state.TRANSIENTResourceInteractionModel;

/**
 * Define the 'new' note Resource Interaction Model
 * Interaction with the 'new' note resource is quite simple.  You post to it and you receive a 
 * note id that only you can use.
 * @author aphethean
 */
@Path("/notes/new")
public class NewNoteRIM extends TRANSIENTResourceInteractionModel {

	private final static String RESOURCE_PATH = "/notes/new";
	private final static String ENTITY_NAME = "ID";
	private final static String DOMAIN_OBJECT_NAME = "NOTE";
	
	public NewNoteRIM() {
		super(ENTITY_NAME, RESOURCE_PATH);

		/*
		 * Not required when wired with Spring
		 */
  		NoteProducerFactory npf = new NoteProducerFactory();
		initialise(npf.getFunctionsProducer());
	}
		  	
	public NewNoteRIM(ODataProducer producer) {
		super(ENTITY_NAME, RESOURCE_PATH);
		initialise(producer);
	}
	
	public void initialise(ODataProducer producer) {
		/*
		 * Configure the New Note RIM
		 */
		CommandController commandController = getCommandController();
		commandController.setGetCommand(RESOURCE_PATH, new GETNewNoteCommand(DOMAIN_OBJECT_NAME, producer));
		commandController.addStateTransitionCommand(RESOURCE_PATH, new POSTNewNoteCommand(this, DOMAIN_OBJECT_NAME, OEntityNoteRIM.RESOURCE_PATH, producer));
	}

}
