package com.temenos.interaction.sdk;

public class FieldInfo {

	private String name;
	private String type;
	
	public FieldInfo(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
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
