package com.temenos.interaction.example.hateoas.banking;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.ResourcePutCommand;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperty;

public class PUTFundTransferCommand implements ResourcePutCommand {
	private DaoHibernate daoHibernate;
	
	public PUTFundTransferCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}

	@SuppressWarnings("unchecked")
	public StatusType put(String id, EntityResource<?> resource) {
		EntityResource<Entity> er = (EntityResource<Entity>) resource;
		Entity entity = er.getEntity();
		EntityProperty body = (EntityProperty) entity.getProperties().getProperty("body");
		String sBody = (String) body.getValue();
		
		
		FundTransfer ft = new FundTransfer(new Long(id), sBody);
		daoHibernate.putFundTransfer(ft);
		return Response.Status.OK;
	}

	public String getMethod() {
		return HttpMethod.PUT;
	}

}
