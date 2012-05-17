package com.temenos.interaction.core.link;

import java.util.Map;

import com.jayway.jaxrs.hateoas.HateoasLink;
import com.jayway.jaxrs.hateoas.HateoasVerbosity;

public class Link implements HateoasLink {

	private final String id;
	private final String rel;
	private final String href;
	private final String[] produces;
	private final String method;
	private final Class<?> templateClass;
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
		this.templateClass = templateClass;
	}

	
	@Override
	public String getRel() {
		return rel;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getHref() {
		return href;
	}

	@Override
	public String[] getConsumes() {
		return consumes;
	}

	@Override
	public String[] getProduces() {
		return produces;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Class<?> getTemplateClass() {
		// TODO remove this unused method, don't extend HateoasLink from jax-rs-hateoas?
		return templateClass;
	}

	@Override
	public Map<String, Object> toMap(HateoasVerbosity verbosity) {
		// TODO remove this unused method, don't extend HateoasLink from jax-rs-hateoas?
		return null;
	}

}
