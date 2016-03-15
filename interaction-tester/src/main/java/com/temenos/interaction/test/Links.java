package com.temenos.interaction.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.temenos.interaction.test.internal.ActionableLink;
import com.temenos.interaction.test.internal.LinkWrapper;
import com.temenos.interaction.test.internal.SessionCallback;

public class Links {

	private List<Link> links = new ArrayList<Link>();
	private boolean linksNotYetMapped = true;
	private Map<String, Link> linksByRel = new HashMap<String, Link>();
	private Map<String, Link> linksByHref = new HashMap<String, Link>();
	private Map<String, Link> linksByTitle = new HashMap<String, Link>();
	private Map<String, Link> linksById = new HashMap<String, Link>();
	private SessionCallback sessionCallback;

	private Links(List<Link> links, SessionCallback sessionCallback) {
		this.sessionCallback = sessionCallback;
		this.links = links; // TODO deep copy
	}

	private Links() {
	}

	public ActionableLink byRel(String rel) {
		if (linksNotYetMapped) {
			mapLinks();
		}
		return buildLink(linksByRel.get(rel), rel);

	}

	public ActionableLink byHref(String href) {
		if (linksNotYetMapped) {
			mapLinks();
		}
		return buildLink(linksByHref.get(href), href);
	}

	public ActionableLink byTitle(String regex) {
		if (linksNotYetMapped) {
			mapLinks();
		}
		for (String title : linksByTitle.keySet()) {
			if (Pattern.compile(regex).matcher(title).matches()) {
				return new LinkWrapper(linksByTitle.get(title), sessionCallback);
			}
		}
		throw new IllegalStateException("No link found for '" + regex + "'");
	}

	public ActionableLink byId(String id) {
		if (linksNotYetMapped) {
			mapLinks();
		}
		return new LinkWrapper(linksById.get(id), sessionCallback);
	}

	public List<Link> all() {
		return links; // TODO defensive copy
	}

	public static Links create(List<Link> links, SessionCallback sessionCallback) {
		return new Links(links, sessionCallback);
	}

	public static Links empty() {
		return new Links();
	}

	private void mapLinks() {
		for (Link link : links) {
			linksByRel.put(link.rel(), link);
			if (!link.id().isEmpty()) {
				linksById.put(link.id(), link);
			}
		}
		linksNotYetMapped = false;
	}

	private ActionableLink buildLink(Link link, String key) {
		if (link != null) {
			return new LinkWrapper(link, sessionCallback);
		} else {
			throw new IllegalStateException("No link found for '" + key + "'");
		}
	}
}
