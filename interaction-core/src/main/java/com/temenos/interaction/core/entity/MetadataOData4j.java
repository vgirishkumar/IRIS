package com.temenos.interaction.core.entity;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.core.ODataVersion;
import org.odata4j.core.PrefixedNamespace;
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


/**
 * This class converts a Metadata structure to odata4j's EdmDataServices.
 */
public class MetadataOData4j {
	public final static String NAMESPACE = "com.iris.metadata";
	public final static String SERVICE_NAME = "FlightResponder";
	
	private EdmDataServices edmDataServices;

	/**
	 * Construct the odata metadata
	 * @param metadata metadata
	 */
	public MetadataOData4j(Metadata metadata)
	{
		this.edmDataServices = createOData4jMetadata(metadata);
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
	public EdmDataServices createOData4jMetadata(Metadata metadata) {
		Builder mdBuilder = EdmDataServices.newBuilder();
	    List<EdmSchema.Builder> bSchemas = new ArrayList<EdmSchema.Builder>();
    	EdmSchema.Builder bSchema = new EdmSchema.Builder();
    	List<EdmEntityContainer.Builder> bEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
    	List<EdmEntitySet.Builder> bEntitySets = new ArrayList<EdmEntitySet.Builder>();
    	List<EdmEntityType.Builder> bEntityTypes = new ArrayList<EdmEntityType.Builder>();
		for(EntityMetadata entityMetadata : metadata.getEntitiesMetadata().values()) {
			List<EdmProperty.Builder> bProperties = new ArrayList<EdmProperty.Builder>();
			List<String> keys = new ArrayList<String>();
			for(String propertyName : entityMetadata.getPropertyVocabularyKeySet()) {
				//Entity properties
	    		String type = entityMetadata.getTermValue(propertyName, TermValueType.TERM_NAME);
	    		EdmType edmType;
	    		if(type.equals(TermValueType.NUMBER)) {
	    			 edmType = EdmSimpleType.INT64;
	    		}
	    		else {
	    			edmType = EdmSimpleType.STRING;
	    		}
				EdmProperty.Builder ep = EdmProperty.newBuilder(propertyName).
						setType(edmType).
						setNullable(entityMetadata.getTermValue(propertyName, TermMandatory.TERM_NAME).equals("true") ? false : true);
				bProperties.add(ep);

				//Entity keys
				if(entityMetadata.getTermValue(propertyName, TermIdField.TERM_NAME).equals("true")) {
					keys.add(propertyName);					
				}
	    	}
			EdmEntityType.Builder bEntityType = EdmEntityType.newBuilder().setNamespace(NAMESPACE).setAlias(entityMetadata.getEntityName()).setName(entityMetadata.getEntityName()).addKeys(keys).addProperties(bProperties);
    		bEntityTypes.add(bEntityType);
	    	
	    	//Create entityContainer builders
    		EdmEntitySet.Builder bEntitySet = EdmEntitySet.newBuilder().setName(SERVICE_NAME).setEntityType(bEntityType);
    		bEntitySets.add(bEntitySet);
		}
		EdmEntityContainer.Builder bEntityContainer = EdmEntityContainer.newBuilder().setName(SERVICE_NAME).addEntitySets(bEntitySets);
		bEntityContainers.add(bEntityContainer);

    	bSchema.setNamespace(NAMESPACE).setAlias(SERVICE_NAME).addEntityTypes(bEntityTypes).addEntityContainers(bEntityContainers);
    	bSchemas.add(bSchema);

		List<PrefixedNamespace> bNamespaces = new ArrayList<PrefixedNamespace>();
    	bNamespaces.add(new PrefixedNamespace("metadata", "com.iris"));
    	
	    mdBuilder.addSchemas(bSchemas).addNamespaces(bNamespaces);
		mdBuilder.setVersion(ODataVersion.V1);

		//Build the EDM metadata
		return mdBuilder.build();
	}

}
