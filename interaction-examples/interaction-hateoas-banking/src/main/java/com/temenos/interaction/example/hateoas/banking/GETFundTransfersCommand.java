package com.temenos.interaction.example.hateoas.banking;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

public class GETFundTransfersCommand implements ResourceGetCommand {
	private DaoHibernate daoHibernate;
	
	public GETFundTransfersCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}
	
	@Override
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		List<EntityResource<FundTransfer>> fts = new ArrayList<EntityResource<FundTransfer>>();
		List<FundTransfer> ftList = daoHibernate.getFundTransfers();
		for(FundTransfer ft : ftList) {
			fts.add(new EntityResource<FundTransfer>(ft));
		}
		CollectionResource<FundTransfer> ftResource = new CollectionResource<FundTransfer>("FundTransfer", fts);
		return new RESTResponse(Status.OK, ftResource);
	}

}
