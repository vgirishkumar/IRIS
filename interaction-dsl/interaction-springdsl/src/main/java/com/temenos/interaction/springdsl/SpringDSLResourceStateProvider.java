package com.temenos.interaction.springdsl;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.temenos.interaction.core.hypermedia.Event;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateProvider;
import com.temenos.interaction.core.resource.ConfigLoader;

public class SpringDSLResourceStateProvider implements ResourceStateProvider, DynamicRegistrationResourceStateProvider {
	private final Logger logger = LoggerFactory.getLogger(SpringDSLResourceStateProvider.class);

	private ConcurrentMap<String, ResourceState> resources = new ConcurrentHashMap<String, ResourceState>();
	
	private StateRegisteration stateRegisteration;	

    /**
     * Map of ResourceState bean names, to paths.
     */
	private Properties beanMap;
	
	private boolean initialised = false;
	/**
	 * Map of paths to state names
	 */
	private Map<String, Set<String>> resourceStatesByPath = new HashMap<String, Set<String>>();
	/**
	 * Map of request to state names
	 */
	private Map<String, String> resourceStatesByRequest = new HashMap<String, String>();
	/**
	 * Map of resource methods where state name is the key
	 */
	private Map<String, Set<String>> resourceMethodsByState = new HashMap<String, Set<String>>();
	/**
	 * Map to a resource path where the state name is the key
	 */
	private Map<String, String> resourcePathsByState = new HashMap<String, String>();
	
	public SpringDSLResourceStateProvider() {}
	public SpringDSLResourceStateProvider(Properties beanMap) {
		this.beanMap = beanMap;
	}
	
	public void setResourceMap(Properties beanMap) {
		this.beanMap = beanMap;
	}
	
	private void initialise() {
		if (initialised)
			return;
		for (Object stateObj : beanMap.keySet()) {
			storeState(stateObj, null);
		}
		initialised = true;
	}
	
	private void storeState(Object stateObj, String binding){
		String stateName = stateObj.toString();
		// binding is [GET,PUT /thePath]
		if (binding == null){
			binding = beanMap.getProperty(stateName.toString());
		}
		// split into methods and path
		String[] strs = binding.split(" ");
		String methodPart = strs[0];
		String path = strs[1];
		// methods
		String[] methodsStrs = methodPart.split(",");
		// path
		resourcePathsByState.put(stateName, path);
		// methods
		Set<String> methods = resourceMethodsByState.get(stateName);
		if (methods == null) {
			methods = new HashSet<String>();
		}
		for (String method : methodsStrs) {
			methods.add(method);
		}
		resourceMethodsByState.put(stateName, methods);
		for (String method : methods) {
			String request = method + " " + path;
			logger.debug("Binding ["+stateName+"] to ["+request+"]");
			String found = resourceStatesByRequest.get(request);
			if (found != null) {
				logger.error("Multiple states bound to the same request ["+request+"], overriding ["+found+"] with ["+stateName+"]");
			}
			resourceStatesByRequest.put(request, stateName);
		}
		
		Set<String> stateNames = resourceStatesByPath.get(path);
		if (stateNames == null) {
			stateNames = new HashSet<String>();
		}
		stateNames.add(stateName.toString());
		resourceStatesByPath.put(path, stateNames);
		
	}
	
	public void addState(String stateObj, Properties properties) {
		if (initialised) {
			String stateName = stateObj.toString();

			// binding is [GET,PUT /thePath]
			String binding = properties.getProperty(stateName);

			// split into methods and path
			String[] strs = binding.split(" ");
			String methodPart = strs[0];
			String path = strs[1];

			// methods
			String[] methods = methodPart.split(",");

			logger.info("Attempting to register state: " + stateName + " methods: " + methods + " path: " + path
					+ " using: " + stateRegisteration);

			/*
			 * Thierry : Let's load it here. There is a lot of chance that if someone is 
			 * adding a new state, he will then browse it.
			 * This will speed up the first call to this resource.
			 */
			ResourceState state = getResourceState(stateName);
			if (state != null){
				storeState(stateName, binding);
				this.stateRegisteration.register(stateName, path, new HashSet<String>(Arrays.asList(methods)));
			}
		}
	}
	
	public void unload(String name) {
		resources.remove(name);
	}
	
	@Override
	public boolean isLoaded(String name) {
		return resources.containsKey(name);
	}

	@Override
	public ResourceState getResourceState(String resourceStateName) {
		ResourceState result = null;
		
		try {
			if (resourceStateName != null) {				
				// Try to retrieve the resource state
				result = resources.get(resourceStateName);
				
				if (result == null) {
					// Resource state has not already been loaded so attempt to load it					
					result = loadResourceStateFromFile(resourceStateName);
				}
			}
		} catch (BeansException e) {
			logger.error("Failed to load ["+resourceStateName+"]", e);
		}
		
		return result;
	}
	/**
	 * @param beanXml
	 * @return
	 */
	private ApplicationContext createApplicationContext(String beanXml) {
		ApplicationContext result = null;
		
		if(System.getProperty(ConfigLoader.IRIS_CONFIG_DIR_PROP) == null) {
			// Try and load the resource from the classpath
			result = new ClassPathXmlApplicationContext(new String[] {beanXml});
		} else {
			// Try and load the resource from the file system as a resource directory has been specified
			String irisResourceDirPath = System.getProperty(ConfigLoader.IRIS_CONFIG_DIR_PROP);
			File irisResourceDir = new File(irisResourceDirPath);
			
			if(irisResourceDir.exists() && irisResourceDir.isDirectory()) {
				File file = new File(irisResourceDir, beanXml);			
				
				if(file.exists()) {
					// Only attempt to create an application context if the file exists
					result = new FileSystemXmlApplicationContext( new String[] { file.getAbsolutePath() });
				}				
			}
		}
		
		return result;
	}
	
	private ResourceState loadResourceStateFromFile(String resourceStateName) {
		String tmpResourceStateName = resourceStateName;		
		String tmpResourceName = tmpResourceStateName;
		
		if(tmpResourceName.contains("-")) {
			tmpResourceName = tmpResourceName.substring(0, tmpResourceName.indexOf("-"));
		}
		
		String beanXml = "IRIS-" + tmpResourceName + "-PRD.xml";
		
		// Attempt to create Spring context based on current resource filename pattern
		ApplicationContext context = createApplicationContext(beanXml);
		
		if (context == null) {
			// Failed to create Spring context using current resource filename pattern so use old pattern
			int pos = tmpResourceName.lastIndexOf("_");
			
			if (pos > 3){
				tmpResourceName = tmpResourceName.substring(0, pos);
				beanXml = "IRIS-" + tmpResourceName + "-PRD.xml";
				
				context = createApplicationContext(beanXml);
				
				if (context != null) {
					// Successfully created Spring context using old resource filename pattern
					
					// Convert resource state name to old resource name format
					pos = tmpResourceStateName.lastIndexOf("-");
					
					if (pos < 0){
						pos = tmpResourceStateName.lastIndexOf("_");
						
						if (pos > 0){
							tmpResourceStateName = tmpResourceStateName.substring(0, pos) + "-" + tmpResourceStateName.substring(pos+1);
						}
					}						
				}else{
					logger.error("Unable to load resource ["+beanXml+"] not found");
				}
			}else{
				logger.error("Unable to load resource ["+beanXml+"] not found");
			}
		}
		
		ResourceState result = null;
		
		if(context != null) {
			result = loadAllResourceStatesFromFile(context, tmpResourceStateName);						
		}
		
		return result;
	}

	private ResourceState loadAllResourceStatesFromFile(ApplicationContext context, String resourceState) {
		Map<String,ResourceState> tmpResources = context.getBeansOfType(ResourceState.class);
		resources.putAll(tmpResources);
		
		ResourceState result = null;
		
		if(tmpResources.containsKey(resourceState)) {
			result = tmpResources.get(resourceState);
		} else {
			logger.error("Unable to resource state: " + resourceState);
		}
		
		return result;
	}

	@Override
	public ResourceState determineState(Event event, String resourcePath) {
		initialise();
		String request = event.getMethod() + " " + resourcePath;
		String stateName = resourceStatesByRequest.get(request);
		if (stateName != null){
			logger.debug("Found state ["+stateName+"] for ["+request+"]");
			return getResourceState(stateName);
		}else{
			logger.warn("NOT Found state ["+stateName+"] for ["+request+"]");
			return null;
		}
	}

	@Override
	public Map<String, Set<String>> getResourceStatesByPath() {
		initialise();
		return resourceStatesByPath;
	}

	public Map<String, Set<String>> getResourceMethodsByState() {
		initialise();
		return resourceMethodsByState;
	}

	public Map<String, String> getResourcePathsByState() {
		initialise();
		return resourcePathsByState;
	}

	protected Map<String, Set<String>> getResourceStatesByPath(Properties beanMap) {
		initialise();
		return resourceStatesByPath;
	}
		
	@Override
	public void setStateRegisteration(StateRegisteration registerState) {
		this.stateRegisteration = registerState;		
	}	
}