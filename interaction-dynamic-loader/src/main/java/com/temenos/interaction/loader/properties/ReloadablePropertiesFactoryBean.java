package com.temenos.interaction.loader.properties;

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
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.core.io.UrlResource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import com.temenos.interaction.springdsl.DynamicProperties;

/**
 * A properties factory bean that creates a reconfigurable Properties object.
 * When the Properties' reloadConfiguration method is called, and the file has
 * changed, the properties are read again from the file. Credit to:
 * http://www.wuenschenswert.net/wunschdenken/archives/127
 */
public class ReloadablePropertiesFactoryBean extends PropertiesFactoryBean implements DynamicProperties,
		DisposableBean, ApplicationContextAware {
	private ApplicationContext ctx;

	private Map<Resource, Long> locations;
	private List<ReloadablePropertiesListener> preListeners;
	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
	private ReloadablePropertiesBase reloadableProperties;
	private Properties properties;
	private long lastCheck = 0;
	private List<Resource> resourcesPath = null;
	private File lastChangeFile = null;

	public void setListeners(List<ReloadablePropertiesListener> listeners) {
		// early type check, and avoid aliassing
		this.preListeners = new ArrayList<ReloadablePropertiesListener>();
		for (ReloadablePropertiesListener l : listeners) {
			preListeners.add(l);
		}
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	protected Object createInstance() throws IOException {
		// would like to uninherit from AbstractFactoryBean (but it's final!)
		if (!isSingleton())
			throw new RuntimeException("ReloadablePropertiesFactoryBean only works as singleton");
		reloadableProperties = new ReloadablePropertiesImpl();
		reloadableProperties.setProperties(properties);
		
		if (preListeners != null)
			reloadableProperties.setListeners(preListeners);
		reload(true);
		return reloadableProperties;
	}

	public void destroy() throws Exception {
		reloadableProperties = null;
	}

	protected void getMoreRecentThan(File root, final long timestamp, final List<Resource> resources,
			final List<simplePattern> patterns) {
		File file = root;// new File(root.getURL().getFile());

		file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					getMoreRecentThan(pathname, timestamp, resources, patterns);
				} else {
					if (pathname.lastModified() > timestamp) {
						for (simplePattern pattern : patterns) {
							if (pattern.matches(pathname.getName())) {
								try {
									resources.add(new UrlResource(pathname.toURI()));
								} catch (MalformedURLException e) {
									logger.error("MalformedURL for " + pathname.getAbsolutePath());
								}
							}
						}

					}
				}
				return false;
			}
		});

	}

	
	
	protected void reload(boolean forceReload) throws IOException {
		long l = System.currentTimeMillis();
		boolean oldReload = System.getProperty("old.reload") != null;
		if (oldReload){
			reload_old(forceReload);
		}else{
			reload_new(forceReload);
		}
		l = System.currentTimeMillis() -l;
		logger.info("Reload time " + (oldReload?"(old) : ":"(new) : ") + l + " ms.");
	}
	
	private List<Resource> initializeResourcesPath() throws IOException {
		List<Resource> ret = new ArrayList<Resource>();
		List<Resource> tmp = Arrays.asList(ctx.getResources("classpath*:"));
		for (Resource oneResource : tmp){
			String sPath = oneResource.getURI().getPath().replace('\\', '/');
			int pos = sPath.indexOf("models-gen/src/generated/iris");
			if (pos > 0){
				ret.add(oneResource);
				/*
				 * now let's look at the lastChange file
				 */
				String sRoot = sPath.substring(0, pos + "models-gen".length());
				File f = new File(sRoot, "lastChange");
				if (f.exists()){
					lastChangeFile = f;
				}
			}
		}
		return ret;
		
	}
	
	
	protected void reload_new(boolean forceReload) throws IOException {


		if (resourcesPath == null){
			/*
			 * initiate it once for all.
			 */
			resourcesPath = initializeResourcesPath();
		}
		
		if (forceReload){
			/*
			 * Take all file independently of the timestamp.
			 */
			lastCheck = 0;
		}


		if (lastChangeFile != null && lastChangeFile.exists()){
			long lastChange = lastChangeFile.lastModified();
			if (lastChange < lastCheck){
				return;
			}
		}

		/*
		 * Let's do it as we could miss a file being modified during the scan.
		 */
		long tmpLastCheck = lastCheck;
		/*
		 * Some file systems (FAT, NTFS) do have a write time resolution of 2
		 * seconds see
		 * http://msdn.microsoft.com/en-us/library/ms724290%28VS.85%29.aspx So
		 * better give a 4 seconds latency.
		 */
		lastCheck = System.currentTimeMillis() - 4000;

		
		List<Resource> changedPaths = new ArrayList<Resource>();
		List<simplePattern> lstPatterns = new ArrayList<simplePattern>();
		for (ReloadablePropertiesListener listener : preListeners) {
			String[] sPatterns = listener.getResourcePatterns();
			for (String pattern : sPatterns) {
				lstPatterns.add(new simplePattern(pattern));
			}
		}
		for (Resource res : resourcesPath) {
			getMoreRecentThan(new File(res.getURL().getFile()), tmpLastCheck, changedPaths, lstPatterns);
		}

		for (Resource location : changedPaths) {
			Properties newProperties = new Properties();
			propertiesPersister.load(newProperties, location.getInputStream());

			// Update (merge in) the new properties
			if (reloadableProperties.updateProperties(newProperties)) {
				reloadableProperties.notifyPropertiesLoaded(location, newProperties);
			} else {
				logger.info("Refreshing : " + location.getFilename());
				// Notify subscribers that properties have been modified
				reloadableProperties.notifyPropertiesChanged(location, newProperties);
			}
		}

	}
	
	protected void reload_old(boolean forceReload) throws IOException {
		List<Resource> tmpLocations = new ArrayList<Resource>();
		
		for (ReloadablePropertiesListener listener : preListeners) {
			String[] patterns = listener.getResourcePatterns();
			
			for(String pattern: patterns) {
				tmpLocations.addAll(Arrays.asList(ctx.getResources(pattern)));	
			}			
		}
		
		boolean reload = forceReload;
		
		if(locations == null) {
			// Uninitialized - Load everything
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
		super.setLocations(tmpLocations.toArray(new Resource[0]));
		
		// TODO Handle removing states
		
		if (reload) {
			doReload();
			logger.info("Finished Refreshing IRIS");	
		}
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

	class simplePattern {
		private final String startsWith;
		private final String endsWith;

		private simplePattern(String pattern) {
			pattern = pattern.replace("classpath*:", "");
			int idx = pattern.indexOf("*");
			startsWith = pattern.substring(0, idx);
			endsWith = pattern.substring(idx + 1);
		}

		private boolean matches(String s) {
			return s.startsWith(startsWith) && s.endsWith(endsWith);
		}
	}

}
