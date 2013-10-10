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
