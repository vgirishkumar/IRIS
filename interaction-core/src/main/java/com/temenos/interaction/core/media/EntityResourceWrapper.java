package com.temenos.interaction.core.media;

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


import java.util.Collection;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.EntityResource;

/**
 * Entity resource wrapper classes which exposes a link to itself. 
 */
public class EntityResourceWrapper {
	protected EntityResource<Map<String, Object>> entityResource;
	protected Link entityGetLink;
	protected Link entityUpdateLink;
	
	public EntityResourceWrapper(EntityResource<Map<String, Object>> entityResource) {
		this.entityResource = entityResource;
		entityGetLink = findEntityGetLink(entityResource.getLinks());
		entityUpdateLink = findEntityGetLink(entityResource.getLinks());
	}
	
	public EntityResource<Map<String, Object>> getResource() {
		return entityResource;
	}

	public Link getEntityGetLink() {
		return entityGetLink;
	}

	public Link getEntityUpdateLink() {
		return entityUpdateLink;
	}

	/**
	 * Get the link to the entity resource state.
	 * @param links links
	 * @return link to entity state
	 */
	protected Link findEntityGetLink(Collection<Link> links) {
		Link selfLink = null;
		if (links != null) {
			for (Link l : links) {
				Transition t = l.getTransition();
				if (l.getRel().equals("self") || t != null &&
						t.getCommand().getMethod().equals(HttpMethod.GET) &&
						t.getSource().getEntityName().equals(t.getTarget().getEntityName()) &&
						t.getSource().getRel().equals("collection") &&
						t.getTarget().getRel().equals("item")) {
					selfLink = l;
					break;
				}
			}
		}
		return selfLink;
	}
}
