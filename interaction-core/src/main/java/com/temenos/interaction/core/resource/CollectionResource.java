package com.temenos.interaction.core.resource;

import java.util.Collection;

import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlTransient;

import com.jayway.jaxrs.hateoas.HateoasLink;

/**
 * A CollectionResource is the RESTful representation of a collection of
 * 'things' within our system.  A 'thing' is addressable by a globally 
 * unique key, it has a set of simple & complex named properties, and a set 
 * of links to find other resources linked to this resource.
 * @author aphethean
 */
public class CollectionResource<T> implements RESTResource {
	private String entitySetName;
	
	private Collection<EntityResource<T>> entities;

	// TODO implement collection properties, used for things like inlinecount and skiptoken
	// TODO implement JAXB Adapter for OProperty
//	private List<OProperty<?>> properties;

	// links from a collection
	@XmlTransient
    private Collection<HateoasLink> links;
	
	public CollectionResource() {}

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
    public Collection<HateoasLink> getLinks() {
    	return this.links;
    }
    
    /**
     * Called during resource building phase to set the links for
     * serialization by the provider.
     * @param links
     */
	@Override
    public void setLinks(Collection<HateoasLink> links) {
    	this.links = links;
    }

}
