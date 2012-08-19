package com.temenos.interaction.core.resource;

import java.util.Collection;

import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.temenos.interaction.core.hypermedia.Link;

/**
 * A MetaDataResource is resource that describes another resource.
 * @author aphethean
 */
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaDataResource<T> extends EntityResource<T> {
	@XmlAnyElement(lax=true)
	private T metadata; 

	public MetaDataResource(T metadata) {
		this.metadata = metadata;
	}
	
	public T getMetadata() {
		return metadata;
	}
	
	@Override
	public GenericEntity<EntityResource<T>> getGenericEntity() {
		return new GenericEntity<EntityResource<T>>(this, this.getClass().getGenericSuperclass());
	}
	
	@Override
    public Collection<Link> getLinks() {
    	return null;
    }
	@Override
	public void setLinks(Collection<Link> links) {}

	@Override
	public String getEntityName() {
		return null;
	}
	@Override
	public void setEntityName(String entityName) {}
	
}
