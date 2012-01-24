package com.temenos.interaction.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;
import org.odata4j.producer.exceptions.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A CollectionResource is the RESTful representation of a collection of
 * 'things' within our system.  A 'thing' is addressable by a globally 
 * unique key, it has a set of simple & complex named properties, and a set 
 * of links to find other resources linked to this resource.
 * @author aphethean
 */
@XmlRootElement(name = "collection-resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionResource implements RESTResource {
	@XmlTransient
	private final Logger logger = LoggerFactory.getLogger(CollectionResource.class);

	@XmlTransient
	private String entitySetName;
	
	@XmlAnyElement(lax=true)
	private Collection<Object> entities;

	@XmlTransient
	private List<OEntity> oEntities;
	// TODO implement collection properties, used for things like inlinecount and skiptoken
	// TODO implement JAXB Adapter for OProperty
	@XmlTransient
	private List<OProperty<?>> properties;

	// TODO implement links from a collection
	@XmlTransient
	private Set<OLink> links;
	
	public CollectionResource() {}

	public CollectionResource(String entitySetName, List<OEntity> entities, List<OProperty<?>> properties) {
		this.entitySetName = entitySetName;
		this.oEntities = entities;
		this.properties = properties;		
	}

	public CollectionResource(String entitySetName, Collection<Object> entities, List<OProperty<?>> properties) {
		this.entitySetName = entitySetName;
		this.entities = entities;
		this.properties = properties;		
	}

	public String getEntitySetName() {
		return entitySetName;
	}
	
	// TODO use Generics here
	public Collection<Object> getEntities() {
		return entities;
	}
	
	public List<OEntity> getOEntities() {
		if (oEntities != null) return oEntities;
		if (entities != null) {
			logger.debug("Discovered a jaxb / json deserialised object");
			/*
			 * TODO implement a generic jaxb to OEntities conversion for our 
			 * 'entities' or throw an error to change the runtime configuration 
			 * to represent this object with a JAXB Provider
			 */
			throw new NotImplementedException();
		}
		throw new RuntimeException("Either oEntities or entities must be supplied");
	}

}
