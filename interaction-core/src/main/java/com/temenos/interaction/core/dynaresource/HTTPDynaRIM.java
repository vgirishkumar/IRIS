package com.temenos.interaction.core.dynaresource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.HttpMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.ASTValidation;
import com.temenos.interaction.core.link.ResourceStateMachine;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
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
    
	public HTTPDynaRIM(String entityName, String path, CommandController commandController) {
		this(new ResourceStateMachine(entityName, null), path, null, commandController);
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
	public HTTPDynaRIM(ResourceStateMachine stateMachine, String path, ResourceRegistry rr, CommandController commandController) {
		super(stateMachine.getEntityName(), path, rr, commandController);
		this.parent = null;
		this.stateMachine = stateMachine;
		if (stateMachine.getInitial() != null) {
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
	public HTTPDynaRIM(HTTPDynaRIM parent, ResourceStateMachine stateMachine, String path, 
			ResourceRegistry rr, CommandController commandController) {
		super(stateMachine.getEntityName(), path, rr, commandController);
		this.parent = parent;
		this.stateMachine = stateMachine;
		if (parent == null && stateMachine.getInitial() != null) {
			logger.info("Checking state machine for [" + this.toString() + "]");
			logger.info(new ASTValidation().graph(stateMachine));
		}
		bootstrap();
	}

	/*
	 * Bootstrap the resource by attempting to fetch a command for all the required
	 * interactions with the resource state.
	 */
	private void bootstrap() {
		getCommandController().fetchGetCommand(getFQResourcePath());
		Set<String> interactions = stateMachine.getInteractions(stateMachine.getInitial());
		if (stateMachine != null && interactions != null) {
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
		Set<String> interactions = stateMachine.getInteractions(stateMachine.getInitial());
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
		List<String> createdResources = new ArrayList<String>();
		for (ResourceState s : stateMachine.getStates()) {
			boolean substate = !s.equals(stateMachine.getInitial()) && !s.isFinalState() && !s.isSelfState();
			if (substate && !createdResources.contains(s.getPath())) {
				HTTPDynaRIM child = new HTTPDynaRIM(this, new ResourceStateMachine(getEntityName(), s), s.getPath(), null, getCommandController());
				result.add(child);
				createdResources.add(s.getPath());
			}
		}
		return result;
	}

	public String toString() {
		return ("HTTPDynaRIM " + stateMachine.getEntityName() + "[" + getFQResourcePath() + "]");
	}
}
