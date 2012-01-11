package com.temenos.interaction.example.note;

import javax.ws.rs.HttpMethod;
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
		super(RESOURCE_PATH);

		/*
		 * Not required when wired with Spring
		 */
  		NoteProducerFactory npf = new NoteProducerFactory();
		initialise(npf.getFunctionsProducer());
	}
		  	
	public NewNoteRIM(ODataProducer producer) {
		super(RESOURCE_PATH);
		initialise(producer);
	}
	
	public void initialise(ODataProducer producer) {
		/*
		 * Configure the New Note RIM
		 */
		CommandController commandController = getCommandController();
//		commandController.addStateTransitionCommand("PUT", RESOURCE_PATH, new PutNotSupportedCommand());
		commandController.addStateTransitionCommand(new POSTNewNoteCommand(HttpMethod.POST, RESOURCE_PATH, producer));
	}

	/*
	@PUT
    @Consumes(MediaType.TEXT_PLAIN)
	public RESTResponse put(String resource) {
	    throw new WebApplicationException(Response.status(NotSupportedCommand.HTTP_STATUS_NOT_IMPLEMENTED).entity(NotSupportedCommand.HTTP_STATUS_NOT_IMPLEMENTED_MSG).build());
	}
	*/
}
