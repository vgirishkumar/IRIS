package com.temenos.interaction.example.hateoas.banking;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.EntityResource;

public class GETFundTransferCommand implements ResourceGetCommand {
	private DaoHibernate daoHibernate;
	
	public GETFundTransferCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}

	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		FundTransfer ft = daoHibernate.getFundTransfer(new Long(id));
		return new RESTResponse(Status.OK, new EntityResource<FundTransfer>(ft));
	}

}
