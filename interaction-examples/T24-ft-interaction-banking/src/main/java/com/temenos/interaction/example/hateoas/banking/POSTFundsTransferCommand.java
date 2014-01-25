package com.temenos.interaction.example.hateoas.banking;

/*
 * #%L
 * T24-ft-interaction-banking
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
import java.util.Collection;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.resource.EntityResource;

public class POSTFundsTransferCommand implements InteractionCommand {

	/* Implement InteractionCommand interface */
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getId() == null || "".equals(ctx.getId()));

		String Id = "NEW";
		String TransactionType = "AC";
		String DebitAcctNo = "60127";
		String DebitCurrency = "USD";
		String DebitAmount = "100";
		String CreditAcctNo = "63417";
		
		EntityProperties entityProperties = new EntityProperties();
		entityProperties.setProperty( new EntityProperty( "Id", Id ) );
		entityProperties.setProperty( new EntityProperty( "TransactionType", TransactionType ) );
		entityProperties.setProperty( new EntityProperty( "DebitAcctNo", DebitAcctNo ) );
		entityProperties.setProperty( new EntityProperty( "DebitCurrency", DebitCurrency ) );
		entityProperties.setProperty( new EntityProperty( "DebitAmount", DebitAmount ) );
		entityProperties.setProperty( new EntityProperty( "CreditAcctNo", CreditAcctNo ) );
		
		Entity entity = new Entity("FundsTransfer", entityProperties);
		EntityResource<Entity> er = FundsTransferHelper.createEntityResource( entity );
		
		Collection<Link> links = new ArrayList<Link>();
		links.add( new Link("new", "self", "http://localhost:8080/example/api/fundstransfer/new", null, null) );
		er.setLinks(links);
		ctx.setResource(er);
		
		return Result.SUCCESS;
	}

	public String getMethod() {
		return HttpMethod.POST;
	}

}
