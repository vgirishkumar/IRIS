package com.temenos.interaction.core.dynaresource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    private final ResourceState subState;
    
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
		this(null, stateMachine, path, stateMachine.getInitial(), rr, commandController);
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
	public HTTPDynaRIM(HTTPDynaRIM parent, ResourceStateMachine stateMachine, String path, ResourceState subState, 
			ResourceRegistry rr, CommandController commandController) {
		super(stateMachine.getEntityName(), path, rr, commandController);
		this.parent = parent;
		this.stateMachine = stateMachine;
		this.subState = subState;
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
		if (subState != null) {
			Set<String> interactions = stateMachine.getInteractions(subState);
			if (interactions != null) {
				// interactions are a set of http methods
				for (String method : interactions) {
					logger.debug("Checking configuration for [" + method + "] " + getFQResourcePath());
					// TODO probably shouldn't end up with a GET interaction here, but maybe we should...
					if (method.equals(HttpMethod.GET))
						continue;
					// check valid http method
					if (!(method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE) || method.equals(HttpMethod.POST)))
						throw new RuntimeException("Invalid configuration of state [" + stateMachine.getInitial().getName() + "] for entity [" + getEntityName() + "]- invalid http method [" + method + "]");
					// fetch command from command controller for this method
					getCommandController().fetchStateTransitionCommand(method, getFQResourcePath());
				}
			}
		}
	}
	

	@Override
    public ResourceInteractionModel getParent() {
        return parent;
    }
    
	@Override
	public ResourceState getCurrentState() {
		return subState;
	}
	
	@Override
	public Collection<ResourceInteractionModel> getChildren() {
		List<ResourceInteractionModel> result = new ArrayList<ResourceInteractionModel>();
		
		Map<String, ResourceState> resourceStates = stateMachine.getStateMap(this.subState);
		for (String childPath : resourceStates.keySet()) {
			ResourceState s = resourceStates.get(childPath);
			HTTPDynaRIM child = new HTTPDynaRIM(this, stateMachine, s.getPath(), s, null, getCommandController());
			result.add(child);
		}
		return result;
	}

	public boolean equals(Object other) {
		//check for self-comparison
	    if ( this == other ) return true;
	    if ( !(other instanceof HTTPDynaRIM) ) return false;
	    HTTPDynaRIM otherResource = (HTTPDynaRIM) other;
	    return getFQResourcePath().equals(otherResource.getFQResourcePath());
	}
	
	public int hashCode() {
		return getFQResourcePath().hashCode();
	}

	public String toString() {
		return ("HTTPDynaRIM " + stateMachine.getEntityName() + "[" + getFQResourcePath() + "]");
	}
}
