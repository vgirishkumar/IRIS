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
import com.temenos.interaction.core.command.MethodNotAllowedCommand;
import com.temenos.interaction.core.command.ResourceCommand;
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
    private final ResourceState currentState;
    
	public static ResourceState createPseudoStateMachine(String entityName, String resourceName) {
		/*
		 * any interaction might be possible for a dynamic resource created without the 
		 * assistance of a state machine, therefore add all possible transitions.
		 */
		ResourceState initial = new ResourceState(entityName, resourceName + ".pseudo.initial");
		ResourceState pseudo = new ResourceState(entityName, resourceName + ".pseudo.created");
		ResourceState deleted = new ResourceState(entityName, resourceName + ".pseudo.deleted");
		initial.addTransition("POST", pseudo);
		pseudo.addTransition("PUT", pseudo);
		pseudo.addTransition("DELETE", deleted);
		return initial;
	}

	/**
	 * Create a dynamic resource with application state interaction defined in supplied StateMachine.
	 * @param stateMachine
	 * 			All application states.
	 * @param path 			
	 * 			The path to this resource, when concatenated to the parent path forms the fully qualified URI. 
	 * @param resourceRegistry
	 * 			A registry of all resources.
	 * @param commandController
	 * 			All commands for all resources.
	 */
	public HTTPDynaRIM(ResourceStateMachine stateMachine, String path, ResourceRegistry resourceRegistry, CommandController commandController) {
		this(null, stateMachine, path, stateMachine.getInitial(), resourceRegistry, commandController);
	}

	/**
	 * Create a dynamic resource for resource state interaction.
	 * @param parent
	 * 			This resources parent interaction model.
	 * @param stateMachine
	 * 			All application states.
	 * @param path 			
	 * 			The path to this resource, when concatenated to the parent path forms the fully qualified URI. 
	 * @param currentState	
	 * 			The current application state when accessing this resource.
	 * @param resourceRegistry
	 * 			A registry of all resources.
	 * @param commandController
	 * 			All commands for all resources.
	 */
	protected HTTPDynaRIM(HTTPDynaRIM parent, ResourceStateMachine stateMachine, String path, ResourceState currentState, 
			ResourceRegistry resourceRegistry, CommandController commandController) {
		super(path, resourceRegistry, commandController);
		this.parent = parent;
		this.stateMachine = stateMachine;
		this.currentState = currentState;
		assert(stateMachine != null);
		assert(currentState != null);
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
		// every resource MUST have a GET command
		getCommandController().fetchGetCommand(getFQResourcePath());
		Set<String> interactions = stateMachine.getInteractions(currentState);
		
		if (interactions != null) {
			// interactions are a set of http methods
			for (String method : interactions) {
				logger.debug("Checking configuration for [" + method + "] " + getFQResourcePath());
				// already checked GET command for this resource
				if (method.equals(HttpMethod.GET))
					continue;
				// check valid http method
				if (!(method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE) || method.equals(HttpMethod.POST)))
					throw new RuntimeException("Invalid configuration of state [" + stateMachine.getInitial().getId() + "] - invalid http method [" + method + "]");
				// fetch command from command controller for this method
				ResourceCommand stc = getCommandController().fetchStateTransitionCommand(method, getFQResourcePath());
				if (stc instanceof MethodNotAllowedCommand)
					throw new RuntimeException("Invalid configuration of dynamic resource [" + this + "] - no state transition command for http method [" + method + "]");
			}
		}

		// TODO should be verified in constructor, but this class is currently mixed with dynamic resources that do not use links
		// assert(getResourceRegistry() != null);
		// resource created and valid, now register ourselves in the resource registry
		if (getResourceRegistry() != null)
			getResourceRegistry().add(this);
	}
	

	@Override
    public ResourceInteractionModel getParent() {
        return parent;
    }
    
	@Override
	public ResourceState getCurrentState() {
		return currentState;
	}
	
	public ResourceStateMachine getStateMachine() {
		return stateMachine;
	}
	
	@Override
	public Collection<ResourceInteractionModel> getChildren() {
		List<ResourceInteractionModel> result = new ArrayList<ResourceInteractionModel>();
		
		Map<String, ResourceState> resourceStates = stateMachine.getStateMap(this.currentState);
		for (String childPath : resourceStates.keySet()) {
			ResourceStateMachine childSM = stateMachine;
			// get the child state
			ResourceState childState = resourceStates.get(childPath);
			if (!childState.getEntityName().equals(stateMachine.getInitial().getEntityName())) {
				// TODO shouldn't really need to create it again
				childSM = new ResourceStateMachine(childState);
			}
			HTTPDynaRIM child = new HTTPDynaRIM(this, childSM, childState.getPath(), childState, getResourceRegistry(), getCommandController());
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
		return ("HTTPDynaRIM " + stateMachine.getInitial().getId() + "[" + getFQResourcePath() + "]");
	}
}
