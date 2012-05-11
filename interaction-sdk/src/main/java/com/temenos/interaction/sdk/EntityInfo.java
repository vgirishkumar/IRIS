package com.temenos.interaction.sdk;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.core.OEntity;

/**
 * A simple java class, annotated as a JPA Entity, that has been generated from an {@link OEntity}
 * @author aphethean
 *
 */
public class EntityInfo {

	private String clazz;
	private String pckge;
	private FieldInfo keyInfo;
	private List<FieldInfo> properties;
	private boolean isFeed = false;
	private boolean isJpaEntity = true;

	public EntityInfo(String clazz, String pckge, FieldInfo keyInfo, List<FieldInfo> properties) {
		this(clazz, pckge, keyInfo, properties, true);
	}
	
	public EntityInfo(String clazz, String pckge, FieldInfo keyInfo, List<FieldInfo> properties, boolean isJpaEntity) {
		this.clazz = clazz;
		this.pckge = pckge;
		this.keyInfo = keyInfo;
		this.properties = properties;
		this.isJpaEntity = isJpaEntity;
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
		List<FieldInfo> fieldsProps = new ArrayList<FieldInfo>();
		for (FieldInfo field : properties) {
			if (!field.equals(keyInfo)) {
				fieldsProps.add(field);
			}
		}

		return fieldsProps;
	}
	
	public List<FieldInfo> getAllFieldInfos() {
		return properties;
	}

	public String getPackageAsPath() {
		if (pckge != null) {
			return pckge.replace(".", "/");
		}
		return "";
	}
	
	public void setFeedEntity() {
		isFeed = true;
	}
	
	public boolean isFeedEntity() {
		return isFeed;
	}

	public boolean isJpaEntity() {
		return isJpaEntity;
	}
}
