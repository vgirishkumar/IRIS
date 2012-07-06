package com.temenos.interaction.commands.odata;

import java.util.HashMap;
import java.util.Map;

import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.hypermedia.Transformer;

/**
 * Implements transformations from regular Java beans (POJOs)
 * @see {@link Transformer}
 * @author aphethean
 */
public class OEntityTransformer implements Transformer {
	private final Logger logger = LoggerFactory.getLogger(OEntityTransformer.class);
	
	/**
	 * @precondition entity not null
	 */
	@Override
	public Map<String, Object> transform(Object entity) {
		assert(entity != null);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			OEntity oentity = (OEntity) entity;
			for (OProperty<?> property : oentity.getProperties()) {
				map.put(property.getName(), property.getValue());				
			}
		} catch (RuntimeException e) {
			logger.error("Error transforming OEntity to map", e);
			throw e;
		}
		return map;
	}

}
