package com.temenos.interaction.example.note;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResource;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePutCommand;
import com.temenos.interaction.core.state.GPPDResource;

/**
 * Notes are simple.  You can put them, you can post them, you can get them, and you can delete them.
 * @author aphethean
 */
@Path("/notes/{id}")
@XmlRootElement(name = "resource")
public class NoteResource extends GPPDResource<NoteResource> implements EntityResource, ResourcePutCommand<NoteResource>, ResourceGetCommand {

	private final static String RESOURCE_PATH = "/notes/{id}";
	private final static String ENTITY_NAME = "Note";
	private ODataProducer producer;
	private EdmDataServices edmDataServices;
	
    @XmlElement(name = "body")
    private String body;

	public NoteResource() {
		super(RESOURCE_PATH);
		NoteProducerFactory npf = new NoteProducerFactory();
		producer = npf.getProducer();
		edmDataServices = npf.getEdmDataServices();
		CommandController commandController = getCommandController();
		commandController.addGetCommand(RESOURCE_PATH, this);
		commandController.addStateTransitionCommand("PUT", RESOURCE_PATH, this);
	}
	
	public Note getNote() {
		return new Note(body);
	}
	
	/* Implement EntityResource */
	
	private OEntity oEntity = null;
	public OEntity getEntity() {
		// TODO Auto-generated method stub
		return oEntity;
	}

	public Set<OLink> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Status put(String id, NoteResource resource) {
		// NOT THREAD SAFE!!!!
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
		EntityResponse er = producer.createEntity(ENTITY_NAME, entity);
		OEntity createdEntity = er.getEntity();
		return Response.Status.OK;
	}

	
	/* Implement ResourceGetCommand */
	
	public Status get(String id) {
		// NOT THREAD SAFE!!!!
		OEntityKey key = OEntityKey.create(new Long(id));
		EntityResponse er = producer.getEntity(ENTITY_NAME, key, null);
		oEntity = er.getEntity();
		return Response.Status.OK;
	}

	public RESTResource getResource() {
		return this;
	}

	public Set<String> getValidNextStates() {
		Set<String> states = new HashSet<String>();
		states.add("GET");
		return states;
	}

}
