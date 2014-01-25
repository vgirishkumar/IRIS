package com.interaction.example.odata.embedded.command;

/*
 * #%L
 * interaction-example-odata-embedded
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;

public class GETEntityPassengerWithFlightEmbedded implements InteractionCommand  {

	public GETEntityPassengerWithFlightEmbedded() {
	}
	
	/* Implement InteractionCommand interface */
	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		assert(ctx != null);
		
		// GET the Passenger (use Entity to test the AtomEntityEntryProvider
		EntityProperties pProperties = new EntityProperties();
		pProperties.setProperty(new EntityProperty("name", "Big Ron"));
		pProperties.setProperty(new EntityProperty("flightID", "2629"));
		Entity pEntity = new Entity("Passenger", pProperties);
		ctx.setResource(new EntityResource<Entity>(pEntity));

		// find the Flight Transition
		ResourceState passenger_flight = null;
		Collection<ResourceState> targets = ctx.getCurrentState().getAllTargets();
		for (ResourceState target : targets) {
			if (target.getName().equals("passenger_flight")) {
				passenger_flight = target;
				break;
			}
		}
		Transition pfTranstion = ctx.getCurrentState().getTransition(passenger_flight);
		
		// embed the Flight
		EntityProperties fProperties = new EntityProperties();
		fProperties.setProperty(new EntityProperty("flightID", 2629));
		Entity fEntity = new Entity("Flight", fProperties);
		Map<Transition, RESTResource> embeddedResources = new HashMap<Transition, RESTResource>();
		embeddedResources.put(pfTranstion, new EntityResource<Entity>(fEntity));
		ctx.getResource().setEmbedded(embeddedResources);
		
		return Result.SUCCESS;
	}

}
