package com.temenos.interaction.core.dynaresource;

import java.util.ArrayList;
import java.util.Collection;
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

    private HTTPDynaRIM parent;
    private String workspaceTitle;
    private String collectionTitle;
    private String beanName;

    private final String path;
    private final ResourceStateMachine stateMachine;
    private final Set<String> interactions;
    
	public HTTPDynaRIM(String entityName, String path, CommandController commandController) {
		this(entityName, path, null, null, commandController);
		this.parent = null;
	}

	public HTTPDynaRIM(String entityName, String path, ResourceState state, ResourceRegistry rr, CommandController commandController) {
		super(entityName, path, rr, commandController);
		this.parent = null;
		this.path = path;
		this.interactions = null;
		this.stateMachine = new ResourceStateMachine(state);
		if (state != null) {
			System.out.println(new ASTValidation().graph(stateMachine));
		}
	}

	public HTTPDynaRIM(HTTPDynaRIM parent, String entityName, String path, 
			ResourceState state, Set<String> interactions, ResourceRegistry rr, CommandController commandController) {
		super(entityName, path, rr, commandController);
		this.parent = parent;
		this.path = path;
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
        return path;
    }

	@Override
    public void setParent(Object parent) {
        this.parent = (HTTPDynaRIM) parent;
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
		ResourceState thisState = stateMachine.getInitial();
		for (ResourceState s : thisState.getAllTargets()) {
			Transition t = thisState.getTransition(s);
			// only create a new child resource if we are defined a new state
			if (!t.getCommand().getPath().equals(thisState.getPath())) {
				HTTPDynaRIM child = new HTTPDynaRIM(this, getEntityName(), t.getCommand().getPath(), s, interactionMap.get(t.getCommand().getPath()), null, getCommandController());
				result.add(child);
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
