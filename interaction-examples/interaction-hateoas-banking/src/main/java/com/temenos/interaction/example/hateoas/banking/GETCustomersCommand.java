package com.temenos.interaction.example.hateoas.banking;

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
