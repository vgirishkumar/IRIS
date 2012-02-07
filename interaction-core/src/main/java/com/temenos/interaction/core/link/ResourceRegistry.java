package com.temenos.interaction.core.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.format.xml.XmlFormatWriter;

import com.temenos.interaction.core.state.ResourceInteractionModel;

public class ResourceRegistry {

	private EdmDataServices edmDataServices;
	private Map<String, ResourceInteractionModel> rimMap = new HashMap<String, ResourceInteractionModel>();
	
	public void add(ResourceInteractionModel rim) {
		rimMap.put(rim.getEntityName(), rim);
	}
	
	public ResourceInteractionModel getResourceInteractionModel(String entityName) {
		return rimMap.get(entityName);
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
		for (EdmNavigationProperty np : ees.type.getNavigationProperties()) {
			if (!np.selected) {
				continue;
			}

			String otherEntity = np.name;
			// TODO add our relations
	        String rel = XmlFormatWriter.related + otherEntity;

			ResourceInteractionModel otherResource = getResourceInteractionModel(otherEntity);
			if (otherResource != null) {
				associatedLinks.add(OLinks.link(rel, otherEntity, otherResource.getResourcePath()));
			}
		}

		
		// these are links supplied from the producer at runtime (not defined in the associations section of the EDMX file)
		for (OLink link : entity.getLinks()) {
			System.out.println("Do something: " + link.getHref());
		}

		// these are links or forms to transition to another state
		List<OLink> transitionLinks = new ArrayList<OLink>();
		Collection<ResourceState> targetStates = currentState.getAllTargets();
		for (ResourceState s : targetStates) {
			TransitionCommandSpec cs = s.getTransition(s).getCommand();
			transitionLinks.add(OLinks.link("rel+method=" + cs.getMethod(), s.getName(), cs.getPath()));
		}
		
		List<OLink> links = new ArrayList<OLink>();
		links.addAll(associatedLinks);
		links.addAll(transitionLinks);
		return OEntities.create(entity.getEntitySet(), entity.getEntityKey(), entity.getProperties(), links);
	}

	public ResourceState getSimpleResourceStateModel() {
		ResourceState initialState = new ResourceState("begin", "");
		ResourceState exists = new ResourceState("exists", "{id}");
		ResourceState finalState = new ResourceState("end", "");
	
		initialState.addTransition(new TransitionCommandSpec("PUT", "{id}"), exists);		
		exists.addTransition(new TransitionCommandSpec("DELETE", "{id}"), finalState);
		return initialState;
	}
}
