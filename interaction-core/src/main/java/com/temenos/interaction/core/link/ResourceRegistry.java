package com.temenos.interaction.core.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.format.xml.XmlFormatWriter;

import com.temenos.interaction.core.state.ResourceInteractionModel;

public class ResourceRegistry {

//	private EdmDataServices edmDataServices;
	// map of resource path to interaction model
	private Map<String, ResourceInteractionModel> rimMap = new HashMap<String, ResourceInteractionModel>();
	// map of entity name to resource path
	private Map<String, String> entityResourcePathMap = new HashMap<String, String>();
	
	public ResourceRegistry() {}

	/**
	 * Construct and fill the resource registry from a single root resource.  In a RESTful interaction
	 * there should be no way to reach a resource unless it has a link from another resource.
	 * @param root
	 */
	public ResourceRegistry(ResourceInteractionModel root) {
		collectResources(root);
	}

	private void collectResources(ResourceInteractionModel resource) {
		add(resource);
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
	public ResourceRegistry(Set<ResourceInteractionModel> resources) {
		for (ResourceInteractionModel r : resources)
			add(r);
	}

	public void add(ResourceInteractionModel rim) {
		rimMap.put(rim.getFQResourcePath(), rim);
		entityResourcePathMap.put(rim.getEntityName(), rim.getFQResourcePath());
	}
	
	
	public String getEntityResourcePath(String entityName) {
		return entityResourcePathMap.get(entityName);
	}
	
	public Set<ResourceInteractionModel> getResourceInteractionModels() {
		Set<ResourceInteractionModel> resources = new HashSet<ResourceInteractionModel>();
		for (String r : rimMap.keySet()) {
			resources.add(rimMap.get(r));
		}
		return resources;
	}

	/**
	 * Where we have an OEntity, the links are generated to other entities. We
	 * just need to re write this to point to our resources.
	 * 
	 * @param entity
	 * @param currentState
	 * @return
	 */
	public OEntity rebuildOEntityLinks(OEntity entity, ResourceState currentState) {
		// these are links to associated Entities
		List<OLink> associatedLinks = new ArrayList<OLink>();

		EdmEntitySet ees = entity.getEntitySet();
		for (EdmNavigationProperty np : ees.getType().getNavigationProperties()) {
			String otherEntity = np.getToRole().getType().getName();			
			
			// TODO get navigation value, and replace {id} with navigation property value
			// TODO add our relations
	        String rel = XmlFormatWriter.related + otherEntity;

	        
			String pathOtherResource = getEntityResourcePath(otherEntity);
			if (pathOtherResource != null) {
				associatedLinks.add(OLinks.relatedEntity(rel, otherEntity, pathOtherResource));
			}
		}
		
		// these are links supplied from the producer at runtime (not defined in the associations section of the EDMX file)
		for (OLink link : entity.getLinks()) {
			System.out.println("Do something: " + link.getHref());
			throw new RuntimeException();
		}

		// these are links or forms to transition to another state
		List<OLink> transitionLinks = new ArrayList<OLink>();
		if (currentState != null) {
			Collection<ResourceState> targetStates = currentState.getAllTargets();
			for (ResourceState s : targetStates) {
				TransitionCommandSpec cs = s.getTransition(s).getCommand();
				transitionLinks.add(OLinks.relatedEntity("rel+method=" + cs.getMethod(), s.getName(), cs.getPath()));
			}
		}
		
		List<OLink> links = new ArrayList<OLink>();
		links.addAll(associatedLinks);
		links.addAll(transitionLinks);
		return OEntities.create(entity.getEntitySet(), entity.getEntityKey(), entity.getProperties(), links);
	}

}
