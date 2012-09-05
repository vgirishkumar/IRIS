package com.temenos.interaction.example.hateoas.banking;

import com.temenos.interaction.core.resource.EntityResource;

public class FundsTransferHelper {

	public static<Entity> EntityResource<Entity> createEntityResource(Entity e) 
	{
		return new EntityResource<Entity>(e) {};	
	}
}
