package com.temenos.interaction.example.note;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePutCommand;
import com.temenos.interaction.core.state.CRUDResourceInteractionModel;

/**
 * Define the Note Resource Interaction Model
 * Interactions with Notes are simple.  You can put them, you can put them, you can get them, and you can delete them.
 * @author aphethean
 */
@Path("/notes/{id}")
public class NoteRIM extends CRUDResourceInteractionModel<NoteResource> implements ResourcePutCommand<NoteResource>, ResourceGetCommand {

	private final static String RESOURCE_PATH = "/notes/{id}";
	private final static String ENTITY_NAME = "Note";
	private ODataProducer producer;
	private EdmDataServices edmDataServices;
	
	public NoteRIM() {
		super(RESOURCE_PATH);
		NoteProducerFactory npf = new NoteProducerFactory();
		producer = npf.getProducer();
		edmDataServices = npf.getEdmDataServices();
		CommandController commandController = getCommandController();
		commandController.addGetCommand(RESOURCE_PATH, this);
		commandController.addStateTransitionCommand("PUT", RESOURCE_PATH, this);
	}
	
	public Status put(String id, NoteResource resource) {
		OEntityKey key = OEntityKey.create(new Long(id).toString());
		try {
			producer.deleteEntity(ENTITY_NAME, key);
		} catch (Exception e) {
			// delete the entity if it exists;
		}
		
		EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(ENTITY_NAME);
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		Note note = resource.getNote();
		if (note != null) {
			properties.add(OProperties.int64("noteID", new Long(id)));
			properties.add(OProperties.string("body", note.getBody()));
		}
		OEntity entity = OEntities.create(entitySet, key, properties, new ArrayList<OLink>());
		producer.createEntity(ENTITY_NAME, entity);
		return Response.Status.OK;
	}

	
	/* Implement ResourceGetCommand */
	
	public RESTResponse get(String id) {
		OEntityKey key = OEntityKey.create(new Long(id));
		EntityResponse er = producer.getEntity(ENTITY_NAME, key, null);
		OEntity oEntity = er.getEntity();
		
		RESTResponse rr = new RESTResponse(Response.Status.OK, new NoteResource(oEntity, null), getValidNextStates());
		return rr;
	}

	public Set<String> getValidNextStates() {
		Set<String> states = new HashSet<String>();
		states.add("GET");
		return states;
	}

}
