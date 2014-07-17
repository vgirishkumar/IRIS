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


import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceLocatorProvider;
import com.temenos.interaction.core.hypermedia.ResourceState;
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
	
    /**
     * Map of ResourceState bean names, to paths.
     */
	private Properties beanMap;
	
	private NewCommandController commandController;
	private Metadata metadata;
	private ResourceLocatorProvider resourceLocatorProvider;
	private ResourceState exception;
	private Transformer transformer;

	public Set<HTTPResourceInteractionModel> getServiceRoots() {
		Set<HTTPResourceInteractionModel> services = new HashSet<HTTPResourceInteractionModel>();
		for (Object stateObj : beanMap.keySet()) {
			String stateName = stateObj.toString();
			String binding = beanMap.getProperty(stateName);
			// split into methods and path
			String[] strs = binding.split(" ");
			String path = strs[1];
			LazyResourceDelegate resource = new LazyResourceDelegate(commandController,
					metadata,
					resourceLocatorProvider,
					exception,
					transformer,
					resourceStateProvider, stateName, path);
			services.add(resource);
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

}
