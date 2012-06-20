package com.temenos.interaction.example.note;

import java.util.Collection;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;

import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.commands.odata.POSTNewCommand;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.Link;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.state.AbstractHTTPResourceInteractionModel;

/**
 * Define the 'new' note Resource Interaction Model
 * Interaction with the 'new' note resource is quite simple.  You post to it and you receive a 
 * note id that only you can use.
 * @author aphethean
 */
@Path("/notes/new")
public class NewNoteRIM extends AbstractHTTPResourceInteractionModel {

	private final static String RESOURCE_PATH = "/notes/new";
	private final static String ENTITY_NAME = "ID";
	private final static String DOMAIN_OBJECT_NAME = "NOTE";
	
	private final ResourceState initial;
	
	public NewNoteRIM() {
		super(RESOURCE_PATH);
		initial = new ResourceState(ENTITY_NAME, "initial", RESOURCE_PATH);
		/*
		 * Not required when wired with Spring
		 */
  		NoteProducerFactory npf = new NoteProducerFactory();
		initialise(npf.getFunctionsProducer());
	}
		  	
	public NewNoteRIM(ODataProducer producer) {
		super(RESOURCE_PATH);
		initial = new ResourceState(ENTITY_NAME, "initial", RESOURCE_PATH);
		initialise(producer);
	}
	
	public void initialise(ODataProducer producer) {
		/*
		 * Configure the New Note RIM
		 */
		CommandController commandController = getCommandController();
		commandController.setGetCommand(RESOURCE_PATH, new GETNewNoteCommand(DOMAIN_OBJECT_NAME, producer));
		commandController.addStateTransitionCommand(RESOURCE_PATH, new POSTNewCommand(ENTITY_NAME, DOMAIN_OBJECT_NAME, OEntityNoteRIM.RESOURCE_PATH, producer));
	}

	public ResourceState getCurrentState() {
		return initial;
	}
	public Collection<Link> getLinks(MultivaluedMap<String, String> pathParameters, RESTResource entity) { return null; }

}
