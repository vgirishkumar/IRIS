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


import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.resource.EntityResource;

public class GETCustomerCommand implements InteractionCommand {
	private DaoHibernate daoHibernate;
	
	public GETCustomerCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		assert(ctx != null);
		// retrieve from a database, etc.
		String id = ctx.getId();
		Customer customer = daoHibernate.getCustomer(id);
		if (customer != null) {
			//Convert Customer object into Entity object
			EntityProperties addressFields = new EntityProperties();
			addressFields.setProperty(new EntityProperty("postcode", customer.getAddress().getPostcode()));
			addressFields.setProperty(new EntityProperty("houseNumber", customer.getAddress().getHouseNumber()));
			
			EntityProperties props = new EntityProperties();
			props.setProperty(new EntityProperty("name", customer.getName()));
			props.setProperty(new EntityProperty("address", addressFields));
			props.setProperty(new EntityProperty("dateOfBirth", customer.getDateOfBirth()));
			Entity entity = new Entity("Customer", props);
			
			ctx.setResource(createEntityResource(entity));
			return Result.SUCCESS;
		} else {
			return Result.FAILURE;
		}
	}

	@SuppressWarnings("hiding")
	public static<Entity> EntityResource<Entity> createEntityResource(Entity e) 
	{
		return new EntityResource<Entity>(e) {};	
	}
}
