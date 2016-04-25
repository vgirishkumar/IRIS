package com.temenos.interaction.media.odata.xml.atom;

/*
 * #%L
 * interaction-media-odata-xml
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import org.odata4j.format.xml.XmlFormatWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.RESTResource;

public class ODataLinkInterceptor implements LinkInterceptor {
    
    private static final String XML_REL_SEPARATOR = " ";
    
	private final Logger logger = LoggerFactory.getLogger(ODataLinkInterceptor.class);

	// this class uses AtomXMLProvider as a helper
	private AtomXMLProvider providerHelper;
	
	public ODataLinkInterceptor(AtomXMLProvider providerHelper) {
		this.providerHelper = providerHelper;
	}
	
	@Override
	public Link addingLink(RESTResource resource, Link linkToAdd) {	
		if(resource == null) {
			return null;
		}
		
		Link result = null;
		String rel = "";
		
		if (linkToAdd != null) {
			logger.debug("Link rel["+linkToAdd.getRel()+"] title["+linkToAdd.getTitle()+"] href["+linkToAdd.getHref()+"]");
			result = linkToAdd;						
			String entitySetName = providerHelper.getEntitySet(result.getTransition().getTarget());			
			rel = getODataLinkRelation(result, entitySetName); 
					
		} else {
			logger.warn("Link to add was null for " + resource.getEntityName());
		}
		
		/*
		 * Identify 'self' link
		 */
		Link selfLink = null;
		for (Link link : resource.getLinks()) {
			if(link == null) {
				logger.warn("Found null link for " + resource.getEntityName());				
			} else {
				// prefer edit
				if ((selfLink == null && ("self".equals(link.getRel()) || "edit".equals(link.getRel())))
						|| (selfLink != null && !"edit".equals(selfLink.getRel()) && "edit".equals(link.getRel()))) {
					selfLink = link;
				}				
			}
		}
		if (selfLink != null && linkToAdd != null && !selfLink.equals(linkToAdd)
				&& (linkToAdd.getRel().equals("item") || linkToAdd.getRel().equals("collection") 
						|| linkToAdd.getRel().equals("self") || linkToAdd.getRel().equals("edit"))
				&& linkToAdd.getHref().equals(selfLink.getHref())) {
			result = null;
		}

		/*
		 * Remove duplicate links (mainly for the 'edit' case)
		 */
		if (result != null) {
			Link firstInstance = null;
			for (Link link : resource.getLinks()) {
				if(link != null) {
					// is this the first instance of this rel/id/href combination.
					if (firstInstance != null
							&& !firstInstance.equals(result)
							&& rel.equals(link.getRel())
							&& result.getHref().equals(link.getHref())
							&& result.getId().equals(link.getId())) {
						result = null;
						break;
					}
					if (result.getRel().equals(link.getRel())			        
							&& result.getHref().equals(link.getHref())
							&& result.getId().equals(link.getId())) {
						firstInstance = link;
					}
				}
			}
		}

		if (result != null) {
			result = new Link(result.getTransition(), 
					result.getTitle(), 
					rel, 
					result.getHref(), 
					result.getConsumes(), 
					result.getProduces(), 
					result.getMethod(), 
					result.getExtensions());
		}
		return result;
	}

	/**
	 * Return the OData link relation from the specified link.
	 * 
	 * rel = "item" => "relDesc"
	 * rel = "collection" => "relDesc"
	 * rel = "foo /new" => "relDesc /new"
	 * rel = "/new" => "relDesc /new"
	 * @param link link
	 * @return odata link rel
	 */
	public String getODataLinkRelation(Link link, String entitySetName) {

		if (canReturnRelUnchanged(link)) {
			return link.getRel();
		}
		String relValue = getRelValue(link.getRel());

		if (link.getTransition().isGetFromCollectionToEntityResource() && relValue.isEmpty()) {
			//Links from collection to entity resource of an entity are considered 'self' links within an odata feed
			return "self";
		}

		if (link.getTransition().getTarget() instanceof CollectionResourceState) {
			return getRelFromCollectionResourceState(link, entitySetName, relValue);
		}
		else if(link.getSourcePropertyName() != null) {
			return getRelFromResourceState(link, link.getTransition().getTarget().getEntityName(), relValue);
		} else { 
			return buildRel(XmlFormatWriter.related + link.getTransition().getTarget().getEntityName(), relValue);
		}
	}

	private String getRelFromCollectionResourceState(Link link, String entitySetName, String relValue) {
		return buildRel(XmlFormatWriter.related + resolveRelationIdentifier(link, entitySetName), relValue);
	}
	
	private String getRelFromResourceState(Link link, String entitySetName, String relValue) {
		return buildRel(XmlFormatWriter.related + resolveRelationIdentifier(link, entitySetName), relValue);
	}

	private String resolveRelationIdentifier(Link link, String entitySetName) {
		if (link.getSourcePropertyName() != null) {//For multivalue drilldown collection resource
			return link.getTransition().getSource().getEntityName() + "_" + link.getSourcePropertyName() + "/" + entitySetName;
		}
		return entitySetName;
	}

	private boolean canReturnRelUnchanged(Link link) {
		if (link.getRel().equals("self")) {
			return true;
		}
		if (link.getRel().equals("item") || link.getRel().equals("collection") || link.getRel().isEmpty()) {
			return false;
		}
		if (link.getTransition() == null || (link.getSourcePropertyName() == null)) {
			return true;
		}
		return false;
	}

	private String buildRel(String relDescription, String relValue) {
		if (relValue.isEmpty()) {
			return relDescription;
		}
		return relDescription + XML_REL_SEPARATOR + relValue;
	}

	private String getRelValue(String rel) {
		if (rel.equals("item") || rel.equals("collection")) {
			return "";
		}
		for (String relItem : rel.split(ResourceState.REL_SEPARATOR)) {
			if (!relItem.startsWith(XmlFormatWriter.related)) {
				return relItem;
			}
		}
		return "";
	}

	private String getRelDescription(String rel) {
		for (String relItem : rel.split(ResourceState.REL_SEPARATOR)) {
			if (relItem.startsWith(XmlFormatWriter.related)) {
				return relItem;
			}
		}
		return "";
	}

}