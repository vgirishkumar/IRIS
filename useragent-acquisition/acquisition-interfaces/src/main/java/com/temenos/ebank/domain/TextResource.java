package com.temenos.ebank.domain;

public class TextResource implements java.io.Serializable {

	private static final long serialVersionUID = -8383547227585000671L;

	private Long id;
	private String parent;
	private String locale = "en";
	private String style;
	private String key;
	private String value;

	public TextResource() {
	}

	public TextResource(Long id, String parent, String key) {
		this.id = id;
		this.parent = parent;
		this.key = key;
	}

	public TextResource(Long id, String parent, String locale, String style, String key, String value) {
		super();
		this.id = id;
		this.parent = parent;
		this.locale = locale;
		this.style = style;
		this.key = key;
		this.value = value;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getParent() {
		return parent;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLocale() {
		return locale;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
