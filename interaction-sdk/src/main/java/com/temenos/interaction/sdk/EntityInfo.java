package com.temenos.interaction.sdk;

/*
 * #%L
 * interaction-sdk
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
	private List<JoinInfo> joins;
	private boolean isFeed = false;
	private boolean isJpaEntity = true;

	public EntityInfo(String clazz, String pckge, FieldInfo keyInfo, List<FieldInfo> properties) {
		this(clazz, pckge, keyInfo, properties, new ArrayList<JoinInfo>(), true);
	}
	
	public EntityInfo(String clazz, String pckge, FieldInfo keyInfo, List<FieldInfo> properties, List<JoinInfo> joins, boolean isJpaEntity) {
		this.clazz = clazz;
		this.pckge = pckge;
		this.keyInfo = keyInfo;
		this.properties = properties;
		this.joins = joins;
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

	public List<JoinInfo> getJoinInfos() {
		return joins;
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
