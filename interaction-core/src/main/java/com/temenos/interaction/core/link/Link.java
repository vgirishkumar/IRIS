package com.temenos.interaction.core.link;

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
