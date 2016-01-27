package com.temenos.interaction.translate.loader;

/*
 * #%L
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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


import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.temenos.interaction.core.cache.Cache;
import com.temenos.interaction.core.hypermedia.Event;
import com.temenos.interaction.core.hypermedia.FileMappingResourceStateProvider;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.StateMachineRegistrar;
import com.temenos.interaction.core.loader.ResourceStateLoader;
import com.temenos.interaction.core.loader.ResourceStateLoader.ResourceStateResult;
import com.temenos.interaction.springdsl.DynamicRegistrationResourceStateProvider;
import com.temenos.interaction.springdsl.StateRegisteration;
import com.temenos.interaction.translate.mapper.ResourceStateMapper;

/**
 * 
 * Register resource states with the state machine and Apache Wink.
 * 
 * @author dgroves
 * @author hmanchala
 *
 */
public class RIMResourceStateProvider implements FileMappingResourceStateProvider, 
		StateMachineRegistrar, DynamicRegistrationResourceStateProvider {
	
	private static final boolean REGISTER_WITH_WINK_DURING_INITIALISATION = false;
	
	private final Logger logger = LoggerFactory.getLogger(RIMResourceStateProvider.class);
	
	private final Cache<String, ResourceStateResult> cache;
	private final String antPattern;
	
	private StateRegisteration stateRegistration;
    private ResourceStateLoader<String> resourceStateLoader;
    private ResourceStateMachine resourceStateMachine;
    private Collection<String> sources;
    private ResourceStateMapper mapper;
    	
	public RIMResourceStateProvider(String antPattern, Cache<String, ResourceStateResult> cache, 
			Collection<String> sources, ResourceStateMapper mapper, ResourceStateLoader<String> loader,
			StateRegisteration stateRegistration){
		this.antPattern = antPattern;
		this.cache = cache;
		this.sources = sources;
		this.mapper = mapper;
		this.resourceStateLoader = loader;
		this.stateRegistration = stateRegistration;
	}
    
    public RIMResourceStateProvider(String antPattern, Cache<String, ResourceStateResult> cache) {
    	this.antPattern = antPattern;
    	this.cache = cache;
		this.sources = new HashSet<String>();
		findRimFilenames();
		loadAllResourceStates();
	}
	
    @Override
	public void loadAndMapFiles(Collection<String> rimFilenames, boolean register) {
		List<ResourceStateResult> results = new ArrayList<ResourceStateResult>();
        for (String filename : rimFilenames) {
            results.addAll(resourceStateLoader.load(filename));
        }
        populateCacheAndMapResourceStates(results, register);
	}
	
    @Override
    public ResourceState getResourceState(String stateName) {
    	ResourceStateResult resolvedResourceState = cache.get(stateName);
    	ResourceState resourceState = null;
    	if(resolvedResourceState != null){
    		logger.debug("Found resource state: ["+stateName+"]");
    		resourceState = resolvedResourceState.getResourceState();
    	}else{
    		logger.error("Could not find resource state: ["+stateName+"]");
    	}
    	return resourceState;
    }
    
    @Override
    public ResourceState determineState(Event event, String resourcePath) {
    	ResourceState result = null;
    	String request = event.getMethod() + " " + resourcePath, 
				stateName = mapper.getResourceStatesByRequest().get(request);
		if (stateName != null){
			logger.debug("Found resource state: ["+stateName+"] for request: ["+request+"]");
			result = getResourceState(stateName);
		}else{
			logger.warn("Could not find resource state: ["+stateName+"] for request: ["+request+"]");
		}
		return result;
    }
    
    @Override
    public boolean isLoaded(String name) {
        return cache.get(name) != null;
    }
    
    private void populateCacheAndMapResourceStates(List<ResourceStateResult> results, 
    		boolean register){
		Map<String, ResourceStateResult> resourceStateNamesToResourceStates = 
				new HashMap<String, ResourceStateResult>();
		for(ResourceStateResult result : results){
			resourceStateNamesToResourceStates.put(result.getResourceStateId(), result);
			mapper.map(result);
			if(register){
				registerWithApacheWink(result);
			}
		}
		this.cache.putAll(resourceStateNamesToResourceStates);
	}
		
	private void registerWithApacheWink(ResourceStateResult resource){
		stateRegistration.register(resource.getResourceStateId(), resource.getPath(), 
				new HashSet<String>(Arrays.asList(resource.getMethods())));
	}
	
    private void findRimFilenames() {
        final ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        final Resource[] locations;
        try {
            String fileName;
            locations = patternResolver.getResources(antPattern);
            if (locations != null) {
                sources.clear();
                for (Resource location : locations) {
                    fileName = Paths.get(
                    		location
                				.getURI()
                				.getPath()
                				.substring(1)
    				).getFileName().toString();
                    sources.add(fileName);
                    logger.info("Found RIM file: " + fileName);
                }
            } else {
                logger.warn("No RIM files found for pattern: "+antPattern);
            }
        } catch (IOException e) {
            String msg = "IOException while loading RIM files";
            logger.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
    }
    
    private synchronized void loadAllResourceStates() {
        cache.removeAll();
        loadAndMapFiles(sources, REGISTER_WITH_WINK_DURING_INITIALISATION);
    }
    
    @Override
    public Map<String, Set<String>> getResourceStatesByPath() {
        return mapper.getResourceStatesByPath();
    }

    @Override
    public Map<String, Set<String>> getResourceMethodsByState() {
    	return mapper.getResourceMethodsByState();
    }

    @Override
    public Map<String, String> getResourcePathsByState() {
        return mapper.getResourcePathsByState();
    }
    
	@Override
	public void registerResourceStateResult(ResourceState resourceState, String method) {
		this.resourceStateMachine.register(resourceState, method);
	}
	
	@Override
	public void setResourceStateMachine(ResourceStateMachine resourceStateMachine) {
		this.resourceStateMachine = resourceStateMachine;
	}
	
    public void setLoader(ResourceStateLoader<String> resourceStateLoader){
    	this.resourceStateLoader = resourceStateLoader;
    }
        
	@Override
	public void setStateRegisteration(StateRegisteration stateRegisteration) {
		this.stateRegistration = stateRegisteration;
	}
}
