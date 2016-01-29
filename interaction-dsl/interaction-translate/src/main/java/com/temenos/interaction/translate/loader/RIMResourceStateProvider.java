package com.temenos.interaction.translate.loader;

import java.io.File;

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
import com.temenos.interaction.core.loader.ResourceStateLoader.ResourceStateResult;
import com.temenos.interaction.rimdsl.generator.launcher.RIMResourceStateLoaderTemplate;
import com.temenos.interaction.springdsl.DynamicRegistrationResourceStateProvider;
import com.temenos.interaction.springdsl.StateRegisteration;
import com.temenos.interaction.translate.mapper.ResourceStateMapper;

/**
 *
 * Register resource states with the state machine and the web service provider
 * (such as Apache Wink). Registration with the provider is done when the
 * object is initialised, and when new resources are added.
 * 
 * Register resource states with the state machine and Apache Wink.
 * 
 * @author dgroves
 * @author hmanchala
 * @author kwieconkowski
 * @author andres
 */
public class RIMResourceStateProvider implements FileMappingResourceStateProvider, 
		DynamicRegistrationResourceStateProvider {
		
	private final Logger logger = LoggerFactory.getLogger(RIMResourceStateProvider.class);
	
	private final Cache<String, ResourceStateResult> cache;
	private final String antPattern;
	
	private StateRegisteration stateRegistration;
    private RIMResourceStateLoaderTemplate resourceStateLoader;
    private Collection<String> sources;
    private ResourceStateMapper mapper;
    	
	public RIMResourceStateProvider(String antPattern, Cache<String, ResourceStateResult> cache, 
			Collection<String> sources, ResourceStateMapper mapper, RIMResourceStateLoaderTemplate loader,
			StateRegisteration stateRegistration){
		this.antPattern = antPattern;
		this.cache = cache;
		this.sources = sources;
		this.mapper = mapper;
		this.resourceStateLoader = loader;
		this.stateRegistration = stateRegistration;
	}
    
    public RIMResourceStateProvider(String antPattern, Cache<String, ResourceStateResult> cache, 
    		ResourceStateMapper mapper, RIMResourceStateLoaderTemplate loader) {
    	this.antPattern = antPattern;
    	this.cache = cache;
		this.sources = new HashSet<String>();
		this.mapper = mapper;
		this.resourceStateLoader = loader;
		findRimFilenames();
		loadAllResourceStates();
	}
	
    @Override
	public void loadAndMapFileNames(Collection<String> rimFilenames) {
    	List<ResourceStateResult> results = new ArrayList<ResourceStateResult>();
        for (String filename : rimFilenames) {
            results.addAll(resourceStateLoader.load(filename));
        }
        populateCacheAndMapResourceStates(results, false);
	}
    
    @Override
    public void loadAndMapFileObjects(Collection<File> files) {
    	List<ResourceStateResult> results = new ArrayList<ResourceStateResult>();
        for (File file : files) {
            results.addAll(resourceStateLoader.load(file));
        }
        populateCacheAndMapResourceStates(results, true);
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
		logger.info("Registering with Apache Wink...");
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
        loadAndMapFileNames(sources);
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
	
    public void setLoader(RIMResourceStateLoaderTemplate resourceStateLoader){
    	this.resourceStateLoader = resourceStateLoader;
    }
        
	@Override
	public void setStateRegisteration(StateRegisteration stateRegisteration) {
		this.stateRegistration = stateRegisteration;
	}
}
