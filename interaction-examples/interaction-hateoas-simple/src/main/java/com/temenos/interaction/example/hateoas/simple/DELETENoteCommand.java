package com.temenos.interaction.example.hateoas.simple;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.core.command.HttpStatusTypes;
import com.temenos.interaction.core.command.ResourceDeleteCommand;

public class DELETENoteCommand implements ResourceDeleteCommand {

	private Persistence persistence;

	public DELETENoteCommand(Persistence p) {
		this.persistence = p;
	}
	
	@Override
	public String getMethod() {
		return HttpMethod.DELETE;
	}

	@Override
	public StatusType delete(String id) {
		// delete from a database, etc.
		persistence.removeNote(new Long(id));
		return HttpStatusTypes.HTTP_STATUS_RESET_CONTENT;
	}

}
