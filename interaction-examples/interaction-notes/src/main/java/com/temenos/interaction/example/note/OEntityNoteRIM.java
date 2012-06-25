package com.temenos.interaction.example.note;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.Link;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.state.AbstractHTTPResourceInteractionModel;

/**
 * Define the Note Resource Interaction Model
 * Interactions with Notes are simple.  You can put them, you can put them, you can get them, and you can delete them.
 * @author aphethean
 */
@Path("/notes/{id}")
public class OEntityNoteRIM extends AbstractHTTPResourceInteractionModel {

	public final static String RESOURCE_PATH = "/notes/{id}";
	public final static String ENTITY_NAME = "note";

	public OEntityNoteRIM() {
		super(RESOURCE_PATH);
		/*
		 * Not required when wired with Spring
		 */
		NoteProducerFactory npf = new NoteProducerFactory();
		initialise(npf.getFunctionsProducer());
	}
	
	public OEntityNoteRIM(ODataProducer producer) {
		super(RESOURCE_PATH);
		initialise(producer);
	}
	
	public void initialise(ODataProducer producer) {
		CommandController commandController = getCommandController();
		commandController.setGetCommand(RESOURCE_PATH, new OEntityGetNoteCommand(producer));
		commandController.addStateTransitionCommand(RESOURCE_PATH, new OEntityPutNoteCommand(producer));
		commandController.addStateTransitionCommand(RESOURCE_PATH, new DeleteNoteCommand(producer));
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

	public ResourceState getCurrentState() { return null; }
	public Collection<Link> getLinks(HttpHeaders headers, MultivaluedMap<String, String> pathParameters, RESTResource entity) { return null; }

}
