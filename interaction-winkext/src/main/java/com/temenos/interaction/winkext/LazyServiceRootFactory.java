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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceLocatorProvider;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.ResourceStateProvider;
import com.temenos.interaction.core.hypermedia.Transformer;
import com.temenos.interaction.core.rim.HTTPResourceInteractionModel;
import com.temenos.interaction.springdsl.DynamicRegistrationResourceStateProvider;
import com.temenos.interaction.springdsl.RIMRegistration;
import com.temenos.interaction.springdsl.StateRegisteration;

/**
 * A resource factory that uses the beans and configuration files from the SpringDSL implementation
 * to construct all the resources required for an hypermedia server instance.
 * @author aphethean
 */
public class LazyServiceRootFactory implements ServiceRootFactory, StateRegisteration {

	private final Logger logger = LoggerFactory.getLogger(LazyServiceRootFactory.class);
	
	private ResourceStateProvider resourceStateProvider;
	// resources by path
	private Map<String, LazyResourceDelegate> resources = new HashMap<String, LazyResourceDelegate>();
	private ResourceStateMachine hypermediaEngine;
	
	// members passed to lazy resources
	private NewCommandController commandController;
	private Metadata metadata;
	private ResourceLocatorProvider resourceLocatorProvider;
	private ResourceState exception;
	private Transformer transformer;
	private RIMRegistration rimRegistration;

	public Set<HTTPResourceInteractionModel> getServiceRoots() {
		if(resourceStateProvider instanceof DynamicRegistrationResourceStateProvider) {
			((DynamicRegistrationResourceStateProvider)resourceStateProvider).setStateRegisteration(this);
		}
		
		hypermediaEngine = new ResourceStateMachine.Builder()
				.initial(null)
				.exception(exception)
				.transformer(transformer)
				.resourceLocatorProvider(resourceLocatorProvider)
				.resourceStateProvider(resourceStateProvider)
				.build();

		Set<HTTPResourceInteractionModel> services = new HashSet<HTTPResourceInteractionModel>();
		Map<String, Set<String>> resourceMethodsByState = resourceStateProvider.getResourceMethodsByState();
		Map<String, String> resourcePathsByState = resourceStateProvider.getResourcePathsByState();
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
	
	@Override
	public void register(String stateName, String path, Set<String> methods) {
		logger.info("Attempting to add service: " + stateName);
		
		LazyResourceDelegate resource = new LazyResourceDelegate(hypermediaEngine,
				resourceStateProvider,
				commandController,
				metadata,
				stateName, 
				path,
				methods);
		
		rimRegistration.register(resource);
		
		logger.info("#####################################################");		
		logger.info("####        New service registered              #####");
		logger.info("#####################################################");		
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

	public void setResourceLocatorProvider(ResourceLocatorProvider resourceLocatorProvider) {
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

	@Override
	public void setRIMRegistration(RIMRegistration rimRegistration) {
		this.rimRegistration = rimRegistration;		
	}	
}
