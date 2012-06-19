package com.temenos.interaction.core.dynaresource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
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
    private final Transformer transformer;
    
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
	 * @param transformer
	 * 			The class that handles transformation from the entity to an object usable during
	 * 				URI construction.
	 * @param commandController
	 * 			All commands for all resources.
	 */
	public HTTPDynaRIM(ResourceStateMachine stateMachine, Transformer transformer, CommandController commandController) {
		this(null, stateMachine, stateMachine.getInitial(), null, transformer, commandController);
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
	 * @param transformer
	 * 			The class that handles transformation from the entity to an object usable during
	 * 				URI construction.
	 * @param commandController
	 * 			All commands for all resources.
	 */
	protected HTTPDynaRIM(HTTPDynaRIM parent, ResourceStateMachine stateMachine, ResourceState currentState, 
			Transformer transformer, CommandController commandController) {
		this(parent, stateMachine, currentState, null, transformer, commandController);
	}
	protected HTTPDynaRIM(HTTPDynaRIM parent, ResourceStateMachine stateMachine, ResourceState currentState, 
			ResourceRegistry resourceRegistry, Transformer transformer, CommandController commandController) {
		super(currentState.getPath(), resourceRegistry, commandController);
		this.parent = parent;
		this.stateMachine = stateMachine;
		this.currentState = currentState;
		this.transformer = transformer;
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
	public Collection<Link> getLinks(MultivaluedMap<String, String> pathParameters, RESTResource resourceEntity) {
		return getLinks(pathParameters, resourceEntity, getCurrentState());
	}
		
	private Collection<Link> getLinks(MultivaluedMap<String, String> pathParameters, RESTResource resourceEntity, ResourceState state) {
		List<Link> links = new ArrayList<Link>();
		
		Object entity = null;
		CollectionResource<?> collectionResource = null;
		if (resourceEntity instanceof EntityResource) {
			entity = ((EntityResource<?>) resourceEntity).getEntity();
		} else if (resourceEntity instanceof CollectionResource) {
			collectionResource = (CollectionResource<?>) resourceEntity;
			// TODO add support for properties on collections
			logger.warn("I hope you don't need to build a link from a template for links from this collection, no properties on the collection at the moment");
		} else if (resourceEntity instanceof MetaDataResource) {
			// TODO deprecate all resource types apart from item (EntityResource) and collection (CollectionResource)
			logger.debug("Returning from the call to getLinks for a MetaDataResource without doing anything");
			return links;
		} else {
			throw new RuntimeException("Unable to get links, an error occurred");
		}
		
		// add link to GET 'self'
		links.add(createSelfLink(state, entity, pathParameters));

		/*
		 * Add links to other application states (resources)
		 */
		Collection<ResourceState> targetStates = state.getAllTargets();
		for (ResourceState s : targetStates) {
			Transition transition = state.getTransition(s);
			TransitionCommandSpec cs = transition.getCommand();
			/* 
			 * build link and add to list of links
			 */
			UriBuilder linkTemplate = RequestContext.getRequestContext().getBasePath().path(cs.getPath());
			if (cs.isForEach()) {
				if (collectionResource != null) {
					for (EntityResource<?> er : collectionResource.getEntities()) {
						Collection<Link> eLinks = er.getLinks();
						if (eLinks == null) {
							eLinks = new ArrayList<Link>();
//							eLinks.add(createSelfLink(s, er.getEntity(), null));
						}
						eLinks.add(createLink(linkTemplate, transition, er.getEntity(), null));
						er.setLinks(eLinks);
					}
				}
			} else {
				links.add(createLink(linkTemplate, transition, entity, null));
			}
		}
		return links;
	}

	private Link createSelfLink(ResourceState state, Object entity, MultivaluedMap<String, String> pathParameters) {
		UriBuilder selfUriTemplate = RequestContext.getRequestContext().getBasePath().path(state.getPath());
		return createLink(selfUriTemplate, state.getSelfTransition(), entity, pathParameters);
	}
	
	private Link createLink(UriBuilder linkTemplate, Transition transition, Object entity, MultivaluedMap<String, String> map) {
		TransitionCommandSpec cs = transition.getCommand();
		try {
			String linkId = transition.getId();
			// TODO get rels properly
			String rel = transition.getTarget().getRel();
			if (transition.getSource().equals(transition.getTarget())) {
				rel = "self";
			}
			
			String method = cs.getMethod();
			URI href = null;
			Map<String, Object> properties = new HashMap<String, Object>();
			if (map != null) {
				for (String key : map.keySet()) {
					properties.put(key, map.getFirst(key));
				}
			}
			if (entity != null) {
				if (transformer != null) {
					properties.putAll(transformer.transform(entity));
					href = linkTemplate.buildFromMap(properties);
				} else {
					href = linkTemplate.build(entity);
				}
			} else {
				href = linkTemplate.buildFromMap(properties);
			}
			Link link = new Link(linkId, rel, href.toASCIIString(), null, null, method, "label", "description", null);
			logger.debug("Created link [" + getFQResourcePath() + "] [id=" + linkId+ ", rel=" + rel + ", method=" + method + ", href=" + href.toString() + "(" + href.toASCIIString() + ")]");
			return link;
		} catch (IllegalArgumentException e) {
			logger.error("An error occurred while creating link [" +  cs.getPath() + "]", e);
			throw e;
		} catch (UriBuilderException e) {
			logger.error("An error occurred while creating link [" + cs.getPath() + "]", e);
			throw e;
		}
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
				child = new HTTPDynaRIM(null, childSM, childState, getResourceRegistry(), this.transformer, getCommandController());
			} else {
				// same entity, same transformer
				child = new HTTPDynaRIM(this, childSM, childState, this.transformer, getCommandController());
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
