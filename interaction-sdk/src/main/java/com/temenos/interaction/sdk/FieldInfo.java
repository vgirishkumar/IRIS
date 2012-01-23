package com.temenos.interaction.sdk;

import java.util.List;

public class FieldInfo {

	private String name;
	private String type;
	private List<String> annotations;
	
	public FieldInfo(String name, String type, List<String> annotations) {
		this.name = name;
		this.type = type;
		this.annotations = annotations;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public List<String> getAnnotations() {
		return annotations;
	}
	
	public boolean equals(Object other) {
		if (other instanceof FieldInfo) {
			FieldInfo theOther = (FieldInfo) other;
			if ((theOther.getName() != null && this.name != null) && theOther.getName().equals(this.name)) {
				if ((theOther.getType() != null && this.type != null) && theOther.getType().equals(this.type)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;
	}
}
