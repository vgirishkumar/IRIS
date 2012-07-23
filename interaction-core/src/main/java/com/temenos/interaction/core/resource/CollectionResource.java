package com.temenos.interaction.core.resource;

import java.util.Collection;

import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlTransient;

import com.temenos.interaction.core.hypermedia.Link;

/**
 * A CollectionResource is the RESTful representation of a collection of
 * 'things' within our system.  A 'thing' is addressable by a globally 
 * unique key, it has a set of simple & complex named properties, and a set 
 * of links to find other resources linked to this resource.
 * @author aphethean
 */
public class CollectionResource<T> implements RESTResource {
	
	// TODO deprecate (we now use entityName)
	private String entitySetName;
	
	private Collection<EntityResource<T>> entities;

	// TODO implement collection properties, used for things like inlinecount and skiptoken
	// TODO implement JAXB Adapter for OProperty
//	private List<OProperty<?>> properties;

	@XmlTransient
    private String entityName;
	// links from a collection
	@XmlTransient
    private Collection<Link> links;
	
	public CollectionResource() {}

	/**
	 * Construct a new instance of a CollectionResource.  EntitySetName will be set by the interaction-core
	 * before passing to a Provider
	 * @param entities
	 */
	public CollectionResource(Collection<EntityResource<T>> entities) {
		this.entities = entities;
	}

	/**
	 * This constructor expected to be used internally.
	 * @param entitySetName
	 * @param entities
	 */
	public CollectionResource(String entitySetName, Collection<EntityResource<T>> entities) {
		this.entitySetName = entitySetName;
		this.entities = entities;
	}

	public String getEntitySetName() {
		return entitySetName;
	}
	
	public Collection<EntityResource<T>> getEntities() {
		return entities;
	}
	
	@Override
	public GenericEntity<CollectionResource<T>> getGenericEntity() {
		return new GenericEntity<CollectionResource<T>>(this, this.getClass().getGenericSuperclass());
	}

	@Override
    public Collection<Link> getLinks() {
    	return this.links;
    }
    
    /**
     * Called during resource building phase to set the links for
     * serialization by the provider.
     * @param links
     */
	@Override
    public void setLinks(Collection<Link> links) {
    	this.links = links;
    }

	@Override
	public String getEntityName() {
		return entityName;
	}

	@Override
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

}
