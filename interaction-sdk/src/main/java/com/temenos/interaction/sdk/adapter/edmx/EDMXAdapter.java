package com.temenos.interaction.sdk.adapter.edmx;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmAssociationEnd;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmMultiplicity;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.stax2.XMLEventReader2;
import org.odata4j.stax2.util.StaxUtil;

import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.sdk.EntityInfo;
import com.temenos.interaction.sdk.FieldInfo;
import com.temenos.interaction.sdk.JPAResponderGen;
import com.temenos.interaction.sdk.JoinInfo;
import com.temenos.interaction.sdk.adapter.InteractionAdapter;
import com.temenos.interaction.sdk.command.Commands;
import com.temenos.interaction.sdk.entity.EMEntity;
import com.temenos.interaction.sdk.entity.EMProperty;
import com.temenos.interaction.sdk.entity.EMTerm;
import com.temenos.interaction.sdk.entity.EntityModel;
import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.InteractionModel;
import com.temenos.interaction.sdk.interaction.state.IMPseudoState;
import com.temenos.interaction.sdk.util.ReferentialConstraintParser;

public class EDMXAdapter implements InteractionAdapter {

	// Dodgy, we read the Edmx contents multiple times
	private ByteArrayOutputStream bufferedEdmx;
	// original EDMX input stream
	private InputStream isEdmx;
	boolean modelsInitialised = false;
	
	private InteractionModel interactionModel;
	private EntityModel entityModel;
	private Commands commands;
	private List<EntityInfo> entitiesInfo;
	
	public EDMXAdapter(String edmxFile) {
		try {
			isEdmx = new FileInputStream(edmxFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public EDMXAdapter(InputStream metadata) {
		isEdmx = metadata;
	}
	
	private void initialise() {
		if (!modelsInitialised) {
			assert(isEdmx != null);
			// We need to read Edmx contents twice, once for odata4j parser and again for the sax parser to read ref.constraints
			try {
				bufferedEdmx = new ByteArrayOutputStream();
				byte[] buf = new byte[512];
				int len;
				while ((len = isEdmx.read(buf)) > -1 ) {
					bufferedEdmx.write(buf, 0, len);
				}
				bufferedEdmx.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			initialiseModels();
			modelsInitialised = true;
		}
	}
	
	@Override
	public InteractionModel getInteractionModel() {
		initialise();
		return interactionModel;
	}

	@Override
	public EntityModel getEntityModel() {
		initialise();
		return entityModel;
	}
	
	@Override
	public Commands getCommands() {
		initialise();
		return commands;
	}
	
	@Override
	public List<EntityInfo> getEntitiesInfo() {
		initialise();
		return entitiesInfo;
	}
	
	/**
	 * Generate entity and interaction models from an EDMX file.
	 */
	private void initialiseModels() {
		assert(bufferedEdmx != null);
		//Parse emdx file
		XMLEventReader2 reader =  StaxUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bufferedEdmx.toByteArray()))));
		EdmDataServices edmDataServices = new EdmxFormatParser().parseMetadata(reader);

		//Make sure we have at least one entity container
		if(edmDataServices.getSchemas().size() == 0 || edmDataServices.getSchemas().get(0).getEntityContainers().size() == 0) {
			throw new RuntimeException("EDMX must contain at least one entity container");
		}
		String entityContainerNamespace = edmDataServices.getSchemas().get(0).getEntityContainers().get(0).getName();

		//Create interaction model
		Map<String, String> linkPropertyMap = buildLinkPropertyMap(edmDataServices);
		Map<String, String> linkPropertyOriginMap = buildLinkPropertyOriginMap(edmDataServices);
		interactionModel = buildInteractionModel(linkPropertyMap, linkPropertyOriginMap, edmDataServices);
		
		//Create the entity model
		entityModel = new EntityModel(entityContainerNamespace);
		for (EdmEntityType entityType : edmDataServices.getEntityTypes()) {
			List<String> keys = entityType.getKeys();
			EMEntity emEntity = new EMEntity(entityType.getName());
			for (EdmProperty prop : entityType.getProperties()) {
				EMProperty emProp = JPAResponderGen.createEMProperty(prop);
				if(keys.contains(prop.getName())) {
					emProp.addVocabularyTerm(new EMTerm(TermIdField.TERM_NAME, "true"));
				}
				emEntity.addProperty(emProp);
			}
			entityModel.addEntity(emEntity);
		}
		
		//Create commands
		commands = JPAResponderGen.getDefaultCommands();
		
		//Obtain resource information
		entitiesInfo = new ArrayList<EntityInfo>();
		for (EdmEntityType t : edmDataServices.getEntityTypes()) {
			EntityInfo entityInfo = createEntityInfoFromEdmEntityType(t, linkPropertyMap);
			JPAResponderGen.addNavPropertiesToEntityInfo(entityInfo, interactionModel);
			entitiesInfo.add(entityInfo);
		}
	}

	protected Map<String, String> buildLinkPropertyMap(EdmDataServices edmDataServices) {
		Map<String, String> linkPropertyMap = new HashMap<String, String>();
		for (EdmEntitySet entitySet : edmDataServices.getEntitySets()) {
			EdmEntityType entityType = entitySet.getType();
			//Use navigation properties to define state transitions
			if(entityType.getNavigationProperties() != null) {
				for (EdmNavigationProperty np : entityType.getNavigationProperties()) {
					EdmAssociation association = np.getRelationship();
					String linkProperty = getLinkProperty(association.getName());
					linkPropertyMap.put(association.getName(), linkProperty);
				}
			}
		}
		return linkPropertyMap;
	}

	protected Map<String, String> buildLinkPropertyOriginMap(EdmDataServices edmDataServices) {
		Map<String, String> linkPropertyOriginMap = new HashMap<String, String>();
		for (EdmEntitySet entitySet : edmDataServices.getEntitySets()) {
			EdmEntityType entityType = entitySet.getType();
			//Use navigation properties to define state transitions
			if(entityType.getNavigationProperties() != null) {
				for (EdmNavigationProperty np : entityType.getNavigationProperties()) {
					EdmAssociation association = np.getRelationship();
					String linkProperty = getLinkPropertyOrigin(association.getName());
					linkPropertyOriginMap.put(association.getName(), linkProperty);
				}
			}
		}
		return linkPropertyOriginMap;
	}

	/*
	 * Build the interaction model from an EDMX model.
	 */
	protected InteractionModel buildInteractionModel(Map<String, String> linkPropertyMap, Map<String, String> linkPropertyOriginMap, EdmDataServices edmDataServices) {

		// this constructor creates all the resource state machines from the entity metadata
		InteractionModel interactionModel = new InteractionModel(edmDataServices);
		for (EdmEntitySet entitySet : edmDataServices.getEntitySets()) {
			EdmEntityType entityType = entitySet.getType();
			String entityName = entityType.getName();
			IMResourceStateMachine rsm = interactionModel.findResourceStateMachine(entityName);
			String collectionStateName = rsm.getCollectionState().getName();
			String entityStateName = rsm.getEntityState().getName();
			
			//Use navigation properties to define state transitions
			if(entityType.getNavigationProperties() != null) {
				for (EdmNavigationProperty np : entityType.getNavigationProperties()) {
					EdmAssociationEnd targetEnd = np.getToRole();
					boolean isTargetCollection = targetEnd.getMultiplicity().equals(EdmMultiplicity.MANY);
					EdmEntityType targetEntityType = targetEnd.getType();
					String targetEntityName = targetEntityType.getName();
					IMResourceStateMachine targetRsm = interactionModel.findResourceStateMachine(targetEntityName);
					
					EdmAssociation association = np.getRelationship();
					String linkProperty = linkPropertyMap.get(association.getName());

					String linkTitle = np.getName();
					String filter = null;
					if(isTargetCollection) {
						String linkPropertyOrigin = linkPropertyOriginMap.get(association.getName());
						filter = linkProperty + " eq '{" + linkPropertyOrigin + "}'";
						linkProperty = np.getName();
						rsm.addTransitionToCollectionState(entityStateName, targetRsm, np.getName(), filter, linkProperty, linkTitle);
					}
					else {
						rsm.addTransitionToEntityState(entityStateName, targetRsm, np.getName(), linkProperty, linkTitle);
					}
				}
			}
			
			// add CRUD operations for each EntitySet
			IMPseudoState pseudoState = rsm.addPseudoStateTransition(collectionStateName, "created", collectionStateName, "POST", null, "CreateEntity", null, true);
			pseudoState.addAutoTransition(rsm.getResourceState(entityStateName), "GET");
			rsm.addPseudoStateTransition(entityStateName, "updated", entityStateName, "PUT", null, "UpdateEntity", "edit", false);
			rsm.addPseudoStateTransition(entityStateName, "deleted", "DELETE", null, "DeleteEntity", "edit", false);
		}
		return interactionModel;
	}

	protected String getLinkProperty(String associationName) {
		return ReferentialConstraintParser.getDependent(associationName, new ByteArrayInputStream(bufferedEdmx.toByteArray()));
	}

	protected String getLinkPropertyOrigin(String associationName) {
		return ReferentialConstraintParser.getPrincipal(associationName, new ByteArrayInputStream(bufferedEdmx.toByteArray()));
	}
	
	public EntityInfo createEntityInfoFromEdmEntityType(EdmType type, Map<String, String> linkPropertyMap) {
		if (!(type instanceof EdmEntityType))
			return null;
		
		EdmEntityType entityType = (EdmEntityType) type;
		// think OData4j only support single keys at the moment
		FieldInfo keyInfo = null;
		if (entityType.getKeys().size() > 0) {
			String keyName = entityType.getKeys().get(0);
			EdmType key = null;
			for (EdmProperty e : entityType.getProperties()) {
				if (e.getName().equals(keyName)) {
					key = e.getType();
				}
			}
			keyInfo = new FieldInfo(keyName, JPAResponderGen.javaType(key), null);
		}
		
		List<FieldInfo> properties = new ArrayList<FieldInfo>();
		for (EdmProperty property : entityType.getProperties()) {
			// add additional configuration by annotations
			List<String> annotations = new ArrayList<String>();
			if (property.getType().equals(EdmSimpleType.DATETIME)) {
				annotations.add("@Temporal(TemporalType.TIMESTAMP)");
			} else if (property.getType().equals(EdmSimpleType.TIME)) {
				annotations.add("@Temporal(TemporalType.TIME)");
			}

			FieldInfo field = new FieldInfo(property.getName(), JPAResponderGen.javaType(property.getType()), annotations);
			properties.add(field);
		}

		List<JoinInfo> joins = new ArrayList<JoinInfo>();
		for (EdmNavigationProperty navProperty : entityType.getNavigationProperties()) {
			// build the join annotations
			List<String> annotations = new ArrayList<String>();
			// lets see if the target has a navproperty to this type
			boolean bidirectional = false;
			for (EdmNavigationProperty np : navProperty.getToRole().getType().getNavigationProperties()) {
				if (np.getRelationship().getName().equals(navProperty.getRelationship().getName())) {
					bidirectional = true;;
				}
			}
			
			String mappedBy = "";
			if (bidirectional) {
				String linkProperty = linkPropertyMap.get(navProperty.getRelationship().getName());
				mappedBy = "(mappedBy=\"" + linkProperty + "\")";
			}
			
			EdmAssociationEnd from = navProperty.getFromRole();
			if ((from.getMultiplicity().equals(EdmMultiplicity.ONE) || from.getMultiplicity().equals(EdmMultiplicity.ZERO_TO_ONE)) 
					&& navProperty.getToRole().getMultiplicity().equals(EdmMultiplicity.MANY)) {
				annotations.add("@OneToMany" + mappedBy);
			} else if (from.getMultiplicity().equals(EdmMultiplicity.MANY) 
					&& navProperty.getToRole().getMultiplicity().equals(EdmMultiplicity.MANY)) {
				annotations.add("@ManyToMany" + mappedBy);
			}
			if (annotations.size() > 0) {
				JoinInfo join = new JoinInfo(navProperty.getName(), navProperty.getToRole().getType().getName(), annotations);
				joins.add(join);
			}
		}
		
		
		//Check if user has specified the name of the JPA entities
		String jpaNamespace = System.getProperty("jpaNamespace");
		boolean isJpaEntity = (jpaNamespace == null || jpaNamespace.equals(entityType.getNamespace()));
		return new EntityInfo(entityType.getName(), entityType.getNamespace(), keyInfo, properties, joins, isJpaEntity);
	}

}
