package com.temenos.interaction.core;

import java.util.Set;

import org.odata4j.core.OProperty;

/**
 * A MetaDataResource is resource that describes another resource.
 * @author aphethean
 */
public abstract class MetaDataResource implements RESTResource {

	public abstract Set<OProperty<?>> getProperties();

}
