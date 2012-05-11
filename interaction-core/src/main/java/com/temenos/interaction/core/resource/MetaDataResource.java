package com.temenos.interaction.core.resource;

import java.util.Collection;

import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.jayway.jaxrs.hateoas.HateoasLink;

/**
 * A MetaDataResource is resource that describes another resource.
 * @author aphethean
 */
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaDataResource<T> implements RESTResource {
	@XmlAnyElement(lax=true)
	private T metadata; 

	public MetaDataResource(T metadata) {
		this.metadata = metadata;
	}
	
	public T getMetadata() {
		return metadata;
	}
	
	@Override
	public GenericEntity<MetaDataResource<T>> getGenericEntity() {
		return new GenericEntity<MetaDataResource<T>>(this, this.getClass().getGenericSuperclass());
	}
	
	@Override
    public Collection<HateoasLink> getLinks() {
    	return null;
    }

}
