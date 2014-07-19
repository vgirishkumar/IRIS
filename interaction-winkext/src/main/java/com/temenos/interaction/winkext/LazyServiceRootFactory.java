package com.temenos.interaction.winkext;

/*
 * #%L
 * interaction-winkext
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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceLocatorProvider;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.ResourceStateProvider;
import com.temenos.interaction.core.hypermedia.Transformer;
import com.temenos.interaction.core.rim.HTTPResourceInteractionModel;
import com.temenos.interaction.winkext.ServiceRootFactory;

/**
 * A resource factory that uses the beans and configuration files from the SpringDSL implementation
 * to construct all the resources required for an hypermedia server instance.
 * @author aphethean
 */
public class LazyServiceRootFactory implements ServiceRootFactory {

	private ResourceStateProvider resourceStateProvider;
	// resources by path
	private Map<String, LazyResourceDelegate> resources = new HashMap<String, LazyResourceDelegate>();
	private ResourceStateMachine hypermediaEngine;
	
    /**
     * Map of ResourceState bean names, to paths.
     */
	private Properties beanMap;
	private Map<String, Set<String>> resourceMethodsByState = new HashMap<String, Set<String>>();
	private Map<String, String> resourcePathsByState = new HashMap<String, String>();

	// members passed to lazy resources
	private NewCommandController commandController;
	private Metadata metadata;
	private ResourceLocatorProvider resourceLocatorProvider;
	private ResourceState exception;
	private Transformer transformer;

	public Set<HTTPResourceInteractionModel> getServiceRoots() {
		hypermediaEngine = new ResourceStateMachine.Builder()
				.initial(null)
				.exception(exception)
				.transformer(transformer)
				.resourceLocatorProvider(resourceLocatorProvider)
				.resourceStateProvider(resourceStateProvider)
				.build();

		Set<HTTPResourceInteractionModel> services = new HashSet<HTTPResourceInteractionModel>();
		build(beanMap);
		for (String stateName : resourceMethodsByState.keySet()) {
			String path = resourcePathsByState.get(stateName);
			LazyResourceDelegate resource = resources.get(path);
			Set<String> methods = resourceMethodsByState.get(stateName);
			if (resource == null) {
				resource = new LazyResourceDelegate(hypermediaEngine,
						resourceStateProvider,
						commandController,
						metadata,
						stateName, 
						path,
						methods);
			} else {
				resource.addResource(stateName, methods);
			}
			resources.put(path, resource);
		}
		for (String path : resources.keySet()) {
			services.add(resources.get(path));
		}
		return services;
	}

    public void setBeanMap(Properties beanMap) {
    	this.beanMap = beanMap;
    }
    
    public Properties getBeanMap() {
    	return beanMap;
    }

	public NewCommandController getCommandController() {
		return commandController;
	}

	public void setCommandController(NewCommandController commandController) {
		this.commandController = commandController;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public ResourceLocatorProvider getResourceLocatorProvider() {
		return resourceLocatorProvider;
	}

	public void setResourceLocatorProvider(
			ResourceLocatorProvider resourceLocatorProvider) {
		this.resourceLocatorProvider = resourceLocatorProvider;
	}

	public ResourceState getException() {
		return exception;
	}

	public void setException(ResourceState exception) {
		this.exception = exception;
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	public ResourceStateProvider getResourceStateProvider() {
		return resourceStateProvider;
	}

	public void setResourceStateProvider(ResourceStateProvider resourceStateProvider) {
		this.resourceStateProvider = resourceStateProvider;
	}

	public ResourceStateMachine getHypermediaEngine() {
		return hypermediaEngine;
	}

	public void setHypermediaEngine(ResourceStateMachine hypermediaEngine) {
		this.hypermediaEngine = hypermediaEngine;
	}

	protected Map<String, Set<String>> getResourceStatesByPath(Properties beanMap) {
		Map<String, Set<String>> resourceStatesByPath = new HashMap<String, Set<String>>();
		for (Object key : beanMap.keySet()) {
			String stateName = key.toString();
			String binding = beanMap.getProperty(stateName);
			// split into methods and path
			String[] strs = binding.split(" ");
			String path = strs[1];
			
			Set<String> states = resourceStatesByPath.get(stateName);
			if (states == null) {
				states = new HashSet<String>();
			}
			states.add(stateName);
			resourceStatesByPath.put(path, states);
			
		}
		return resourceStatesByPath;
	}

	protected void build(Properties beanMap) {
		for (Object key : beanMap.keySet()) {
			String stateName = key.toString();
			String binding = beanMap.getProperty(stateName);
			// split into methods and path
			String[] strs = binding.split(" ");
			String methodPart = strs[0];
			String path = strs[1];
			// path
			resourcePathsByState.put(stateName, path);
			// methods
			Set<String> methods = resourceMethodsByState.get(stateName);
			if (methods == null) {
				methods = new HashSet<String>();
			}
			String[] methodsStrs = methodPart.split(",");
			for (String method : methodsStrs) {
				methods.add(method);
			}
			resourceMethodsByState.put(stateName, methods);
			
		}
	}

	protected Map<String, Set<String>> getResourceMethodsByState() {
		return resourceMethodsByState;
	}

	protected Map<String, String> getResourcePathsByState() {
		return resourcePathsByState;
	}

	
}
