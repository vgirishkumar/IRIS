package com.temenos.interaction.example.hateoas.banking;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
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
	public Result execute(InteractionContext ctx) {
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
