package com.temenos.interaction.core.media;

import java.util.Collection;
import java.util.Map;

import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.EntityResource;

/**
 * Entity resource wrapper classes which exposes a link to itself. 
 */
public class EntityResourceWrapper {
	protected EntityResource<Map<String, Object>> entityResource;
	protected Link entityGetLink;
	protected Link entityUpdateLink;
	
	public EntityResourceWrapper(EntityResource<Map<String, Object>> entityResource) {
		this.entityResource = entityResource;
		entityGetLink = findEntityGetLink(entityResource.getLinks());
		entityUpdateLink = findEntityGetLink(entityResource.getLinks());
	}
	
	public EntityResource<Map<String, Object>> getResource() {
		return entityResource;
	}

	public Link getEntityGetLink() {
		return entityGetLink;
	}

	public Link getEntityUpdateLink() {
		return entityUpdateLink;
	}
	
	protected Link findEntityGetLink(Collection<Link> links) {
		Link selfLink = null;
		if (links != null) {
			for (Link l : links) {
				Transition t = l.getTransition();
				if (l.getRel().equals("self") || t != null &&
						t.getSource().getEntityName().equals(t.getTarget().getEntityName()) &&
						t.getSource().getRel().equals("collection") &&
						t.getTarget().getRel().equals("item")) {
					selfLink = l;
					break;
				}
			}
		}
		return selfLink;
	}
}
