package com.temenos.interaction.core.media;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.core4j.Enumerable;
import org.odata4j.core.ImmutableList;
import org.odata4j.core.ODataVersion;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmDataServices.Builder;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.producer.ODataProducer;


/**
 * This class provides EDM metadata merged from multiple OData producers.
 * It enables a service providing REST resources to multiple resource managers
 * to expose a single service document and single metadata document.
 */
public class ODataMetadata {

	private EdmDataServices mdMerged = null;
	private List<EdmDataServices> metadataProducers = new ArrayList<EdmDataServices>();

	/**
	 * Construct the odata metadata merged from multiple OData producers
	 * @param producers Set of OData producers
	 */
	public ODataMetadata(Set<ODataProducer> producers)
	{
		if(producers != null) {
			for(ODataProducer producer : producers) {
				metadataProducers.add(producer.getMetadata());				
			}
		}
		mdMerged = mergeMetadata(metadataProducers);
	}

	/**
	 * Construct the odata metadata merged from multiple EdmDataServices
	 * @param producers Set of OData producers
	 */
	public ODataMetadata(List<EdmDataServices> metadataProducers)
	{
		mdMerged = mergeMetadata(metadataProducers);
	}
	
	public EdmDataServices getMetadata() {
		return this.mdMerged;
	}

	/**
	 * Create EDM metadata merged from multiple producers
	 * @param producers Set of odata producers
	 * @return Merged EDM metadata
	 */
	public EdmDataServices mergeMetadata(List<EdmDataServices> metadataProducers) {
		if(metadataProducers.size() == 1) {
			//Only one producer
			return metadataProducers.get(0);
		}
		else {
			//Multiple producers => merge metadata documents
			ODataVersion odataVersion = null;
			Builder mdBuilder = EdmDataServices.newBuilder();
			for(EdmDataServices md : metadataProducers) {
			    List<EdmSchema.Builder> bSchemas = new ArrayList<EdmSchema.Builder>();
			    ImmutableList<EdmSchema> schemas = md.getSchemas();
			    if(schemas != null) {
				    for (EdmSchema schema : schemas) {
				    	//BuildContext is private so create our own EdmSchema builder
				    	EdmSchema.Builder bSchema = createSchemaBuilder(schema);
				    	bSchemas.add(bSchema);
				    }
			    }
			    mdBuilder.addSchemas(bSchemas).addNamespaces(md.getNamespaces());
			    
			    //Use the highest OData version
			    ODataVersion version;
			    try {
			    	version = ODataVersion.parse(md.getVersion());
			    }
			    catch(Exception e) {
			    	version = ODataVersion.V1;
			    }
			    if(odataVersion == null || ODataVersion.isVersionGreaterThan(version, odataVersion)) {
			    	odataVersion = version;
			    }
			}
			mdBuilder.setVersion(odataVersion);
	
			//Build the EDM metadata
			return mdBuilder.build();
		}
	}
	
	private EdmSchema.Builder createSchemaBuilder(EdmSchema schema) {
    	//Create entityType builders
    	List<EdmEntityType.Builder> bEntityTypes = new ArrayList<EdmEntityType.Builder>();
	    List<EdmEntityType> entityTypes = schema.getEntityTypes();
	    if(entityTypes != null) {
	    	for(EdmEntityType entityType : entityTypes) {
				List<EdmProperty.Builder> bProperties = new ArrayList<EdmProperty.Builder>();
		    	for(EdmProperty property : entityType.getProperties()) {
					EdmProperty.Builder ep = EdmProperty.newBuilder(property.getName()).setType(property.getType());
					bProperties.add(ep);
		    	}
				EdmEntityType.Builder bEntityType = EdmEntityType.newBuilder().setNamespace(entityType.getNamespace()).setAlias(entityType.getAlias()).setName(entityType.getName()).addKeys(entityType.getKeys()).addProperties(bProperties);
	    		bEntityTypes.add(bEntityType);
	    	}
	    }
    	
    	//Create entityContainer builders
    	List<EdmEntityContainer.Builder> bEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
	    List<EdmEntityContainer> entityContainers = schema.getEntityContainers();
	    if(entityContainers != null) {
	    	for(EdmEntityContainer entityContainer : entityContainers) {
		    	List<EdmEntitySet.Builder> bEntitySets = new ArrayList<EdmEntitySet.Builder>();
			    List<EdmEntitySet> entitySets = entityContainer.getEntitySets();
			    if(entitySets != null) {
			    	for(EdmEntitySet entitySet : entitySets) {
			    		EdmEntityType entityType = entitySet.getType();
						List<EdmProperty.Builder> bProperties = new ArrayList<EdmProperty.Builder>();
					    Enumerable<EdmProperty> properties = entityType.getProperties();
					    if(properties != null) {
					    	for(EdmProperty property : properties) {
								EdmProperty.Builder ep = EdmProperty.newBuilder(property.getName()).setType(property.getType());
								bProperties.add(ep);
					    	}
					    }
						EdmEntityType.Builder bEntityType = EdmEntityType.newBuilder().setNamespace(entityType.getNamespace()).setAlias(entityType.getAlias()).setName(entityType.getName()).addKeys(entityType.getKeys()).addProperties(bProperties);
			    		EdmEntitySet.Builder bEntitySet = EdmEntitySet.newBuilder().setName(entitySet.getName()).setEntityType(bEntityType);
			    		bEntitySets.add(bEntitySet);
			    	}
			    }
	    		EdmEntityContainer.Builder bEntityContainer = EdmEntityContainer.newBuilder().setName(entityContainer.getName()).addEntitySets(bEntitySets);
	    		bEntityContainers.add(bEntityContainer);
	    	}			 
	    }
    	
    	//Create the schema builder
    	EdmSchema.Builder bSchema = new EdmSchema.Builder();
    	bSchema
          .setNamespace(schema.getNamespace())
          .setAlias(schema.getAlias())
          .addEntityTypes(bEntityTypes)
          .addEntityContainers(bEntityContainers);
    	return bSchema;
	}
}
