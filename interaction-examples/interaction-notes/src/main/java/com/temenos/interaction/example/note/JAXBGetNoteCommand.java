package com.temenos.interaction.example.note;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;

public class JAXBGetNoteCommand implements ResourceGetCommand {

	private ODataProducer producer;
	
	public JAXBGetNoteCommand(ODataProducer producer) {
		this.producer = producer;
	}
	
	/* Implement ResourceGetCommand (JAXB) */
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		OEntityKey key = OEntityKey.create(new Long(id));
		EntityResponse er = producer.getEntity(OEntityNoteRIM.ENTITY_NAME, key, null);
		OEntity oEntity = er.getEntity();
		
		RESTResponse rr = new RESTResponse(Response.Status.OK, new EntityResource<Note>(new Note(oEntity)));
		rr.getValidMethods().addAll(OEntityNoteRIM.getValidNextStates());
		return rr;
	}

}
