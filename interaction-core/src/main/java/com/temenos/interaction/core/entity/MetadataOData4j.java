package com.temenos.interaction.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.odata4j.core.ODataVersion;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmDataServices.Builder;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;

import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermMandatory;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;


/**
 * This class converts a Metadata structure to odata4j's EdmDataServices.
 */
public class MetadataOData4j {
	private EdmDataServices edmDataServices;

	/**
	 * Construct the odata metadata
	 * @param metadata metadata
	 */
	public MetadataOData4j(Metadata metadata, ResourceStateMachine hypermediaEngine)
	{
		this.edmDataServices = createOData4jMetadata(metadata, hypermediaEngine);
	}

	/**
	 * Returns odata4j metadata
	 * @return edmdataservices object
	 */
	public EdmDataServices getMetadata() {
		return this.edmDataServices;
	}

	/**
	 * Create EDM metadata merged from multiple producers
	 * @param producers Set of odata producers
	 * @return Merged EDM metadata
	 */
	public EdmDataServices createOData4jMetadata(Metadata metadata, ResourceStateMachine hypermediaEngine) {
		String serviceName = metadata.getModelName();
		String namespace = serviceName + Metadata.MODEL_SUFFIX;
		Builder mdBuilder = EdmDataServices.newBuilder();
	    List<EdmSchema.Builder> bSchemas = new ArrayList<EdmSchema.Builder>();
    	EdmSchema.Builder bSchema = new EdmSchema.Builder();
    	List<EdmEntityContainer.Builder> bEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
    	List<EdmEntitySet.Builder> bEntitySets = new ArrayList<EdmEntitySet.Builder>();
    	Map<String, EdmEntityType.Builder> bEntityTypeMap = new HashMap<String, EdmEntityType.Builder>();
		for(EntityMetadata entityMetadata : metadata.getEntitiesMetadata().values()) {
			List<EdmProperty.Builder> bProperties = new ArrayList<EdmProperty.Builder>();
			List<String> keys = new ArrayList<String>();
			for(String propertyName : entityMetadata.getPropertyVocabularyKeySet()) {
				//Entity properties
	    		String type = entityMetadata.getTermValue(propertyName, TermValueType.TERM_NAME);
	    		EdmType edmType = termValueToEdmType(type);
				EdmProperty.Builder ep = EdmProperty.newBuilder(propertyName).
						setType(edmType).
						setNullable(entityMetadata.getTermValue(propertyName, TermMandatory.TERM_NAME).equals("true") ? false : true);
				bProperties.add(ep);

				//Entity keys
				if(entityMetadata.getTermValue(propertyName, TermIdField.TERM_NAME).equals("true")) {
					keys.add(propertyName);					
				}
	    	}
			//Add entity type
			EdmEntityType.Builder bEntityType = EdmEntityType.newBuilder().setNamespace(namespace).setAlias(entityMetadata.getEntityName()).setName(entityMetadata.getEntityName()).addKeys(keys).addProperties(bProperties);
			bEntityTypeMap.put(entityMetadata.getEntityName(), bEntityType);
		}

		for (ResourceState state : hypermediaEngine.getStates()) {
			if (state instanceof CollectionResourceState) {
				EdmEntityType.Builder entityType = bEntityTypeMap.get(state.getEntityName());
				if (entityType == null) 
					throw new RuntimeException("Entity type not found for " + state.getEntityName());
		    	//Add entity set
				EdmEntitySet.Builder bEntitySet = EdmEntitySet.newBuilder().setName(state.getName()).setEntityType(entityType);
				bEntitySets.add(bEntitySet);
			}
		}

		EdmEntityContainer.Builder bEntityContainer = EdmEntityContainer.newBuilder().setName(serviceName).setIsDefault(true).addEntitySets(bEntitySets);
		bEntityContainers.add(bEntityContainer);

		List<EdmEntityType.Builder> bEntityTypes = new ArrayList<EdmEntityType.Builder>();
		bEntityTypes.addAll(bEntityTypeMap.values());
		bSchema.setNamespace(namespace).setAlias(serviceName).addEntityTypes(bEntityTypes).addEntityContainers(bEntityContainers);
    	bSchemas.add(bSchema);

	    mdBuilder.addSchemas(bSchemas);
		mdBuilder.setVersion(ODataVersion.V1);

		//Build the EDM metadata
		return mdBuilder.build();
	}
	
	/**
	 * Convert a Metadata vocabulary TermValueType to EdmType 
	 * @param type TermValueType
	 * @return EdmType
	 */
	public static EdmType termValueToEdmType(String type) {
		EdmType edmType;
		if(type.equals(TermValueType.NUMBER)) {
			 edmType = EdmSimpleType.DOUBLE;
		}
		else if(type.equals(TermValueType.INTEGER_NUMBER)) {
			 edmType = EdmSimpleType.INT64;
		}
		else if(type.equals(TermValueType.TIMESTAMP) ||
				type.equals(TermValueType.DATE)) {
			edmType = EdmSimpleType.DATETIME;
		}
		else if(type.equals(TermValueType.TIME)) {
			edmType = EdmSimpleType.TIME;
		}
		else if(type.equals(TermValueType.BOOLEAN)) {
			edmType = EdmSimpleType.BOOLEAN;
		}
		else {
			edmType = EdmSimpleType.STRING;
		}
		return edmType;
	}

}
