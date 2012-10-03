package com.temenos.interaction.example.hateoas.banking;

import java.util.ArrayList;
import java.util.List;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

public class GETFundTransfersCommand implements InteractionCommand {
	private DaoHibernate daoHibernate;
	
	public GETFundTransfersCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}
	
	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		List<EntityResource<FundTransfer>> fts = new ArrayList<EntityResource<FundTransfer>>();
		List<FundTransfer> ftList = daoHibernate.getFundTransfers();
		for(FundTransfer ft : ftList) {
			fts.add(new EntityResource<FundTransfer>(ft));
		}
		CollectionResource<FundTransfer> ftResource = new CollectionResource<FundTransfer>("FundTransfer", fts);
		ctx.setResource(ftResource);
		return Result.SUCCESS;
	}

}
