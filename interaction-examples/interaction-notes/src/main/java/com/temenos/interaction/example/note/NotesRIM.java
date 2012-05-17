package com.temenos.interaction.example.note;

import java.util.Collection;

import javax.ws.rs.Path;

import org.odata4j.producer.ODataProducer;

import com.jayway.jaxrs.hateoas.HateoasLink;
import com.temenos.interaction.commands.odata.GETEntitiesCommand;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.state.AbstractHTTPResourceInteractionModel;

@Path("/notes")
public class NotesRIM extends AbstractHTTPResourceInteractionModel {

	private final static String RESOURCE_PATH = "/notes";
	private final static String ENTITYSET_NAME = "note";

	public NotesRIM() {
		super(RESOURCE_PATH);
		/*
		 * Not required when wired with Spring
		 */
  		NoteProducerFactory npf = new NoteProducerFactory();
		initialise(npf.getFunctionsProducer());
	}
		  	
	public NotesRIM(ODataProducer producer) {
		super(RESOURCE_PATH);
		initialise(producer);
	}
	
	public void initialise(ODataProducer producer) {
		/*
		 * Configure the New Note RIM
		 */
		CommandController commandController = getCommandController();
		commandController.setGetCommand(RESOURCE_PATH, new GETEntitiesCommand(ENTITYSET_NAME, producer));
	}

	public ResourceState getCurrentState() { return null; }
	public Collection<HateoasLink> getLinks(RESTResource entity) { return null; }

}
