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
	private boolean linksNotYetMapped = true;
	private Map<String, Link> linksByRel = new HashMap<String, Link>();
	private Map<String, Link> linksByHref = new HashMap<String, Link>();
	private Map<String, Link> linksByTitle = new HashMap<String, Link>();
	private Map<String, Link> linksById = new HashMap<String, Link>();
	private SessionContext sessionCallback;

	private Links(List<Link> links, SessionContext sessionCallback) {
		this.sessionCallback = sessionCallback;
		for (Link link : links) {
			this.links.add(link);
		}
	}

	private Links() {
	}

	/**
	 * Returns a {@link ActionableLink link} from the mapping for a supplied
	 * attribute <i>rel</i>.
	 * 
	 * @param rel
	 * @return {@link ActionableLink link}
	 * @throws IllegalStateException
	 *             if no mapping found for the supplied attribute
	 */
	public ActionableLink byRel(String rel) {
		if (linksNotYetMapped) {
			mapLinks();
		}
		return buildLink(linksByRel.get(rel), "rel", rel);

	}

	/**
	 * Returns a {@link ActionableLink link} from the mapping for a supplied
	 * attribute <i>href</i>.
	 * 
	 * @param href
	 * @return {@link ActionableLink link}
	 * @throws IllegalStateException
	 *             if no mapping found for the supplied attribute
	 */
	public ActionableLink byHref(String href) {
		if (linksNotYetMapped) {
			mapLinks();
		}
		return buildLink(linksByHref.get(href), "href", href);
	}

	/**
	 * Returns a {@link ActionableLink link} from the mapping for a matching
	 * supplied attribute <i>title</i>.
	 * 
	 * @param regex
	 * @return {@link ActionableLink link}
	 * @throws IllegalStateException
	 *             if no mapping found for the supplied attribute
	 */
	public ActionableLink byTitle(String regex) {
		if (linksNotYetMapped) {
			mapLinks();
		}
		for (String title : linksByTitle.keySet()) {
			if (Pattern.compile(regex).matcher(title).matches()) {
				return new LinkWrapper(linksByTitle.get(title), sessionCallback);
			}
		}
		throw new IllegalStateException("No link found matching title '"
				+ regex + "'");
	}

	/**
	 * Returns a {@link ActionableLink link} from the mapping for a supplied
	 * attribute <i>id</i>.
	 * 
	 * @param id
	 * @return {@link ActionableLink link}
	 * @throws IllegalStateException
	 *             if no mapping found for the supplied attribute
	 */
	public ActionableLink byId(String id) {
		if (linksNotYetMapped) {
			mapLinks();
		}
		return buildLink(linksById.get(id), "id", id);
	}

	/**
	 * Returns all {@link Link links} from this mapping.
	 * 
	 * @return all links
	 */
	public List<Link> all() {
		return links; // TODO defensive copy
	}

	public static Links create(List<Link> links, SessionContext sessionCallback) {
		return new Links(links, sessionCallback);
	}

	public static Links empty() {
		return new Links();
	}

	private void mapLinks() {
		for (Link link : links) {
			if (!link.rel().isEmpty()) {
				linksByRel.put(link.rel(), link);
			}
			if (!link.id().isEmpty()) {
				linksById.put(link.id(), link);
			}
			if (!link.title().isEmpty()) {
				linksByTitle.put(link.title(), link);
			}
			if (!link.href().isEmpty()) {
				linksByHref.put(link.href(), link);
			}
		}
		linksNotYetMapped = false;
	}

	private ActionableLink buildLink(Link link, String attribute, String value) {
		if (link != null) {
			return new LinkWrapper(link, sessionCallback);
		} else {
			throw new IllegalStateException("No link found for " + attribute
					+ " '" + value + "'");
		}
	}
}
