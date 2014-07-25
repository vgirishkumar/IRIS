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
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * A properties factory bean that creates a reconfigurable Properties object.
 * When the Properties' reloadConfiguration method is called, and the file has
 * changed, the properties are read again from the file. 
 * Credit to: http://www.wuenschenswert.net/wunschdenken/archives/127
 */
public class ReloadablePropertiesFactoryBean extends PropertiesFactoryBean implements DisposableBean {

	private Log log = LogFactory.getLog(getClass());

	// add missing getter for locations

	private Resource[] locations;
	private long[] lastModified;
	private List<ReloadablePropertiesListener> preListeners;
	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

	public void setLocation(Resource location) {
		setLocations(new Resource[] { location });
	}

	public void setLocations(Resource[] locations) {
		this.locations = locations;
		lastModified = new long[locations.length];
		super.setLocations(locations);
	}

	protected Resource[] getLocations() {
		return locations;
	}

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
		boolean reload = forceReload;
		for (int i = 0; i < locations.length; i++) {
			Resource location = locations[i];
			File file;
			try {
				file = location.getFile();
			} catch (IOException e) {
				// not a file resource
				continue;
			}
			try {
				long currentLastModified = file.lastModified();
				if (currentLastModified > lastModified[i]) {
					lastModified[i] = currentLastModified;
					Properties newProperties = new Properties();
					propertiesPersister.load(newProperties, location.getInputStream());
					if (currentLastModified == 0) {
						reloadableProperties.notifyPropertiesLoaded(location, newProperties);
					} else {
						reloadableProperties.notifyPropertiesChanged(location, newProperties);
					}
					reloadableProperties.notifyPropertiesChanged(newProperties);
					reload = true;
				}
			} catch (Exception e) {
				// cannot access file. assume unchanged.
				if (log.isDebugEnabled())
					log.debug("can't determine modification time of " + file + " for " + location, e);
			}
		}
		if (reload)
			doReload();
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

}
