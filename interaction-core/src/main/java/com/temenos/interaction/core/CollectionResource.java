package com.temenos.interaction.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;

/**
 * A CollectionResource is the RESTful representation of a collection of
 * 'things' within our system.  A 'thing' is addressable by a globally 
 * unique key, it has a set of simple & complex named properties, and a set 
 * of links to find other resources linked to this resource.
 * @author aphethean
 */
@XmlRootElement(name = "collection-resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionResource<T> implements RESTResource {
	@XmlTransient
	private String entitySetName;
	
	@XmlAnyElement(lax=true)
	private Collection<T> entities;

	// TODO implement collection properties, used for things like inlinecount and skiptoken
	// TODO implement JAXB Adapter for OProperty
	@SuppressWarnings("unused")
	@XmlTransient
	private List<OProperty<?>> properties;

	// TODO implement links from a collection
	@SuppressWarnings("unused")
	@XmlTransient
	private Set<OLink> links;
	
	public CollectionResource() {}

	public CollectionResource(String entitySetName, Collection<T> entities, List<OProperty<?>> properties) {
		this.entitySetName = entitySetName;
		this.entities = entities;
		this.properties = properties;		
	}

	public String getEntitySetName() {
		return entitySetName;
	}
	
	public Collection<T> getEntities() {
		return entities;
	}
	
	@Override
	public GenericEntity<CollectionResource<T>> getGenericEntity() {
		return new GenericEntity<CollectionResource<T>>(this, this.getClass().getGenericSuperclass());
	}
}
