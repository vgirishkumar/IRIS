package com.temenos.interaction.example.hateoas.banking;


import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperty;

public class PUTFundTransferCommand implements InteractionCommand {
	private DaoHibernate daoHibernate;
	
	public PUTFundTransferCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}

	/* Implement InteractionCommand interface */

	@SuppressWarnings("unchecked")
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getResource() != null);
		assert(ctx.getId() != null);
		EntityResource<Entity> er = (EntityResource<Entity>) ctx.getResource();
		Entity entity = er.getEntity();
		EntityProperty body = (EntityProperty) entity.getProperties().getProperty("body");
		String sBody = (String) body.getValue();
		
		FundTransfer ft = new FundTransfer(new Long(ctx.getId()), sBody);
		daoHibernate.putFundTransfer(ft);
		ctx.setResource(new EntityResource<FundTransfer>(ft));
		return Result.SUCCESS;
	}

	public String getMethod() {
		return HttpMethod.PUT;
	}

}
