package com.temenos.interaction.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.producer.exceptions.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ServiceDocumentResource is a resource that lists other resources to aid
 * discovery of available resources.
 */
@XmlRootElement(name = "servicedocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceDocumentResource implements RESTResource {
	@XmlTransient
	private final Logger logger = LoggerFactory.getLogger(ServiceDocumentResource.class);

	@XmlAnyElement(lax=true)
	private Object serviceDocument; 

	@XmlTransient
	private EdmDataServices edmx; 
	
	/**
	 * Construct an instance for the specified service document
	 * @param serviceDocument Service document
	 */
	public ServiceDocumentResource(Object serviceDocument) {
		this.serviceDocument = serviceDocument;
	}
	
	/**
	 * Construct an instance with the specified OData EDM definition.
	 * @param edmx OData EDM definition
	 */
	public ServiceDocumentResource(EdmDataServices edmx) {
		this.edmx = edmx;
	}
	
	/**
	 * Returns the Service document
	 * @return Service document
	 */
	public Object getServiceDocument() {
		return serviceDocument;
	}
	
	/**
	 * Returns the EDM definition
	 * @return EDM definition
	 */
	public EdmDataServices getEdmx() {
		if (edmx != null) return edmx;
		if (serviceDocument != null) {
			logger.debug("Discovered a jaxb / json deserialised object");
			// TODO implement a generic jaxb to OEntity conversion for our 'entity'
			/*
			 * TODO implement a generic jaxb to OEntities conversion for our 
			 * 'entities' or throw an error to change the runtime configuration 
			 * to represent this object with a JAXB Provider
			 */
			throw new NotImplementedException();
		}
		throw new RuntimeException("Service document has not been be supplied");
	}
	
}
