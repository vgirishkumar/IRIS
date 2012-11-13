package com.temenos.interaction.commands.odata;

import com.temenos.interaction.core.hypermedia.UriSpecification;

public class ODataUriSpecification {

	public final static String NAME = "ODataUriSpecification";
	
	public final static String ENTITY_URI_TYPE = "Entity";
	public final static String ENTITYSET_URI_TYPE = "EntitySet";
	public final static String NAVPROPERTY_URI_TYPE = "NavProperty";
	
	public UriSpecification getTemplate(String resourcePath) {
		return new UriSpecification(resourcePath, resourcePath);
	}
	
	public UriSpecification getTemplate(String resourcePathPrefix, String type) {
		assert(type != null);  // would be a bug in IRIS core
		if (type.equals(ENTITY_URI_TYPE)) {
			return new UriSpecification(resourcePathPrefix, resourcePathPrefix + "({id})");
		} else if (type.equals(ENTITYSET_URI_TYPE)) {
			return new UriSpecification(resourcePathPrefix, resourcePathPrefix);
		} else if (type.equals(NAVPROPERTY_URI_TYPE)) {
			return new UriSpecification(resourcePathPrefix, resourcePathPrefix + "({id})/{navproperty}");
		}
		throw new IllegalArgumentException("Could not produce template for supplied URI type [" + type + "]");
	}

	public String toString() {
		return NAME;
	}
	
}
