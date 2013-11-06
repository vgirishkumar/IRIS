package com.interaction.example.odata.airline.commands;

/*
 * #%L
 * interaction-example-odata-airline
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;

public class GETEntityWithEtagCommand implements InteractionCommand {

	private InteractionCommand getEntityCommand;
	
	public GETEntityWithEtagCommand(InteractionCommand getEntityCommand) {
		this.getEntityCommand = getEntityCommand;
	}
	
	/* Implement InteractionCommand interface */
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		assert(ctx != null);
		Result result = getEntityCommand.execute(ctx);
		RESTResource resource = ctx.getResource();
		if(resource != null && resource.getEntityTag() == null) {
			EntityResource<OEntity> er = (EntityResource<OEntity>) ctx.getResource();
			resource.setEntityTag(getEtag(er.getEntity()));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public String getEtag(InteractionContext ctx) throws InteractionException {
		getEntityCommand.execute(ctx);
		RESTResource resource = ctx.getResource();
		if(resource != null && resource.getEntityTag() == null) {
			EntityResource<OEntity> er = (EntityResource<OEntity>) ctx.getResource();
			return getEtag(er.getEntity());
		}
		return null;
	}
	
	public String getEtag(OEntity entity) {
		String etag = "";
		List<OProperty<?>> props = new ArrayList<OProperty<?>>(entity.getProperties());
		Collections.sort(props, new Comparator<OProperty<?>>(){
		    public int compare(OProperty<?> p1, OProperty<?> p2) {
		        return p1.getName().compareToIgnoreCase(p2.getName());
		    }
		});
		for(OProperty<?> prop : props) {
			if(!etag.isEmpty()) {
				etag += ", ";
			}
			etag += prop.getName() + "=" + prop.getValue();
		}
		etag = entity.getEntitySetName() + "[" + etag + "]";
		return etag;
	}
}
