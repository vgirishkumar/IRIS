package com.temenos.interaction.core.resource;

import java.util.Collection;

import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.temenos.interaction.core.link.Link;

/**
 * An EntityResource is the RESTful representation of a 'thing' within our
 * system.  A 'thing' is addressable by a globally unique key, it has a set of
 * simple & complex named properties, and a set of links to find other resources
 * linked to this resource.
 * @author aphethean
 */
@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityResource<T> implements RESTResource {
	@XmlAnyElement(lax=true)
	private T entity;

	/* injected by HateoasResponse */
	@XmlTransient
    private Collection<Link> links;

	public EntityResource() {
	}
	
	public EntityResource(T entity) {
		this.entity = entity;
	}

	public T getEntity() {
		return entity;
	}

	@Override
	public GenericEntity<EntityResource<T>> getGenericEntity() {
		return new GenericEntity<EntityResource<T>>(this, this.getClass().getGenericSuperclass());
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
}
