package com.temenos.interaction.springdsl.properties;

/*
 * #%L
 * interaction-springdsl
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * A properties factory bean that creates a reconfigurable Properties object.
 * When the Properties' reloadConfiguration method is called, and the file has
 * changed, the properties are read again from the file. 
 * Credit to: http://www.wuenschenswert.net/wunschdenken/archives/127
 */
public class ReloadablePropertiesFactoryBean extends PropertiesFactoryBean implements DisposableBean, ApplicationContextAware {
	private ApplicationContext ctx;

	private Map<Resource,Long> locations = new HashMap<Resource,Long>();
	private List<ReloadablePropertiesListener> preListeners;
	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
	
	public void setListeners(List<ReloadablePropertiesListener> listeners) {
		// early type check, and avoid aliassing
		this.preListeners = new ArrayList<ReloadablePropertiesListener>();
		for (Object o : listeners) {
			preListeners.add((ReloadablePropertiesListener) o);
		}
	}

	private ReloadablePropertiesBase reloadableProperties;

	protected Object createInstance() throws IOException {
		// would like to uninherit from AbstractFactoryBean (but it's final!)
		if (!isSingleton())
			throw new RuntimeException("ReloadablePropertiesFactoryBean only works as singleton");
		reloadableProperties = new ReloadablePropertiesImpl();
		if (preListeners != null)
			reloadableProperties.setListeners(preListeners);
		reload(true);
		return reloadableProperties;
	}

	public void destroy() throws Exception {
		reloadableProperties = null;
	}

	protected void reload(boolean forceReload) throws IOException {
		Resource[] tmpLocations = ctx.getResources("classpath*:IRIS-*.properties");
		
		boolean reload = forceReload;
		
		if(locations == null) {
			// Uninitalized - Load everything
			locations = new HashMap<Resource, Long>();
			
			for(Resource location : tmpLocations) {
				addNewLocation(location);
				
				reload = true;
			}
		} else {
			// Process new and modified
			for(Resource location : tmpLocations) {
				
				if(locations.containsKey(location)) {
					// Existing location
					File file = new File(location.getURL().getFile());
					long lastModified = file.lastModified();
					
					if( lastModified > locations.get(location)) {
						// Identified modification
						
						// Update entry in locations
						locations.put(location, lastModified);						
						
						// Load properties file						
						Properties newProperties = new Properties();
						propertiesPersister.load(newProperties, location.getInputStream());
						
						// Notify subscribers that properties have been modified
						reloadableProperties.notifyPropertiesChanged(location, newProperties);
						
						reload = true;
					}
				} else {
					// New location
					addNewLocation(location);
					
					reload = true;					
				}
			}						
		}
		
		// Set locations on parent ready for merging of properties with overrides
		super.setLocations(tmpLocations);
		
		// TODO Handle removing states
		
		if (reload)
			doReload();
	}

	/**
	 * @param location
	 * @throws IOException
	 */
	private void addNewLocation(Resource location) throws IOException {
		// Add entry to locations
		File file = new File(location.getURL().getFile());
		locations.put(location, file.lastModified());
		
		// Load properties file
		Properties newProperties = new Properties();
		propertiesPersister.load(newProperties, location.getInputStream());	
		
		// Notify subscribers that new properties have been loaded
		reloadableProperties.notifyPropertiesLoaded(location, newProperties);
	}

	private void doReload() throws IOException {
		reloadableProperties.setProperties(mergeProperties());
	}

	@SuppressWarnings("unchecked")
	class ReloadablePropertiesImpl extends ReloadablePropertiesBase implements ReconfigurableBean {
		private static final long serialVersionUID = -3401718333944329073L;

		public void reloadConfiguration() throws Exception {			
			ReloadablePropertiesFactoryBean.this.reload(false);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;		
	}
}
