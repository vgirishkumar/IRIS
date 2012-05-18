package com.temenos.interaction.core.dynaresource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jaxrs.hateoas.HateoasLink;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.MethodNotAllowedCommand;
import com.temenos.interaction.core.command.ResourceCommand;
import com.temenos.interaction.core.link.ASTValidation;
import com.temenos.interaction.core.link.Link;
import com.temenos.interaction.core.link.ResourceStateMachine;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.Transition;
import com.temenos.interaction.core.link.TransitionCommandSpec;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.state.AbstractHTTPResourceInteractionModel;
import com.temenos.interaction.core.state.ResourceInteractionModel;
import com.temenos.interaction.core.web.RequestContext;

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
    
	public static ResourceState createPseudoStateMachine(String entityName, String resourceName, String resourcePath) {
		/*
		 * any interaction might be possible for a dynamic resource created without the 
		 * assistance of a state machine, therefore add all possible transitions.
		 */
		ResourceState initial = new ResourceState(entityName, resourceName + ".pseudo.initial", resourcePath);
		ResourceState pseudo = new ResourceState(initial, resourceName + ".pseudo.created");
		ResourceState deleted = new ResourceState(initial, resourceName + ".pseudo.deleted");
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
	public HTTPDynaRIM(ResourceStateMachine stateMachine, ResourceRegistry resourceRegistry, CommandController commandController) {
		this(null, stateMachine, stateMachine.getInitial(), resourceRegistry, commandController);
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
	protected HTTPDynaRIM(HTTPDynaRIM parent, ResourceStateMachine stateMachine, ResourceState currentState, 
			ResourceRegistry resourceRegistry, CommandController commandController) {
		super(currentState.getPath(), resourceRegistry, commandController);
		this.parent = parent;
		this.stateMachine = stateMachine;
		this.currentState = currentState;
		assert(stateMachine != null);
		assert(currentState != null);
		if (parent == null && stateMachine.getInitial() != null) {
			logger.info("State graph for [" + this.toString() + "] [" + new ASTValidation().graph(stateMachine) + "]");
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
	
	@Override
	public Collection<HateoasLink> getLinks(RESTResource entity) {
		List<HateoasLink> links = new ArrayList<HateoasLink>();
		// add link to GET 'self'
		String selfUri = RequestContext.getRequestContext().getBasePath().path(getFQResourcePath()).build(entity).toASCIIString();
		links.add(new Link(getCurrentState().getId(), "self", selfUri, null, null, "GET", "label", "description", null));

		/*
		 * Add links to other application states (resources)
		 */
		Collection<ResourceState> targetStates = getCurrentState().getAllTargets();
		for (ResourceState s : targetStates) {
			Transition transition = getCurrentState().getTransition(s);
			TransitionCommandSpec cs = transition.getCommand();
			String linkId = transition.getId();
			// TODO get rels properly
			String rel = s.getId();
			
			String method = cs.getMethod();
			String path = cs.getPath();
			URI href = null;			
			/* 
			 * build link and add to list of links
			 */
			UriBuilder linkTemplate = RequestContext.getRequestContext().getBasePath().path(path);
			href = linkTemplate.build();
			/*
			if (map != null) {
				href = linkTemplate.buildFromMap(map);
			} else {
				href = linkTemplate.build();
			}
			 */
			links.add(new Link(linkId, rel, href.toASCIIString(), null, null, method, "label", "description", null));
			logger.debug("Link added to [" + getFQResourcePath() + "] [id=" + linkId+ ", rel=" + rel + ", method=" + method + ", href=" + href.toString() + "(" + href.toASCIIString() + ")]");
		}
		return links;
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
			HTTPDynaRIM child = null;
			if (!childState.getEntityName().equals(stateMachine.getInitial().getEntityName())) {
				// TODO shouldn't really need to create it again
				childSM = new ResourceStateMachine(childState);
				// this is a new resource
				child = new HTTPDynaRIM(childSM, getResourceRegistry(), getCommandController());
			} else {
				child = new HTTPDynaRIM(this, childSM, childState, getResourceRegistry(), getCommandController());
			}
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
