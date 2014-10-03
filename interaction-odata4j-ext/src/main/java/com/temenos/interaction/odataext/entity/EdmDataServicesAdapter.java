package com.temenos.interaction.odataext.entity;

/*
 * #%L
 * interaction-odata4j-ext
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import org.odata4j.core.ImmutableList;
import org.odata4j.core.PrefixedNamespace;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmPropertyBase;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmStructuralType;
import org.odata4j.edm.EdmType;


/**
 * Apdator class allowing EDM meta data, which would otherwise be immutable, to be refreshed 
 *
 * @author mlambert
 *
 */
public final class EdmDataServicesAdapter extends EdmDataServices {
	private MetadataOData4j metadataOData4j;
	
	/**
	 * @param version
	 * @param schemas
	 * @param namespaces
	 */
	EdmDataServicesAdapter(MetadataOData4j metadataOData4j) {
		super(null, null, null);
		
		this.metadataOData4j = metadataOData4j;
	}
			
	@Override
	public EdmComplexType findEdmComplexType(String arg0) {
		return metadataOData4j.getEdmMetadata().findEdmComplexType(arg0);
	}

	@Override
	public EdmEntitySet findEdmEntitySet(String arg0) {					
		return metadataOData4j.getEdmMetadata().findEdmEntitySet(arg0);
	}

	@Override
	public EdmType findEdmEntityType(String typeName) {					
		return metadataOData4j.getEdmEntityTypeByTypeName(typeName);
	}

	@Override
	public EdmFunctionImport findEdmFunctionImport(String arg0) {					
		return metadataOData4j.getEdmMetadata().findEdmFunctionImport(arg0);
	}

	@Override
	public EdmPropertyBase findEdmProperty(String arg0) {					
		return metadataOData4j.getEdmMetadata().findEdmProperty(arg0);
	}

	@Override
	public EdmSchema findSchema(String arg0) {					
		return metadataOData4j.getEdmMetadata().findSchema(arg0);
	}

	@Override
	public Iterable<EdmAssociation> getAssociations() {					
		return metadataOData4j.getEdmMetadata().getAssociations();
	}

	@Override
	public Iterable<EdmComplexType> getComplexTypes() {					
		return metadataOData4j.getEdmMetadata().getComplexTypes();
	}

	@Override
	public EdmEntitySet getEdmEntitySet(EdmEntityType type) {					
		return metadataOData4j.getEdmEntitySetByType(type);
	}

	@Override
	public EdmEntitySet getEdmEntitySet(String entitySetName) {					
		return metadataOData4j.getEdmEntitySetByEntitySetName(entitySetName);
	}

	@Override
	public Iterable<EdmEntitySet> getEntitySets() {					
		return metadataOData4j.getEdmMetadata().getEntitySets();
	}

	@Override
	public Iterable<EdmEntityType> getEntityTypes() {					
		return metadataOData4j.getEdmMetadata().getEntityTypes();
	}

	@Override
	public ImmutableList<PrefixedNamespace> getNamespaces() {					
		return metadataOData4j.getEdmMetadata().getNamespaces();
	}

	@Override
	public ImmutableList<EdmSchema> getSchemas() {					
		return metadataOData4j.getEdmMetadata().getSchemas();
	}

	@Override
	public Iterable<EdmStructuralType> getStructuralTypes() {					
		return metadataOData4j.getEdmMetadata().getStructuralTypes();
	}

	@Override
	public Iterable<EdmStructuralType> getSubTypes(EdmStructuralType t) {					
		return metadataOData4j.getEdmMetadata().getSubTypes(t);
	}

	@Override
	public String getVersion() {					
		return metadataOData4j.getEdmMetadata().getVersion();
	}

	@Override
	public EdmType resolveType(String fqTypeName) {					
		return metadataOData4j.getEdmMetadata().resolveType(fqTypeName);
	}

}
