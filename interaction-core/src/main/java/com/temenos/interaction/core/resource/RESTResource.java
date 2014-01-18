package com.temenos.interaction.core.resource;

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

import javax.ws.rs.core.GenericEntity;

import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.Transition;

/**
 * A RESTResource is the base interface for all types of resources.
 * 
 * @author aphethean
 */
public interface RESTResource {

	/**
	 * Wrap this resource into a JAX-RS GenericEntity object
	 * @return GenericEntity object
	 */
	public GenericEntity<?> getGenericEntity();
	
	/**
	 * Return the links from this resource to another.
	 * @return Collection<Link>
	 */
    public Collection<Link> getLinks();

    /**
     * Called during resource building phase to set the links for
     * serialization by the provider.
     * @param links
     */
    public void setLinks(Collection<Link> links);

    /**
     * Called during resource building phase to set the embedded
     * resources for serialization by the provider.
     * @param embedded
     */
    public void setEmbedded(Map<Transition, RESTResource> embedded);

	/**
	 * Return the embedded resources.
	 * @return Map<Transition, RESTResource>
	 */
    public Map<Transition, RESTResource> getEmbedded();
    
	/**
	 * Return the entity name for this resource.
	 * @return String
	 */
    public String getEntityName();

    /**
     * Called during resource building phase to set the entity name for 
     * use by the provider.
     * @param links
     */
    public void setEntityName(String entityName);

	/**
	 * Return the entity tag for this resource.
	 * @return etag
	 */
    public String getEntityTag();

    /**
     * Set the entity tag for this resource.
     * @param etag
     */
    public void setEntityTag(String entityTag);
}  