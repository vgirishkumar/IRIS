package com.temenos.interaction.example.note;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.odata4j.core.OEntityKey;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.command.ResourceDeleteCommand;

public class DeleteNoteCommand implements ResourceDeleteCommand {

	private String method;
	private String path;
	private ODataProducer producer;
	
	public DeleteNoteCommand(String method, String path, ODataProducer producer) {
		this.method = method;
		this.path = path;
		this.producer = producer;
	}

	/* Implement ResourceDeleteCommand */
	public StatusType delete(String id) {
		OEntityKey key = OEntityKey.create(new Long(id));
		try {
			producer.deleteEntity(OEntityNoteRIM.ENTITY_NAME, key);
		} catch (Exception e) {
			// delete the entity if it exists;
		}
		return Response.Status.NO_CONTENT;
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

}
