package com.temenos.interaction.core.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmMultiplicity;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.format.xml.XmlFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jaxrs.hateoas.HateoasContext;
import com.jayway.jaxrs.hateoas.LinkableInfo;
import com.temenos.interaction.core.dynaresource.HTTPDynaRIM;
import com.temenos.interaction.core.state.ResourceInteractionModel;

public class ResourceRegistry implements HateoasContext {
	private final static Logger logger = LoggerFactory.getLogger(ResourceRegistry.class);

	// the resource metadata
	private EdmDataServices edmDataServices;
	// map of resource path to interaction model
	private Map<String, HTTPDynaRIM> rimMap = new HashMap<String, HTTPDynaRIM>();
	// map of entity name to resource path
	private Map<String, String> entityResourcePathMap = new HashMap<String, String>();
	// map of resource state to resource path
	private Map<ResourceState, String> statePathMap = new HashMap<ResourceState, String>();
	// map of link key to transition
	private Map<String, Transition> linkTransitionMap = new HashMap<String, Transition>();
	
	/**
	 * Construct and fill the resource registry from a single root resource.  In a RESTful interaction
	 * there should be no way to reach a resource unless it has a link from another resource.
	 * @param root
	 */
	public ResourceRegistry(EdmDataServices edmDataServices, HTTPDynaRIM root) {
		assert(edmDataServices != null);
		this.edmDataServices = edmDataServices;
		collectResources(root);
	}

	private void collectResources(ResourceInteractionModel resource) {
		// the registry can only be constructed with HTTPDynaRIM, no way to have children of any other type
		assert(resource instanceof HTTPDynaRIM);
		add((HTTPDynaRIM) resource);
		for (ResourceInteractionModel r : resource.getChildren()) {
			if (!rimMap.containsKey(r.getFQResourcePath())) {
				collectResources(r);
			}
		}
	}
	
	/**
	 * Construct and fill the resource registry with a set of resources.
	 * @param resources
	 */
	public ResourceRegistry(EdmDataServices edmDataServices, Set<HTTPDynaRIM> resources) {
		assert(edmDataServices != null);
		this.edmDataServices = edmDataServices;
		for (HTTPDynaRIM r : resources)
			add(r);
	}

	public void add(HTTPDynaRIM rim) {
		logger.debug("Registering new resource " + rim);
		// TODO dodgy - need to change this to a context object that can be initialised and passed to all dynamic resources
		rim.setResourceRegistry(this);
		rimMap.put(rim.getFQResourcePath(), rim);
		
		// populate a map of resources and their paths, and resource states and their paths 
		ResourceStateMachine stateMachine = rim.getStateMachine();

		// do not update entity resource path if this resource is a child state
		if (stateMachine.getInitial().equals(rim.getCurrentState())) {
			
			
			entityResourcePathMap.put(rim.getCurrentState().getEntityName(), rim.getFQResourcePath());
		}
		/*
		 *  create the state-path map for "self state" and child states, not necessary to
		 *  populate the map with other resource states as these RIMs will be added
		 *  here also 
		 */
		Transition selfTransition = new Transition(null, new TransitionCommandSpec("GET", rim.getFQResourcePath()), rim.getCurrentState());
		Transition previousSelfTransitionValue = linkTransitionMap.put(rim.getCurrentState().getId(), selfTransition);
		if (previousSelfTransitionValue != null && !previousSelfTransitionValue.equals(selfTransition)) {
			logger.warn("We are replacing the link registered for a transition to 'self':  previous [" + previousSelfTransitionValue.getCommand() + "], new [" + selfTransition.getCommand() + "]");
		}
		statePathMap.put(rim.getCurrentState(), rim.getFQResourcePath());
		Collection<ResourceState> resourceStates = rim.getCurrentState().getAllTargets();
		for (ResourceState childState : resourceStates) {
			if (childState.isSelfState()) {
				statePathMap.put(childState, rim.getFQResourcePath());
			} else if (!childState.getEntityName().equals(rim.getCurrentState().getEntityName())) {
				statePathMap.put(childState, childState.getPath());
			}
		}
		
		
		/* 
		 * test if current state of resource has been supplied, HTTPDynaRIM could be
		 * constructed without a state model, and this registry could be filled with
		 * a set of resources that have no state model
		 */
		if (rim.getCurrentState() != null) {
			for (ResourceState targetState : rim.getCurrentState().getAllTargets()) {
				// linkKey = the target entity name and state name
				String linkKey = rim.getCurrentState().getId() + "." + targetState.getId();
				Transition transition = rim.getCurrentState().getTransition(targetState);
				linkTransitionMap.put(linkKey, transition);
			}
		}
	}
	
	public EdmEntitySet getEntitySet(String entitySetName) {
		return edmDataServices.getEdmEntitySet(entitySetName);
	}
	
	public String getEntityResourcePath(String entityName) {
		return entityResourcePathMap.get(entityName);
	}
	
	/**
	 * When supplied with a uri of a registered resource you will be returned a 
	 * reference to the ResourceInteractionModel.
	 * @param path
	 * 		The base uri for the resource.
	 * @return {@link ResourceInteractionModel}
	 */
	public ResourceInteractionModel getResourceInteractionModel(String path) {
		return rimMap.get(path);
	}
	
	public Set<ResourceInteractionModel> getResourceInteractionModels() {
		Set<ResourceInteractionModel> resources = new HashSet<ResourceInteractionModel>();
		for (String r : rimMap.keySet()) {
			resources.add(rimMap.get(r));
		}
		return resources;
	}

	/**
	 * Create the links from this entity to view other entities (when used in this 
	 * way these links control the application state).
	 * 
	 * @param entity
	 * @param currentState
	 * @return
	 * @precondition non null entityTypeName
	 * @precondition a resource for all associated entities must have been 
	 * 		previously added {@link ResourceRegistry.add())} to this registry
	 * @postcondition a list of the supplied entity types' links to other entities
	 * @invariant the resource registry will not be modified
	 */
	public List<OLink> getNavigationLinks(EdmEntityType entityType) {
		assert(entityType != null);
		// TODO change test so we can enable this assertion
//		assert(entityResourcePathMap.get(entity.getEntitySetName()) != null);
		// these are links to associated Entities
		List<OLink> associatedLinks = new ArrayList<OLink>();

		for (EdmNavigationProperty np : entityType.getNavigationProperties()) {
			EdmEntityType otherType = np.getToRole().getType();
	        String otherEntitySetName = edmDataServices.getEdmEntitySet(otherType).getName();
			
			// TODO get navigation value, and replace {id} with navigation property value
			// TODO add our relations
	        String rel = XmlFormatWriter.related + otherEntitySetName;

			String pathOtherResource = getEntityResourcePath(otherEntitySetName);
			if (pathOtherResource != null) {
	            if (np.getToRole().getMultiplicity() == EdmMultiplicity.MANY) {
					associatedLinks.add(OLinks.relatedEntities(rel, otherEntitySetName, pathOtherResource));
	              } else {
	  				associatedLinks.add(OLinks.relatedEntity(rel, otherEntitySetName, pathOtherResource));
	              }
			}
		}
		
		// these are links or forms to transition to another state
		List<OLink> transitionLinks = new ArrayList<OLink>();
/*
		if (currentState != null) {
			Collection<ResourceState> targetStates = currentState.getAllTargets();
			for (ResourceState s : targetStates) {
				TransitionCommandSpec cs = s.getTransition(s).getCommand();
				transitionLinks.add(OLinks.relatedEntity("rel+method=" + cs.getMethod(), s.getName(), cs.getPath()));
			}
		}
*/
		
		List<OLink> links = new ArrayList<OLink>();
		links.addAll(associatedLinks);
		links.addAll(transitionLinks);
		return links;
	}

	@Override
	public void mapClass(Class<?> clazz) {
		// we do not implement this part of HateoasContext
		assert(false);
	}

	@Override
	public LinkableInfo getLinkableInfo(String linkKey) {
		Transition transition = linkTransitionMap.get(linkKey);
		// no transition, must be a transition to self
		String fqPath = (transition != null ? statePathMap.get(transition.getTarget()) :  entityResourcePathMap.get(linkKey));
		// there should not be any way to define linkKey's without defining a transition and resource
		assert(fqPath != null);
		ResourceInteractionModel rim = rimMap.get(fqPath);
		assert(rim != null);
		
		// TODO need to lookup from link registry, mock up GET link for now
		String label = "lookup label from EDMX";  // TODO get from entityDataServices
		String description = "lookup description from EDMX";  // TODO get from entityDataServices, in Accept-Language
		String method = (transition != null ? transition.getCommand().getMethod() : "GET");
		String path = (transition != null && transition.getCommand().getPath() != null ? transition.getCommand().getPath() : rim.getFQResourcePath());
		LinkableInfo link = new LinkableInfo(linkKey, path, method, null, null, label, description, null);
		
		assert(link != null);
		return link;
	}

}
