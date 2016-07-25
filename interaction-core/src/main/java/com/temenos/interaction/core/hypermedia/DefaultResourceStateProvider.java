package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

public class DefaultResourceStateProvider implements ResourceStateProvider {

	private ResourceStateMachine hypermediaEngine;
	PathTree paths = new PathTree();   
	
	
    @Context
    private UriInfo uriInfo;
	
	public DefaultResourceStateProvider(ResourceStateMachine hypermediaEngine) {
		this.hypermediaEngine = hypermediaEngine;
		
    	Queue<Transition> queue = new LinkedList<Transition>();
    	
    	ResourceState initial = hypermediaEngine.getInitial();
    	
    	paths.put(initial.getPath(), "GET", initial.getName());
    	
    	Set<Transition> alreadyProcessed = new HashSet<Transition>();
    	Transition.Builder builder = new Transition.Builder();
    	
    	alreadyProcessed.add(builder.target(initial).build());
    	
    	for(Transition transition: initial.getTransitions()) {
    		queue.offer(transition);
    	}    	
            	
        while(!queue.isEmpty()) {
        	Transition transition = queue.poll();
        	
        	if(!alreadyProcessed.contains(transition)) {
            	ResourceState target = transition.getTarget();
            	        		        		
        		String method = transition.getCommand().getMethod();
        		
                alreadyProcessed.add(transition);
                
        		if(target.getPath() != null) {
        			paths.put(target.getPath(), method, target.getName());        	
	                
	                for(Transition targetTransition: target.getTransitions()) {
	                	if(!alreadyProcessed.contains(targetTransition)) {
	                		queue.offer(targetTransition);
	                	}
	                }
        		}
        	}
        }		
	}

	@Override
	public boolean isLoaded(String name) {
		return hypermediaEngine.getResourceStateByName(name) != null;
	}

	@Override
	public ResourceState getResourceState(String name) {
		return hypermediaEngine.getResourceStateByName(name);
	}

	@Override
	public ResourceState determineState(Event event, String resourcePath) {
		return hypermediaEngine.determineState(event, resourcePath);
	}

	@Override
	public Map<String, Set<String>> getResourceStatesByPath() {
		Map<String, Set<String>> results = new HashMap<String, Set<String>>();
		Map<String, Set<ResourceState>> statesByPath = hypermediaEngine.getResourceStatesByPath();
		for (String key : statesByPath.keySet()) {
			Set<ResourceState> states = statesByPath.get(key);
			Set<String> stateNames = new HashSet<String>();
			for (ResourceState state : states) {
				stateNames.add(state.getName());
			}
			results.put(key, stateNames);
		}
		return results;
	}

	@Override
	public Map<String, Set<String>> getResourceMethodsByState() {
		return hypermediaEngine.getInteractionByState();
	}

	@Override
	public Map<String, String> getResourcePathsByState() {
		Map<String, String> results = new HashMap<String, String>();
		Map<String, ResourceState> statesByName = hypermediaEngine.getResourceStateByName();

		for (String key : statesByName.keySet()) {
			ResourceState resourceState = statesByName.get(key);
			results.put(key, resourceState.getPath());
		}
		return results;
	}
		
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public ResourceState getResourceState(String httpMethod, String url) throws MethodNotAllowedException {

        String resourceStateId = getResourceStateId(httpMethod, url);
        if(resourceStateId == null) {
            if(paths.get(url) != null) {
                Set<String> allowedMethods = paths.get(url).keySet();
                throw new MethodNotAllowedException(allowedMethods);
            } else {
                return null;
            }
        }
        return getResourceState(resourceStateId);
    }

    @Override
    public String getResourceStateId(String httpMethod, String url) throws MethodNotAllowedException {
        Map<String,String> methodToState = null;
        
        methodToState = paths.get(url);

        String resourceStateId = null;
        
        if(methodToState != null) {
            resourceStateId = methodToState.get(httpMethod);
            if(resourceStateId == null) {
                if(paths.get(url) != null) {
                    Set<String> allowedMethods = paths.get(url).keySet();
                    throw new MethodNotAllowedException(allowedMethods);
                }
            }
        } else {
            return null;
        }
        
        return resourceStateId;
    }
}