package com.temenos.interaction.core.resource;

import java.util.Collection;

import javax.ws.rs.core.GenericEntity;

import com.temenos.interaction.core.link.Link;

/**
 * A RESTResource is the base interface for all types of resources.
 * 
 * @author aphethean
 */
public interface RESTResource {
	/**
	 * Return the name of the entity.
	 * @return String entityName
	 */
//	public String getEntityName();

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

}  