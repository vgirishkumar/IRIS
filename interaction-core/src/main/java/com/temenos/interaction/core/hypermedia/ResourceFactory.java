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


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple reflection based ResourceFactory that is used by the generated Behaviour
 * class to construct runtime instances of our resources.
 * @author aphethean
 */
public class ResourceFactory {
	private final static Logger logger = LoggerFactory.getLogger(ResourceFactory.class);

	private Map<String, ResourceState> resources = new HashMap<String, ResourceState>();
	private ResourceLocatorProvider resourceLocatorProvider;
	
	@SuppressWarnings("unchecked")
	public ResourceState getResourceState(String name) {
		ResourceState rs = resources.get(name);
		if (rs == null) {
			try {
				Class<ResourceState> rsClass = (Class<ResourceState>) Class.forName(name + "ResourceState");
				Constructor<ResourceState> rsCtr = rsClass.getConstructor(ResourceFactory.class);
				rs = rsCtr.newInstance(this);
				resources.put(name, rs);
				// now initialise
				if (rs instanceof LazyResourceLoader) {
					((LazyResourceLoader) rs).initialise();
				}
			} catch (ClassNotFoundException e) {
				logger.error("An error occurred constructing ResourceState " + name, e);
			} catch (InstantiationException e) {
				logger.error("An error occurred constructing ResourceState " + name, e);
			} catch (IllegalAccessException e) {
				logger.error("An error occurred constructing ResourceState " + name, e);
			} catch (NoSuchMethodException e) {
				logger.error("An error occurred constructing ResourceState " + name, e);
			} catch (IllegalArgumentException e) {
				logger.error("An error occurred constructing ResourceState " + name, e);
			} catch (InvocationTargetException e) {
				logger.error("An error occurred constructing ResourceState " + name, e);
			}
			
		}
		
		return rs;
	}

	/**
	 * @return the resourceLocatorProvider
	 */
	public ResourceLocatorProvider getResourceLocatorProvider() {
		return resourceLocatorProvider;
	}

	/**
	 * @param resourceLocatorProvider the resourceLocatorProvider to set
	 */
	public void setResourceLocatorProvider(ResourceLocatorProvider resourceLocatorProvider) {
		this.resourceLocatorProvider = resourceLocatorProvider;
	}
	
	
	
}
