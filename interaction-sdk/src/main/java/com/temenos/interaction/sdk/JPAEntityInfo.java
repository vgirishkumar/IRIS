package com.temenos.interaction.sdk;

import java.util.List;

import org.odata4j.core.OEntity;

/**
 * A simple java class, annotated as a JPA Entity, that has been generated from an {@link OEntity}
 * @author aphethean
 *
 */
public class JPAEntityInfo {

	private String clazz;
	private String pckge;
	private FieldInfo keyInfo;
	private List<FieldInfo> properties;
	
	public JPAEntityInfo(String clazz, String pckge, FieldInfo keyInfo, List<FieldInfo> properties) {
		this.clazz = clazz;
		this.pckge = pckge;
		this.keyInfo = keyInfo;
		this.properties = properties;
	}

	/**
	 * @precondition clazz not null (under no circumstance should class be null)
	 * @postcondition returns a fully qualified class name for this entity
	 * @return
	 */
	public String getFQTypeName() {
		assert(clazz != null);
		if (pckge == null) {
			return clazz;
		} else {
			return pckge + "." + clazz;
		}
	}

	public String getClazz() {
		return clazz;
	}

	public String getPackage() {
		return pckge;
	}
	
	public FieldInfo getKeyInfo() {
		return keyInfo;
	}
	
	public List<FieldInfo> getFieldInfos() {
		return properties;
	}
	
	public String getPackageAsPath() {
		if (pckge != null) {
			return pckge.replace(".", "/");
		}
		return "";
	}
}
