package com.temenos.interaction.core.hypermedia;

import java.util.HashMap;
import java.util.Map;

import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements transformations from Entity objects
 * @see {@link Transformer}
 */
public class EntityTransformer implements Transformer {
	private final Logger logger = LoggerFactory.getLogger(EntityTransformer.class);
	
	/**
	 * @precondition entity not null
	 */
	@Override
	public Map<String, Object> transform(Object entity) {
		if(entity instanceof OEntity) {
			return transform((OEntity) entity);
		}
		else {
			logger.error("Unable to transform entity: " + entity.toString());
			return null;
		}
	}
	
	private Map<String, Object> transform(OEntity entity) {
		assert(entity != null);
		Map<String, Object> map = new HashMap<String, Object>();
		for(OProperty<?> prop : entity.getProperties()) {
			String name = prop.getName();
			Object value = prop.getValue();
			map.put(name, value);				
		}
		return map;
	}
}
