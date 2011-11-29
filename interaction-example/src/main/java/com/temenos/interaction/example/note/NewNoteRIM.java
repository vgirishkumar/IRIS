package com.temenos.interaction.example.note;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OFunctionParameter;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmSchema;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePostCommand;
import com.temenos.interaction.core.state.CRUDResourceInteractionModel;

/**
 * Define the 'new' note Resource Interaction Model
 * Interaction with the 'new' note resource is quite simple.  You post to it and you get a 
 * note id that only you can use.
 * @author aphethean
 */
@Path("/notes/new")
public class NewNoteRIM extends CRUDResourceInteractionModel<StringResource> implements ResourcePostCommand<StringResource> {

	private final static String RESOURCE_PATH = "/notes/new";
	private final static String ENTITY_NAME = "ID";
	private final static String DOMAIN_OBJECT_NAME = "NOTE";
	private ODataProducer producer;
	private EdmDataServices edmDataServices;

	public NewNoteRIM() {
		super(RESOURCE_PATH);
		NoteProducerFactory npf = new NoteProducerFactory();
		producer = npf.getProducer();
		edmDataServices = npf.getEdmDataServices();
		CommandController commandController = getCommandController();
		commandController.addGetCommand(RESOURCE_PATH, new ResourceGetCommand() {
			public RESTResponse get(String id) {
				Set<String> validMethods = getValidNextStates();
				return new RESTResponse(Response.Status.OK, null, validMethods);
			}
		});
		commandController.addStateTransitionCommand("POST", RESOURCE_PATH, this);
	}

	private static Set<String> getValidNextStates() {
		Set<String> validMethods = new HashSet<String>();
		validMethods.add("HEAD");
		validMethods.add("OPTIONS");
		validMethods.add("POST");
		return validMethods;
	}
	
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
	public RESTResponse post(String id, String resource) {
		assert(id == null);
		/*
        EdmSchema schema = edmDataServices.findSchema("NorthwindContainer");
        EdmEntityContainer container = schema.findEntityContainer("NorthwindEntities");
        EdmComplexType ct = edmDataServices.findEdmEntityType("Long");

		Map<String, OFunctionParameter> params = new HashMap<String, OFunctionParameter>();
		params.put("DOMAIN_OBJECT_NAME", "NOTE");
		EdmFunctionImport functionName = new EdmFunctionImport(name, null, returnType, "POST", parameters);
		producer.callFunction(functionName, params, null);
		
		
		OEntityKey key = OEntityKey.create(DOMAIN_OBJECT_NAME);
		EntityResponse er = producer.getEntity(ENTITY_NAME, key, null);
		OEntity oEntity = er.getEntity();
		RESTResponse rr = new RESTResponse(Response.Status.OK, new NoteResource(oEntity, null), getValidNextStates());
		return rr;
		*/
		return null;
	}

	public RESTResponse post(String id, StringResource resource) {
		assert(id == null);
		return null;
	}

}
