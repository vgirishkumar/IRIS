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
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

public class GETCustomersCommand implements InteractionCommand {
	private DaoHibernate daoHibernate;
	
	public GETCustomersCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		List<EntityResource<Entity>> customers = new ArrayList<EntityResource<Entity>>();
		List<Customer> customerList = daoHibernate.getCustomers();
		for(Customer customer : customerList) {
			//Convert Customer object into Entity object
			EntityProperties addressFields = new EntityProperties();
			addressFields.setProperty(new EntityProperty("postcode", customer.getAddress().getPostcode()));
			addressFields.setProperty(new EntityProperty("houseNumber", customer.getAddress().getHouseNumber()));
			
			EntityProperties props = new EntityProperties();
			props.setProperty(new EntityProperty("name", customer.getName()));
			props.setProperty(new EntityProperty("address", addressFields));
			props.setProperty(new EntityProperty("dateOfBirth", customer.getDateOfBirth()));
			Entity entity = new Entity(customer.getName(), props);
			
			customers.add(createEntityResource(entity));
		}
		ctx.setResource(createCollectionResource("Customer", customers));
		return Result.SUCCESS;
	}

	@SuppressWarnings("hiding")
	private static<Entity> EntityResource<Entity> createEntityResource(Entity e) 
	{
		return new EntityResource<Entity>(e) {};	
	}

	@SuppressWarnings("hiding")
	private static<Entity> CollectionResource<Entity> createCollectionResource(String entitySetName, List<EntityResource<Entity>> entityResources) {
		return new CollectionResource<Entity>(entitySetName, entityResources) {};
	}
}
