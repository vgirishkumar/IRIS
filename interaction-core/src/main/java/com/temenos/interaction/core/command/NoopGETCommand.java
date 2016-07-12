package com.temenos.interaction.core.command;

/*
 * #%L
 * interaction-core
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


import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;

/**
 * A GET command that does nothing.  Can be useful for laying out a straw
 * man of resources and not needing to implement them all initially.
 * @author aphethean
 */
public final class NoopGETCommand implements InteractionCommand {

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		RESTResource resource = ctx.getResource();
		if(resource != null) {
			ctx.setResource(resource);
		}
		else {
			ctx.setResource(new EntityResource<Object>());
		}
		
	    
	    for (Entry<String, List<String>> entry : ctx.getQueryParameters().entrySet()) {
	        StringBuilder valueString = new StringBuilder();
	        Iterator<String> iter = entry.getValue().iterator();
	        while (iter.hasNext()) {
	            valueString.append(iter.next());
	            if (iter.hasNext()) {
	                valueString.append(",");
	            }
	        }
	        
	        ctx.getOutQueryParameters().put(entry.getKey(), valueString.toString());
	    }
		
		return Result.SUCCESS;
	}

}
