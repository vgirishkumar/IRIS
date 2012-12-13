package com.temenos.interaction.core.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.HttpMethod;

import org.odata4j.core.ODataVersion;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmDataServices.Builder;
import org.odata4j.edm.EdmAssociationEnd;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmMultiplicity;
import org.odata4j.edm.EdmNavigationProperty;
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
import com.temenos.interaction.core.hypermedia.Transition;


/**
 * This class converts a Metadata structure to odata4j's EdmDataServices.
 */
public class MetadataOData4j {

	private final static String MULTI_NAV_PROP_TO_ENTITY = "MULTI_NAV_PROP";

	private EdmDataServices edmDataServices;

	/**
	 * Construct the odata metadata
	 * @param metadata metadata
	 */
	public MetadataOData4j(Metadata metadata, ResourceStateMachine hypermediaEngine)
	{
		assert(!(hypermediaEngine.getInitial() instanceof CollectionResourceState)) : "Initial state must be an individual resource state";
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
    	Map<String, EdmEntityType.Builder> bEntityTypeMap = new HashMap<String, EdmEntityType.Builder>();
		Map<String, EdmEntitySet.Builder> bEntitySetMap = new HashMap<String, EdmEntitySet.Builder>();
		Map<String, EdmFunctionImport.Builder> bFunctionImportMap = new HashMap<String, EdmFunctionImport.Builder>();
		List<EdmAssociation.Builder> bAssociations = new ArrayList<EdmAssociation.Builder>();
		
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
			
			// Add entity type
			EdmEntityType.Builder bEntityType = EdmEntityType.newBuilder().setNamespace(namespace).setAlias(entityMetadata.getEntityName()).setName(entityMetadata.getEntityName()).addKeys(keys).addProperties(bProperties);
			bEntityTypeMap.put(entityMetadata.getEntityName(), bEntityType);
		}

		// Add Navigation Properties
		for (EdmEntityType.Builder bEntityType : bEntityTypeMap.values()) {
			// build associations
			Map<String, EdmAssociation.Builder> bAssociationMap = buildAssociations(namespace, bEntityType, bEntityTypeMap, hypermediaEngine);
			bAssociations.addAll(bAssociationMap.values());
			
			for (String navPropertyName : bAssociationMap.keySet()) {
				EdmAssociation.Builder relationship = bAssociationMap.get(navPropertyName);
				bEntityType.addNavigationProperties(EdmNavigationProperty
						.newBuilder(navPropertyName)
						.setRelationship(relationship)
						.setFromTo(relationship.getEnd1(), relationship.getEnd2()));
			}
		}
		
		// Index EntitySets by Entity name
		for (ResourceState state : hypermediaEngine.getInitial().getAllTargets()) {
			if (state instanceof CollectionResourceState) {
				EdmEntityType.Builder entityType = bEntityTypeMap.get(state.getEntityName());
				if (entityType == null) 
					throw new RuntimeException("Entity type not found for " + state.getEntityName());
				Transition fromInitialState = hypermediaEngine.getInitial().getTransition(state);
				if (fromInitialState != null) {
			    	//Add entity set
					EdmEntitySet.Builder bEntitySet = EdmEntitySet.newBuilder().setName(state.getName()).setEntityType(entityType);
					bEntitySetMap.put(state.getEntityName(), bEntitySet);
				}
			}
		}
		
		for (ResourceState state : hypermediaEngine.getStates()) {
			if (state instanceof CollectionResourceState) {
				Transition fromInitialState = hypermediaEngine.getInitial().getTransition(state);
				if (fromInitialState == null) {
					EdmEntitySet.Builder bEntitySet = bEntitySetMap.get(state.getEntityName());
					// Add Function
					EdmFunctionImport.Builder bFunctionImport = EdmFunctionImport.newBuilder()
							.setName(state.getName())
							.setEntitySet(bEntitySet)
							.setHttpMethod(HttpMethod.GET)
							.setIsCollection(true)
							.setReturnType(bEntityTypeMap.get(state.getEntityName()));
					bFunctionImportMap.put(state.getName(), bFunctionImport);
				}
			}
		}

		EdmEntityContainer.Builder bEntityContainer = EdmEntityContainer.newBuilder()
				.setName(serviceName)
				.setIsDefault(true)
				.addEntitySets(new ArrayList<EdmEntitySet.Builder>(bEntitySetMap.values()))
				.addFunctionImports(new ArrayList<EdmFunctionImport.Builder>(bFunctionImportMap.values()));
		bEntityContainers.add(bEntityContainer);

		List<EdmEntityType.Builder> bEntityTypes = new ArrayList<EdmEntityType.Builder>();
		bEntityTypes.addAll(bEntityTypeMap.values());
		bSchema
			.setNamespace(namespace)
			.setAlias(serviceName)
			.addEntityTypes(bEntityTypes)
			.addAssociations(bAssociations)
			.addEntityContainers(bEntityContainers);
    	bSchemas.add(bSchema);

	    mdBuilder.addSchemas(bSchemas);
		mdBuilder.setVersion(ODataVersion.V1);

		//Build the EDM metadata
		return mdBuilder.build();
	}

	private Map<String, EdmAssociation.Builder> buildAssociations(String namespace, EdmEntityType.Builder entityType, Map<String, EdmEntityType.Builder> bEntityTypeMap, ResourceStateMachine hypermediaEngine) {
		// Obtain the relation between entities and write navigation properties
		Map<String, EdmAssociation.Builder> bAssociationMap = new HashMap<String, EdmAssociation.Builder>();
		//Map<Association name, Entity relation>
		Map<String, EdmAssociation.Builder> relations = new HashMap<String, EdmAssociation.Builder>();

		String entityName = entityType.getName();
		Collection<Transition> entityTransitions = hypermediaEngine.getTransitionsById().values();
		if (entityTransitions != null) {
			//Find out which target entities have more than one transition from this state
	        Set<String> targetStateNames = new HashSet<String>();
			Map<String, String> multipleNavPropsToEntity = new HashMap<String, String>();		//Map<TargetEntityName, TargetStateName>
			for(Transition entityTransition : entityTransitions) {
				if (entityTransition.getSource().getEntityName().equals(entityName) 
						&& !entityTransition.getTarget().isPseudoState()) {
					String targetEntityName = entityTransition.getTarget().getEntityName();
					String targetStateName = entityTransition.getTarget().getName();
					String lastTargetStateName = multipleNavPropsToEntity.get(targetEntityName);
					if(lastTargetStateName == null) {
						multipleNavPropsToEntity.put(targetEntityName, targetStateName);
						targetStateNames.add(entityTransition.getTarget().getName());
					}
					else if(!targetStateName.equals(lastTargetStateName)) {		//Disregard transitions from multiple source states
						multipleNavPropsToEntity.put(targetEntityName, MULTI_NAV_PROP_TO_ENTITY);		//null indicates to generate multiple navigation properties 
					}
				}
			}

			//Create navigation properties from transitions
	        Set<String> npNames = new HashSet<String>();
			for(Transition entityTransition : entityTransitions) {
				ResourceState sourceState = entityTransition.getSource();
				ResourceState targetState = entityTransition.getTarget();
				String npName = targetState.getName();
				if (sourceState.getEntityName().equals(entityName) 
						&& !entityTransition.getTarget().isPseudoState()
						&& !npNames.contains(npName)
						&& !(entityTransition.getSource() instanceof CollectionResourceState)) {		//We can have transitions to a resource state from multiple source states
					EdmMultiplicity multiplicity = EdmMultiplicity.ONE;
					if (targetState instanceof CollectionResourceState) {
						multiplicity = EdmMultiplicity.MANY;
					}
	
					// Use the entity names to define the relation
					String relationName;
					if(multipleNavPropsToEntity.get(targetState.getEntityName()).equals(MULTI_NAV_PROP_TO_ENTITY)) {
						//More than one transition => use separate associations "sourceEntityName_navPropName"
						relationName = sourceState.getEntityName() + "_" + targetState.getName();
					}
					else {
						//Only one transition => use single association "sourceEntityName_targetEntityName"
						relationName = sourceState.getEntityName() + "_" + targetState.getEntityName();
						String invertedRelationName = targetState.getEntityName() + "_" + sourceState.getEntityName();
						if(relations.containsKey(invertedRelationName)) {
							relationName = invertedRelationName;					
						}
					}
					
					// Association
					EdmAssociationEnd.Builder sourceRole = EdmAssociationEnd.newBuilder()
							.setRole(relationName + "_Source")
							.setType(entityType)
							.setMultiplicity(EdmMultiplicity.MANY);
					EdmEntityType.Builder targetEntityType = bEntityTypeMap.get(targetState.getEntityName());
					assert(targetEntityType != null);
					EdmAssociationEnd.Builder targetRole = EdmAssociationEnd.newBuilder()
							.setRole(relationName + "_Target")
							.setType(targetEntityType)
							.setMultiplicity(multiplicity);
					EdmAssociation.Builder bAssociation = EdmAssociation.newBuilder()
							.setNamespace(namespace)
							.setName(relationName)
							.setEnds(sourceRole, targetRole);
					
					bAssociationMap.put(targetState.getName(), bAssociation);
					if (!relations.containsKey(relationName)) {
			            relations.put(relationName, bAssociation);
					}
				}
			}
		}
		return bAssociationMap;
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
