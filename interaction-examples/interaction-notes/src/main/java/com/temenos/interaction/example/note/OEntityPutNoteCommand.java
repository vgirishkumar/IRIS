package com.temenos.interaction.example.note;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
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
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.ResourcePutCommand;

public class OEntityPutNoteCommand implements ResourcePutCommand {

	private ODataProducer producer;
	private EdmDataServices edmDataServices;

	public OEntityPutNoteCommand(ODataProducer producer) {
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
	}

	/* Implement ResourcePutCommand */
	@SuppressWarnings("unchecked")
	public Status put(String id, EntityResource<?> resource) {
		EntityResource<OEntity> entityResource = (EntityResource<OEntity>) resource; 
		OEntityKey key = OEntityKey.create(new Long(id));
		try {
			producer.deleteEntity(OEntityNoteRIM.ENTITY_NAME, key);
		} catch (Exception e) {
			// delete the entity if it exists;
		}
		
		EdmEntitySet entitySet = edmDataServices.getEdmEntitySet(OEntityNoteRIM.ENTITY_NAME);
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.int64("noteID", new Long(id)));
		properties.addAll((entityResource.getEntity().getProperties()));
/*		
		Note note = (Note) resource.getEntity();
		if (note != null) {
			properties.add(OProperties.int64("noteID", new Long(id)));
			properties.add(OProperties.string("body", note.getBody()));
		}
*/
		OEntity entity = OEntities.create(entitySet, key, properties, new ArrayList<OLink>());
		producer.createEntity(OEntityNoteRIM.ENTITY_NAME, entity);
		return Response.Status.OK;
	}

	public String getMethod() {
		return HttpMethod.PUT;
	}

}
