package com.temenos.interaction.core.dynaresource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.HttpMethod;

import org.apache.wink.common.DynamicResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.ASTValidation;
import com.temenos.interaction.core.link.ResourceStateMachine;
import com.temenos.interaction.core.link.TransitionCommandSpec;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.Transition;
import com.temenos.interaction.core.state.HTTPResourceInteractionModel;
import com.temenos.interaction.core.state.ResourceInteractionModel;

/**
 * Define a Dynamic HTTP based Resource Interaction Model for an individual resource.
 * HTTP interactions with resources are simple, just GET, PUT, POST and DELETE.
 * @author aphethean
 */
public class HTTPDynaRIM extends HTTPResourceInteractionModel implements DynamicResource {
	private final Logger logger = LoggerFactory.getLogger(HTTPDynaRIM.class);

    private Object parent;
    private String workspaceTitle;
    private String collectionTitle;
    private String beanName;
    
    private final ResourceState state;
    private final Set<String> interactions;
    
	public HTTPDynaRIM(String entityName, String path, ResourceState state, ResourceRegistry rr, CommandController commandController) {
		super(entityName, path, rr, commandController);
		this.parent = null;
		this.state = state;
		this.interactions = null;
		System.out.println(new ASTValidation().graph(new ResourceStateMachine(this.state)));
	}

	public HTTPDynaRIM(HTTPDynaRIM parent, String entityName, String path, 
			ResourceState state, Set<String> interactions, ResourceRegistry rr, CommandController commandController) {
		super(entityName, path, rr, commandController);
		this.parent = parent;
		this.state = state;
		if (parent == null && state != null) {
			System.out.println(new ASTValidation().graph(new ResourceStateMachine(this.state)));
		}
		this.interactions = interactions;
		bootstrap();
	}

	/*
	 * Bootstrap the resource by attempting to fetch a command for all the required
	 * interactions with the resource state.
	 */
	private void bootstrap() {
		getCommandController().fetchGetCommand();
		if (state != null) {
			// interactions are a set of http methods
			for (String method : interactions) {
				logger.debug("Checking configuration for [" + method + "] " + getPath());
				// check valid http method
				if (!(method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE)))
					throw new RuntimeException("Invalid configuration of state [" + state.getName() + "] for entity [" + getEntityName() + "]- invalid http method [" + method + "]");
				// fetch command from command controller for this method
				getCommandController().fetchStateTransitionCommand(method, getPath());
			}
		}
	}
	
	@Override
    public String getBeanName() {
        return beanName;
    }

	@Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setWorkspaceTitle(String workspaceTitle) {
        this.workspaceTitle = workspaceTitle;
    }

	@Override
    public String getWorkspaceTitle() {
        return workspaceTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

	@Override
    public String getCollectionTitle() {
        return collectionTitle;
    }

	@Override
    public String getPath() {
        return getResourcePath();
    }

	@Override
    public void setParent(Object parent) {
        this.parent = parent;
    }

	@Override
    public Object getParent() {
        return parent;
    }
    
	@Override
	public Set<String> getInteractions() {
		return interactions;
	}

	public ResourceState getState() {
		return state;
	}
	
	public Collection<ResourceInteractionModel> createChildResources() {
		List<ResourceInteractionModel> result = new ArrayList<ResourceInteractionModel>();
		Map<String, Set<String>> interactionMap = getInteractionMap();
		for (String path : interactionMap.keySet()) {
			ResourceState state = null;
			Collection<ResourceState> allStates = getStates();
			for (ResourceState s : allStates) {
				if (s.getPath().equals(path)) {
					state = s;
				}
			}
			HTTPDynaRIM child = new HTTPDynaRIM(this, getEntityName(), path, state, interactionMap.get(path), null, getCommandController());
			result.add(child);
		}
		return result;
	}
	
	public Collection<ResourceState> getStates() {
		List<ResourceState> result = new ArrayList<ResourceState>();
		collectStates(result, state);
		return result;
	}

	private void collectStates(Collection<ResourceState> result, ResourceState currentState) {
		if (result.contains(currentState)) return;
		result.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			if (!next.equals(this.state)) {
				collectStates(result, next);
			}
		}
		
	}

	public Map<String, Set<String>> getInteractionMap() {
		Map<String, Set<String>> interactionMap = new HashMap<String, Set<String>>();
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectInteractions(interactionMap, states, this.state);
		return interactionMap;
	}
	
	private void collectInteractions(Map<String, Set<String>> result, Collection<ResourceState> states, ResourceState currentState) {
		if (states.contains(currentState)) return;
		states.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			if (!next.equals(this.state)) {
				// lookup transition to get to here
				Transition t = currentState.getTransition(next);
				TransitionCommandSpec command = t.getCommand();
				String path = command.getPath();
				
				Set<String> interactions = result.get(path);
				if (interactions == null)
					interactions = new HashSet<String>();
				interactions.add(command.getMethod());
				
				result.put(path, interactions);
				collectInteractions(result, states, next);
			}
		}
		
	}
	
}
