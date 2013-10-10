package com.temenos.interaction.core.resource;

/*
 * #%L
 * interaction-core
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


import java.util.Collection;

import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.temenos.interaction.core.hypermedia.Link;

/**
 * A MetaDataResource is resource that describes another resource.
 * @author aphethean
 */
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetaDataResource<T> extends EntityResource<T> {
	@XmlAnyElement(lax=true)
	private T metadata; 

	public MetaDataResource(T metadata) {
		this.metadata = metadata;
	}
	
	public T getMetadata() {
		return metadata;
	}
	
	@Override
	public GenericEntity<EntityResource<T>> getGenericEntity() {
		return new GenericEntity<EntityResource<T>>(this, this.getClass().getGenericSuperclass());
	}
	
	@Override
    public Collection<Link> getLinks() {
    	return null;
    }
	@Override
	public void setLinks(Collection<Link> links) {}

	@Override
	public String getEntityName() {
		return null;
	}
	@Override
	public void setEntityName(String entityName) {}
	
}
