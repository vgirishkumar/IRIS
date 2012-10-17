package com.temenos.interaction.example.hateoas.banking;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.EntityResource;

public class GETFundTransferCommand implements InteractionCommand {
	private DaoHibernate daoHibernate;
	
	public GETFundTransferCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		// retrieve from a database, etc.
		String id = ctx.getId();
		FundTransfer ft = daoHibernate.getFundTransfer(new Long(id));
		if (ft != null) {
			ctx.setResource(new EntityResource<FundTransfer>(ft));
			return Result.SUCCESS;
		} else {
			return Result.FAILURE;
		}
	}

}
