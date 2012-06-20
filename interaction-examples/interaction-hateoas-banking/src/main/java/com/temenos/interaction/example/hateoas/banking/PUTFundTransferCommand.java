package com.temenos.interaction.example.hateoas.banking;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.odata4j.core.OEntity;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.ResourcePutCommand;

public class PUTFundTransferCommand implements ResourcePutCommand {
	private DaoHibernate daoHibernate;
	
	public PUTFundTransferCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}

	@SuppressWarnings("unchecked")
	public StatusType put(String id, EntityResource<?> resource) {
		//TODO change HALProvider to support jaxb
		EntityResource<OEntity> er = (EntityResource<OEntity>) resource;
		OEntity oe = er.getEntity();
		//String id = (String) oe.getProperty("id").getValue();
		String body = (String) oe.getProperty("body").getValue();
		FundTransfer ft = new FundTransfer(new Long(id), body);
		daoHibernate.putFundTransfer(ft);
		return Response.Status.OK;
	}

	public String getMethod() {
		return HttpMethod.PUT;
	}

}
