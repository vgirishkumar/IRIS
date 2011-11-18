package com.temenos.interaction.core;

import java.util.Set;

import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;


/**
 * An EntityResource is the RESTful representation of a 'thing' within our
 * system.  A 'thing' is addressable by a globally unique key, it has a 
 * set of name value pairs, and a set of links to find other resources
 * linked to this resource.
 * @author aphethean
 */
public interface EntityResource extends RESTResource {

	public OEntity getEntity();
	public Set<OLink> getLinks();

}
