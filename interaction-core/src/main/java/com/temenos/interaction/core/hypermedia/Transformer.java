package com.temenos.interaction.core.hypermedia;

import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

/**
 * A transformer is used to map an entity to a form usable by
 * a {@link UriBuilder} or an interaction {@link Provider}
 * @author aphethean
 */
public interface Transformer {

	/**
	 * Transform an entity to a map where the key is the target
	 * element name and the value is the source entity element.
	 * @param entity
	 * @return
	 */
	public Map<String, Object> transform(Object entity);
	
}
