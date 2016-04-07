package com.temenos.useragent.generic;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.temenos.useragent.generic.internal.ActionableLink;
import com.temenos.useragent.generic.internal.LinkWrapper;
import com.temenos.useragent.generic.internal.SessionContext;

/**
 * This class maps {@link Link links} against some of their attributes to
 * provide convenient access.
 * 
 * @author ssethupathi
 *
 */
public class Links {

	private List<Link> links = new ArrayList<Link>();
	private Map<String, List<Link>> linksByRel = new HashMap<String, List<Link>>();
	private Map<String, List<Link>> linksByHref = new HashMap<String, List<Link>>();
	private Map<String, List<Link>> linksByTitle = new HashMap<String, List<Link>>();
	private Map<String, List<Link>> linksById = new HashMap<String, List<Link>>();
	private Map<String, List<Link>> linksByDesc = new HashMap<String, List<Link>>();
	private SessionContext sessionContext;

	private Links(List<Link> links, SessionContext sessionCallback) {
		this.sessionContext = sessionCallback;
		for (Link link : links) {
			this.links.add(link);
		}
		mapLinks();
	}

	private Links() {
	}

	/**
	 * Returns the first {@link ActionableLink link} from the mapping for a
	 * supplied attribute <i>rel</i>.
	 * 
	 * @param rel
	 * @return {@link ActionableLink link}
	 * @throws IllegalStateException
	 *             if no mapping found for the supplied attribute
	 */
	public ActionableLink byRel(String rel) {
		return buildFromFirstLink(linksByRel.get(rel), "rel", rel);

	}

	/**
	 * Returns all the {@link ActionableLink links} from the mapping for a
	 * supplied attribute <i>rel</i>.
	 * 
	 * @param rel
	 * @return {@link ActionableLink links} list
	 */
	public List<ActionableLink> allByRel(String rel) {
		return buildFromAllLinks(linksByRel.get(rel));
	}

	/**
	 * Returns the first {@link ActionableLink link} from the mapping for a
	 * supplied attribute <i>href</i>.
	 * 
	 * @param href
	 * @return {@link ActionableLink link}
	 * @throws IllegalStateException
	 *             if no mapping found for the supplied attribute
	 */
	public ActionableLink byHref(String href) {
		return buildFromFirstLink(linksByHref.get(href), "href", href);
	}

	/**
	 * Returns all the {@link ActionableLink links} from the mapping for a
	 * supplied attribute <i>href</i>.
	 * 
	 * @param href
	 * @return {@link ActionableLink links} list
	 */
	public List<ActionableLink> allByHref(String href) {
		return buildFromAllLinks(linksByHref.get(href));
	}

	/**
	 * Returns the first {@link ActionableLink link} from the mapping for a
	 * matching supplied attribute <i>title</i>.
	 * 
	 * @param regex
	 * @return {@link ActionableLink link}
	 * @throws IllegalStateException
	 *             if no mapping found for the supplied attribute
	 */
	public ActionableLink byTitle(String regex) {
		for (String title : linksByTitle.keySet()) {
			if (Pattern.compile(regex).matcher(title).matches()) {
				List<Link> matchingLinks = linksByTitle.get(title);
				if (!matchingLinks.isEmpty()) {
					return new LinkWrapper(matchingLinks.get(0), sessionContext);
				}
			}
		}
		throw new IllegalStateException("No link found matching title '"
				+ regex + "'");
	}

	/**
	 * Returns all the {@link ActionableLink links} from the mapping for a
	 * supplied attribute <i>title</i>.
	 * 
	 * @param regex
	 * @return {@link ActionableLink links} list
	 */
	public List<ActionableLink> allByTitle(String regex) {
		List<ActionableLink> allLinks = new ArrayList<ActionableLink>();
		for (String title : linksByTitle.keySet()) {
			if (Pattern.compile(regex).matcher(title).matches()) {
				allLinks.addAll(buildFromAllLinks(linksByTitle.get(title)));
			}
		}
		return allLinks;
	}

	/**
	 * Returns the first {@link ActionableLink link} from the mapping for a
	 * supplied attribute <i>id</i>.
	 * 
	 * @param id
	 * @return {@link ActionableLink link}
	 * @throws IllegalStateException
	 *             if no mapping found for the supplied attribute
	 */
	public ActionableLink byId(String id) {
		return buildFromFirstLink(linksById.get(id), "id", id);
	}

	/**
	 * Returns all the {@link ActionableLink links} from the mapping for a
	 * supplied attribute <i>id</i>.
	 * 
	 * @param id
	 * @return {@link ActionableLink links} list
	 */
	public List<ActionableLink> allById(String id) {
		return buildFromAllLinks(linksById.get(id));
	}

	/**
	 * Returns the first {@link ActionableLink link} from the mapping for a
	 * matching supplied attribute <i>description</i>.
	 * 
	 * @param regex
	 * @return {@link ActionableLink link}
	 * @throws IllegalStateException
	 *             if no mapping found for the supplied attribute
	 */
	public ActionableLink byDescription(String regex) {
		for (String description : linksByDesc.keySet()) {
			if (Pattern.compile(regex).matcher(description).matches()) {
				List<Link> matchingLinks = linksByDesc.get(description);
				if (!matchingLinks.isEmpty()) {
					return new LinkWrapper(matchingLinks.get(0), sessionContext);
				}
			}
		}
		throw new IllegalStateException("No link found matching description '"
				+ regex + "'");
	}

	/**
	 * Returns all the {@link ActionableLink links} from the mapping for a
	 * supplied attribute <i>description</i>.
	 * 
	 * @param regex
	 * @return {@link ActionableLink links} list
	 */
	public List<ActionableLink> allByDescription(String regex) {
		List<ActionableLink> allLinks = new ArrayList<ActionableLink>();
		for (String description : linksByDesc.keySet()) {
			if (Pattern.compile(regex).matcher(description).matches()) {
				allLinks.addAll(buildFromAllLinks(linksByDesc.get(description)));
			}
		}
		return allLinks;
	}	
	
	/**
	 * Returns "read-only" view of all {@link Link links} from this mapping.
	 * 
	 * @return all links
	 */
	public List<Link> all() {
		return Collections.unmodifiableList(links);
	}

	public static Links create(List<Link> links, SessionContext sessionContext) {
		return new Links(links, sessionContext);
	}

	public static Links empty() {
		return new Links();
	}

	private void mapLinks() {
		for (Link link : links) {
			mapLinksByAttribute(link.rel(), linksByRel, link);
			mapLinksByAttribute(link.id(), linksById, link);
			mapLinksByAttribute(link.title(), linksByTitle, link);
			mapLinksByAttribute(link.href(), linksByHref, link);
			mapLinksByAttribute(link.description(), linksByDesc, link);
		}
	}

	private void mapLinksByAttribute(String attributeValue,
			Map<String, List<Link>> mapping, Link link) {
		if (attributeValue.isEmpty()) {
			return;
		}
		List<Link> links = mapping.get(attributeValue);
		if (links == null) {
			links = new ArrayList<Link>();
			links.add(link);
			mapping.put(attributeValue, links);
		} else {
			mapping.get(attributeValue).add(link);
		}
	}

	private ActionableLink buildFromFirstLink(List<Link> links,
			String attribute, String value) {
		if (links == null || links.isEmpty()) {
			throw new IllegalStateException("No link found for " + attribute
					+ " '" + value + "'");
		} else {
			return new LinkWrapper(links.get(0), sessionContext);
		}
	}

	private List<ActionableLink> buildFromAllLinks(List<Link> links) {
		List<ActionableLink> allActionableLinks = new ArrayList<ActionableLink>();
		if (links == null) {
			return allActionableLinks;
		}
		for (Link link : links) {
			allActionableLinks.add(new LinkWrapper(link, sessionContext));
		}
		return allActionableLinks;
	}
}
