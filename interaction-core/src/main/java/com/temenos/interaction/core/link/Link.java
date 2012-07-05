package com.temenos.interaction.core.link;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Link {

	private final String id;
	private final String rel;
	private final String href;
	private final String[] produces;
	private final String method;
	private final String[] consumes;
	private final String description;
	private final String label;

	public Link(String id, String rel, String href, String[] consumes,
			String[] produces, String method, String label, String description,
			Class<?> templateClass) {
		this.id = id;
		this.rel = rel;
		this.href = href;
		this.consumes = consumes;
		this.produces = produces;
		this.method = method;
		this.label = label;
		this.description = description;
	}

	public String getRel() {
		return rel;
	}

	public String getMethod() {
		return method;
	}

	public String getId() {
		return id;
	}

	public String getHref() {
		return href;
	}

	/**
	 * Obtain the transition, i.e. the link relative to the REST service.
	 * 
	 * @param href Full URL
	 * @param basePath  Path to REST service
	 * @return Path of transition relative to REST service 
	 */
	public String getHrefTransition(String basePath) {
		String regex = "(?<=" + basePath + "/)\\S+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(href);
		while (m.find()) {
			return m.group();
		}		
		return href;
	}
	
	public String[] getConsumes() {
		return consumes;
	}

	public String[] getProduces() {
		return produces;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

}
