package com.temenos.interaction.example.hateoas.banking;

import java.util.ArrayList;
import java.util.List;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

public class GETCustomersCommand implements InteractionCommand {
	private DaoHibernate daoHibernate;
	
	public GETCustomersCommand(DaoHibernate daoHibernate) {
		this.daoHibernate = daoHibernate;
	}
	
	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		List<EntityResource<Customer>> customers = new ArrayList<EntityResource<Customer>>();
		List<Customer> customerList = daoHibernate.getCustomers();
		for(Customer customer : customerList) {
			customers.add(new EntityResource<Customer>(customer));
		}
		CollectionResource<Customer> cr = new CollectionResource<Customer>("Customer", customers);
		ctx.setResource(cr);
		return Result.SUCCESS;
	}

}
