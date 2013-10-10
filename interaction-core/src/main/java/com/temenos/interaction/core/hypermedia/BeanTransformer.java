package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements transformations from regular Java beans (POJOs)
 * @see {@link Transformer}
 * @author aphethean
 */
public class BeanTransformer implements Transformer {
	private final Logger logger = LoggerFactory.getLogger(BeanTransformer.class);
	
	static class ReservedProperty {
		private static final String[] ALL_RESERVED_PROPERTIES = {"CLASS"};
		
		private ReservedProperty() {}
		public static boolean contains(String value) {
			for (String s : ALL_RESERVED_PROPERTIES) {
				if (s.equalsIgnoreCase(value)) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * @precondition entity not null
	 */
	@Override
	public Map<String, Object> transform(Object entity) {
		assert(entity != null);
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(entity.getClass());
			for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
			    String propertyName = propertyDesc.getName();
			    if (!ReservedProperty.contains(propertyName)) {
				    Object value = propertyDesc.getReadMethod().invoke(entity);
					map.put(propertyName, value);				
			    }
			}
		} catch (IllegalArgumentException e) {
			logger.error("Error accessing bean property", e);
		} catch (IntrospectionException e) {
			logger.error("Error accessing bean property", e);
		} catch (IllegalAccessException e) {
			logger.error("Error accessing bean property", e);
		} catch (InvocationTargetException e) {
			logger.error("Error accessing bean property", e);
		}
		return map;
	}

	/**
	 * This transformer will accept any object and push its properties
	 * {@see PropertyDescriptor} into the returned Map
	 */
	@Override
	public boolean canTransform(Object entity) {
		if (entity != null) {
			return true;
		}
		return false;
	}
}
