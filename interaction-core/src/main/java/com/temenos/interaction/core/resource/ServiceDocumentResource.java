package com.temenos.interaction.core.resource;

import java.util.Collection;

import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.temenos.interaction.core.link.Link;

/**
 * A ServiceDocumentResource is a resource that lists other resources to aid
 * discovery of available resources.
 */
@XmlRootElement(name = "servicedocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceDocumentResource<T> implements RESTResource {

	@XmlAnyElement(lax=true)
	private T serviceDocument; 

	/**
	 * Construct an instance for the specified service document
	 * @param serviceDocument Service document
	 */
	public ServiceDocumentResource(T serviceDocument) {
		this.serviceDocument = serviceDocument;
	}
	
	/**
	 * Returns the Service document
	 * @return Service document
	 */
	public T getServiceDocument() {
		return serviceDocument;
	}
	
	@Override
	public GenericEntity<ServiceDocumentResource<T>> getGenericEntity() {
		return new GenericEntity<ServiceDocumentResource<T>>(this, this.getClass().getGenericSuperclass());
	}
	
	@Override
    public Collection<Link> getLinks() {
    	return null;
    }
	public void setLinks(Collection<Link> links) {}

}
