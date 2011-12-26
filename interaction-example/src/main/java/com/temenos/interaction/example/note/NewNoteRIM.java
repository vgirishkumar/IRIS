package com.temenos.interaction.example.note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OFunctionParameter;
import org.odata4j.core.OFunctionParameters;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.producer.BaseResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.PropertyResponse;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.PutNotSupportedCommand;
import com.temenos.interaction.core.command.ResourcePostCommand;
import com.temenos.interaction.core.state.CRUDResourceInteractionModel;

/**
 * Define the 'new' note Resource Interaction Model
 * Interaction with the 'new' note resource is quite simple.  You post to it and you receive a 
 * note id that only you can use.
 * @author aphethean
 */
@Path("/notes/new")
public class NewNoteRIM extends CRUDResourceInteractionModel implements ResourcePostCommand {

	private final static String RESOURCE_PATH = "/notes/new";
	private final static String ENTITY_NAME = "ID";
	private final static String DOMAIN_OBJECT_NAME = "NOTE";
	private ODataProducer producer;
	private EdmDataServices edmDataServices;

	public NewNoteRIM() {
		super(RESOURCE_PATH);

		/*
		 * Not required when wired with Spring
		 */
		  		NoteProducerFactory npf = new NoteProducerFactory();
		  		producer = npf.getFunctionsProducer();
		  		edmDataServices = producer.getMetadata();
		/*
		 * Not required when wired with Spring
		 * 		NoteProducerFactory npf = new NoteProducerFactory();
		 * 		producer = npf.getFunctionsProducer();
		 * 		edmDataServices = producer.getMetadata();
		 */

		  		
		/*
		 * Configure the dynamic RIM
		 */
		CommandController commandController = getCommandController();
		commandController.addStateTransitionCommand("PUT", RESOURCE_PATH, new PutNotSupportedCommand());
		commandController.addStateTransitionCommand("POST", RESOURCE_PATH, this);
	}

	public ODataProducer getProducer() {
		return producer;
	}

	public void setProducer(ODataProducer producer) {
		this.producer = producer;
		edmDataServices = producer.getMetadata();
	}

	private static Set<String> getValidNextStates() {
		Set<String> validMethods = new HashSet<String>();
		validMethods.add("HEAD");
		validMethods.add("OPTIONS");
		validMethods.add("POST");
		return validMethods;
	}
	
	
	@PUT
    @Consumes(MediaType.TEXT_PLAIN)
	public RESTResponse put(String resource) {
	    throw new WebApplicationException(Response.status(PutNotSupportedCommand.HTTP_STATUS_NOT_IMPLEMENTED).entity(PutNotSupportedCommand.HTTP_STATUS_NOT_IMPLEMENTED_MSG).build());
	}
	
	
	public RESTResponse post(String id, EntityResource resource) {
		assert(id == null || "".equals(id));

        // find the function that creates us new things
        EdmFunctionImport functionName = edmDataServices.findEdmFunctionImport("NEW");
        
		Map<String, OFunctionParameter> params = new HashMap<String, OFunctionParameter>();
		params.put("PARAM1", OFunctionParameters.create("DOMAIN_OBJECT_NAME", "NOTE"));
		//EdmFunctionImport functionName = new EdmFunctionImport("NEW", null, returnType, "POST", params);
		BaseResponse fr = producer.callFunction(functionName, params, null);
		assert(functionName.returnType == EdmSimpleType.INT64);
		
		// TODO this could either be the type we are creating and ID for, or it could just be a transient type
		EdmEntitySet noteEntitySet = edmDataServices.findEdmEntitySet("Note");
		OEntityKey entityKey = OEntityKey.create("new");
		List<OLink> links = new ArrayList<OLink>();
		String replacement = ((PropertyResponse)fr).getProperty().getValue().toString();
		links.add(OLinks.link("_new", "NewNote", NoteRIM.RESOURCE_PATH.replaceFirst("\\{id\\}", replacement)));
		final OEntity entity = OEntities.create(noteEntitySet, entityKey, new ArrayList<OProperty<?>>(), links);
		EntityResource er = new EntityResource() {
			public OEntity getOEntity() {
				return entity;
			}
		};
		return new RESTResponse(Response.Status.OK, er, getValidNextStates());
	}

}
