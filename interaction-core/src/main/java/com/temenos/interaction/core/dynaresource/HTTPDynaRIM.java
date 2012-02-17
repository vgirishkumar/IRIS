package com.temenos.interaction.core.dynaresource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.HttpMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.ASTValidation;
import com.temenos.interaction.core.link.ResourceStateMachine;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.Transition;
import com.temenos.interaction.core.state.AbstractHTTPResourceInteractionModel;
import com.temenos.interaction.core.state.ResourceInteractionModel;

/**
 * Define a Dynamic HTTP based Resource Interaction Model for an individual resource.
 * HTTP interactions with resources are simple, just GET, PUT, POST and DELETE.
 * @author aphethean
 */
public class HTTPDynaRIM extends AbstractHTTPResourceInteractionModel {
	private final Logger logger = LoggerFactory.getLogger(HTTPDynaRIM.class);

    private HTTPDynaRIM parent;
    private final ResourceStateMachine stateMachine;
    private final Set<String> interactions;
    
	public HTTPDynaRIM(String entityName, String path, CommandController commandController) {
		this(entityName, path, null, null, commandController);
		this.parent = null;
	}

	/**
	 * Create a dynamic resource for application state interaction.
	 * @param entityName
	 * @param path
	 * @param state
	 * @param rr
	 * @param commandController
	 */
	public HTTPDynaRIM(String entityName, String path, ResourceState state, ResourceRegistry rr, CommandController commandController) {
		super(entityName, path, rr, commandController);
		this.parent = null;
		this.interactions = null;
		this.stateMachine = new ResourceStateMachine(state);
		if (state != null) {
			System.out.println(new ASTValidation().graph(stateMachine));
		}
	}

	/**
	 * Create a dynamic resource for resource state interaction.
	 * @param parent
	 * @param entityName
	 * @param path
	 * @param state
	 * @param interactions
	 * @param rr
	 * @param commandController
	 */
	public HTTPDynaRIM(HTTPDynaRIM parent, String entityName, String path, 
			ResourceState state, Set<String> interactions, ResourceRegistry rr, CommandController commandController) {
		super(entityName, path, rr, commandController);
		this.parent = parent;
		this.stateMachine = new ResourceStateMachine(state);
		if (parent == null && state != null) {
			System.out.println(new ASTValidation().graph(stateMachine));
		}
		this.interactions = interactions;
		bootstrap();
	}

	/*
	 * Bootstrap the resource by attempting to fetch a command for all the required
	 * interactions with the resource state.
	 */
	private void bootstrap() {
		getCommandController().fetchGetCommand(getFQResourcePath());
		if (stateMachine != null) {
			// interactions are a set of http methods
			for (String method : interactions) {
				logger.debug("Checking configuration for [" + method + "] " + getFQResourcePath());
				// check valid http method
				if (!(method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE)))
					throw new RuntimeException("Invalid configuration of state [" + stateMachine.getInitial().getName() + "] for entity [" + getEntityName() + "]- invalid http method [" + method + "]");
				// fetch command from command controller for this method
				getCommandController().fetchStateTransitionCommand(method, getFQResourcePath());
			}
		}
	}
	

	@Override
    public ResourceInteractionModel getParent() {
        return parent;
    }
    
	@Override
	public ResourceState getCurrentState() {
		return stateMachine.getInitial();
	}
	
	@Override
	public Set<String> getInteractions() {
		Set<String> allows = new HashSet<String>();
		if (interactions != null)
			allows.addAll(interactions);
		allows.add("GET");
		allows.add("OPTIONS");
		allows.add("HEAD");
		return allows;
	}

	@Override
	public Collection<ResourceInteractionModel> getChildren() {
		List<ResourceInteractionModel> result = new ArrayList<ResourceInteractionModel>();
		Map<String, Set<String>> interactionMap = stateMachine.getInteractionMap();

		List<String> createdResources = new ArrayList<String>();
		for (ResourceState s : stateMachine.getStates()) {
			if (!createdResources.contains(s.getPath())) {
				HTTPDynaRIM child = new HTTPDynaRIM(this, getEntityName(), s.getPath(), s, interactionMap.get(s.getPath()), null, getCommandController());
				result.add(child);
				createdResources.add(s.getPath());
			}
		}
		return result;
	}

	/*
	public Collection<ResourceInteractionModel> createChildResources() {
		List<ResourceInteractionModel> result = new ArrayList<ResourceInteractionModel>();
		Map<String, Set<String>> interactionMap = stateMachine.getInteractionMap();
		for (String path : interactionMap.keySet()) {
			ResourceState state = null;
			Collection<ResourceState> allStates = stateMachine.getStates();
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
	*/
}
