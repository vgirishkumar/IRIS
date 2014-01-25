package com.temenos.ebank.domain;

import java.io.Serializable;

public class Nomencl implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String language;
	private String groupCode;
	private String code;
	private String label;
	private Integer sortOrder;

	public Nomencl() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String group) {
		this.groupCode = group;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}
