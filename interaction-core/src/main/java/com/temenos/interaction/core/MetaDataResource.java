package com.temenos.interaction.core;

import java.util.Set;

import org.odata4j.core.OProperty;

/**
 * A MetaDataResource is resource that describes another resource.
 * @author aphethean
 */
public interface MetaDataResource extends RESTResource {

	public Set<OProperty<?>> getProperties();

}
