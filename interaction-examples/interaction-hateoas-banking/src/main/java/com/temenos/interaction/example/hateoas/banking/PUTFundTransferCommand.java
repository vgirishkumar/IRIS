package com.temenos.interaction.example.hateoas.banking;


import javax.ws.rs.HttpMethod;

import org.odata4j.core.OEntity;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class PUTFundTransferCommand implements InteractionCommand {
	private DaoHibernate daoHibernate;
	
	public PUTFundTransferCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}

	/* Implement InteractionCommand interface */

	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getResource() != null);
		assert(ctx.getId() != null);
		EntityResource<OEntity> er = (EntityResource<OEntity>) ctx.getResource();
		OEntity oe = er.getEntity();
		String body = (String) oe.getProperty("body").getValue();
		FundTransfer ft = new FundTransfer(new Long(ctx.getId()), body);
		daoHibernate.putFundTransfer(ft);
		ctx.setResource(new EntityResource<FundTransfer>(ft));
		return Result.SUCCESS;
	}

	public String getMethod() {
		return HttpMethod.PUT;
	}

}
