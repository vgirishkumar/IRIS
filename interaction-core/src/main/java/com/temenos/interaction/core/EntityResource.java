package com.temenos.interaction.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.odata4j.core.OEntity;
import org.odata4j.producer.exceptions.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An EntityResource is the RESTful representation of a 'thing' within our
 * system.  A 'thing' is addressable by a globally unique key, it has a 
 * set of name value pairs, and a set of links to find other resources
 * linked to this resource.
 * @author aphethean
 */
//@XmlRootElement(name = "resource")
//@XmlAccessorType(XmlAccessType.FIELD)
public class EntityResource implements RESTResource {
	@XmlTransient
	private final Logger logger = LoggerFactory.getLogger(EntityResource.class);

	@XmlAnyElement(lax=true)
	private Object entity;
	
	// keep JAXB happy
	public EntityResource() {}

	// TODO use Generics here
	public Object getEntity() {
		return entity;
	}
	
	public OEntity getOEntity() {
		if (entity != null) {
			logger.debug("Discovered a jaxb / json deserialised object");
		}
		// TODO implement a generic jaxb to OEntity conversion for our 'entity'
		throw new NotImplementedException();
	};
// not used?, see OEntity.getLinks	public Set<OLink> getLinks();

}
