package com.temenos.interaction.example.hateoas.banking;

/*
 * #%L
 * interaction-example-hateoas-banking
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */



import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.resource.EntityResource;

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
