package com.temenos.interaction.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmAssociationEnd;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmMultiplicity;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.stax2.XMLEventReader2;

import com.temenos.interaction.sdk.command.Commands;
import com.temenos.interaction.sdk.entity.EMEntity;
import com.temenos.interaction.sdk.entity.EMProperty;
import com.temenos.interaction.sdk.entity.EMTerm;
import com.temenos.interaction.sdk.entity.EntityModel;
import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.InteractionModel;
import com.temenos.interaction.sdk.util.ReferentialConstraintParser;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataOData4j;
import com.temenos.interaction.core.entity.vocabulary.Term;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermMandatory;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;

/**
 * This class is the main entry point to the IRIS SDK. It is a simple front end
 * for generating JPA classes, and associated configuration files. With these
 * classes and config files in place we can then fill a mock database with
 * appropriate values and enable IRIS to respond to resource requests from User
 * Agents.
 * 
 */
public class JPAResponderGen {

	private final static String JPA_CONFIG_FILE = "persistence.xml";
	private final static String SPRING_CONFIG_FILE = "spring-beans.xml";
	private final static String SPRING_RESOURCEMANAGER_FILE = "resourcemanager-context.xml";
	private final static String RESPONDER_INSERT_FILE = "responder_insert.sql";
	private final static String BEHAVIOUR_CLASS_FILE = "Behaviour.java";
	private final static String METADATA_FILE = "metadata.xml";

	/*
	 *  create a new instance of the engine
	 */
	VelocityEngine ve = new VelocityEngine();

	/**
	 * Construct an instance of this class
	 */
	public JPAResponderGen() {
		// load .vm templates using classloader
		ve.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
		ve.init();
	}

	/**
	 * Generate project artefacts from an EDMX file.
	 * @param edmxFile EDMX file
	 * @param srcOutputPath Path to output directory
	 * @param configOutputPath Path to configuration files directory
	 * @return true if successful, false otherwise
	 */
	public boolean generateArtifacts(String edmxFile, File srcOutputPath, File configOutputPath) {
		//Read Edmx contents twice, once for odata4j parser and again for the sax parser to read ref.constraints
		InputStream isEdmx;
		try {
			isEdmx = new FileInputStream(edmxFile);
		} catch (FileNotFoundException e) {
			return false;
		}		

		//Parse emdx file
		XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(isEdmx)));
		EdmDataServices edmDataServices = new EdmxFormatParser().parseMetadata(reader);

		//generate artefacts
		return generateArtifacts(edmxFile, edmDataServices, srcOutputPath, configOutputPath);
	}
	
	/**
	 * Generate project artefacts from OData4j metadata
	 * @param edmxFile EDMX file
	 * @param edmDataServices odata4j metadata 
	 * @param srcOutputPath Path to output directory
	 * @param configOutputPath Path to configuration files directory
	 * @return true if successful, false otherwise
	 */
	public boolean generateArtifacts(String edmxFile, EdmDataServices edmDataServices, File srcOutputPath, File configOutputPath) {
		//Make sure we have at least one entity container
		boolean ok = true;
		if(edmDataServices.getSchemas().size() == 0 || edmDataServices.getSchemas().get(0).getEntityContainers().size() == 0) {
			return false;
		}
		String entityContainerNamespace = edmDataServices.getSchemas().get(0).getEntityContainers().get(0).getName();
		
		//Obtain resource information
		List<ResourceInfo> resourcesInfo = new ArrayList<ResourceInfo>();
		List<EntityInfo> entitiesInfo = new ArrayList<EntityInfo>();
		for (EdmEntityType t : edmDataServices.getEntityTypes()) {
			EntityInfo entityInfo = createEntityInfoFromEdmEntityType(t);
			EntityInfo collectionEntityInfo = createEntityInfoFromEdmEntityType(t);
			addResourcesInfo(resourcesInfo, entityInfo, collectionEntityInfo, new Commands());
			entitiesInfo.add(entityInfo);
		}
		
		//Create interaction model
		InteractionModel interactionModel = new InteractionModel(edmDataServices);
		for (EdmEntityType entityType : edmDataServices.getEntityTypes()) {
			String entityName = entityType.getName();
			IMResourceStateMachine rsm = interactionModel.findResourceStateMachine(entityName);
			//Use navigation properties to define state transitions
			if(entityType.getNavigationProperties() != null) {
				for (EdmNavigationProperty np : entityType.getNavigationProperties()) {
					EdmAssociationEnd targetEnd = np.getToRole();
					boolean isTargetCollection = targetEnd.getMultiplicity().equals(EdmMultiplicity.MANY);
					EdmEntityType targetEntityType = targetEnd.getType();
					String targetEntityName = targetEntityType.getName();
					IMResourceStateMachine targetRsm = interactionModel.findResourceStateMachine(targetEntityName);
					
					EdmAssociation association = np.getRelationship();
					String linkProperty = getLinkProperty(association.getName(), edmxFile);

					//Reciprocal link state name
					String reciprocalLinkState = "";
					for(EdmNavigationProperty npTarget : targetEntityType.getNavigationProperties()) {
						String targetNavPropTargetEntityName = npTarget.getToRole().getType().getName();
						if(targetNavPropTargetEntityName.equals(entityName)) {
							reciprocalLinkState = npTarget.getName();
						}
					}
					rsm.addTransition(targetEntityName, linkProperty, np.getName(), isTargetCollection, reciprocalLinkState, targetRsm);
				}
			}			
		}
		
		//Create the entity model
		EntityModel entityModel = new EntityModel(entityContainerNamespace);
		for (EdmEntityType entityType : edmDataServices.getEntityTypes()) {
			List<String> keys = entityType.getKeys();
			EMEntity emEntity = new EMEntity(entityType.getName());
			for (EdmProperty prop : entityType.getProperties()) {
				EMProperty emProp = createEMProperty(prop);
				if(keys.contains(prop.getName())) {
					emProp.addVocabularyTerm(new EMTerm(TermIdField.TERM_NAME, "true"));
				}
				emEntity.addProperty(emProp);
			}
			entityModel.addEntity(emEntity);
		}
		
		//Write other artefacts
		if(!writeArtefacts(entityContainerNamespace, resourcesInfo, entitiesInfo, entityModel, interactionModel, srcOutputPath, configOutputPath, true)) {
			ok = false;
		}
		
		
		return ok;
	}

	/**
	 * Generate project artefacts from conceptual interaction and metadata models.
	 * @param metadata metadata model
	 * @param interactionModel Conceptual interaction model
	 * @param srcOutputPath Path to output directory
	 * @param configOutputPath Path to configuration files directory
	 * @param generateMockResponder Indicates whether to generate artifacts for a mock responder 
	 * @return true if successful, false otherwise
	 */
	public boolean generateArtifacts(Metadata metadata, InteractionModel interactionModel, File srcOutputPath, File configOutputPath, boolean generateMockResponder) {
		Commands commands = new Commands();
		return generateArtifacts(metadata, interactionModel, commands, srcOutputPath, configOutputPath, generateMockResponder);
	}
	
	/**
	 * Generate project artefacts from conceptual interaction and metadata models.
	 * @param metadata metadata model
	 * @param interactionModel Conceptual interaction model
	 * @param commands Commands
	 * @param srcOutputPath Path to output directory
	 * @param configOutputPath Path to configuration files directory
	 * @param generateMockResponder Indicates whether to generate artifacts for a mock responder 
	 * @return true if successful, false otherwise
	 */
	public boolean generateArtifacts(Metadata metadata, InteractionModel interactionModel, Commands commands, File srcOutputPath, File configOutputPath, boolean generateMockResponder) {
		boolean ok = true;

		String modelName = metadata.getModelName();
		String namespace = modelName + Metadata.MODEL_SUFFIX;
		
		//Obtain resource information
		List<ResourceInfo> resourcesInfo = new ArrayList<ResourceInfo>();
		List<EntityInfo> entitiesInfo = new ArrayList<EntityInfo>();
		for (EntityMetadata entityMetadata: metadata.getEntitiesMetadata().values()) {
			EntityInfo entityInfo = createEntityInfoFromEntityMetadata(namespace, entityMetadata);
			EntityInfo collectionEntityInfo = createEntityInfoFromEntityMetadata(namespace, entityMetadata);
			addResourcesInfo(resourcesInfo, entityInfo, collectionEntityInfo, commands);
			entitiesInfo.add(entityInfo);
		}

		//Create the entity model
		EntityModel entityModel = createEntityModelFromMetadata(namespace, metadata);
		
		//Write other artefacts
		if(!writeArtefacts(modelName, resourcesInfo, entitiesInfo, entityModel, interactionModel, srcOutputPath, configOutputPath, generateMockResponder)) {
			ok = false;
		}
		
		return ok;
	}

	protected String getLinkProperty(String associationName, String edmxFile) {
		return ReferentialConstraintParser.getLinkProperty(associationName, edmxFile);
	}
	
	private boolean writeArtefacts(String modelName, List<ResourceInfo> resourcesInfo, List<EntityInfo> entitiesInfo, EntityModel entityModel, InteractionModel interactionModel, File srcOutputPath, File configOutputPath, boolean generateMockResponder) {
		boolean ok = true;
		String namespace = modelName + Metadata.MODEL_SUFFIX;
		
		//Create the source directory
		new File(srcOutputPath + "/" + namespace.replace(".", "/")).mkdirs();
		
		//Write metadata.xml
		if (!writeMetadata(configOutputPath, generateMetadata(entityModel))) {
			ok = false;
		}
		
		// generate spring configuration files
		if (!writeSpringConfiguration(configOutputPath, SPRING_CONFIG_FILE, generateSpringConfiguration(resourcesInfo, modelName, interactionModel))) {
			ok = false;
		}

		// generate Behaviour class
		String behaviourFilePath = srcOutputPath + "/" + namespace.replace(".", "/") + "/" + BEHAVIOUR_CLASS_FILE;
		if (!writeBehaviourClass(behaviourFilePath, generateBehaviourClass(modelName, interactionModel))) {
			ok = false;
		}

		if(generateMockResponder) {
			//Write JPA classes
			for(EntityInfo entityInfo : entitiesInfo) {
				if(!generateJPAEntity(entityInfo, srcOutputPath)) {
					ok = false;
				}
			}
			
			// generate persistence.xml
			if (!writeJPAConfiguration(configOutputPath, generateJPAConfiguration(resourcesInfo))) {
				ok = false;
			}

			// generate spring configuration for JPA database
			if (!writeSpringConfiguration(configOutputPath, SPRING_RESOURCEMANAGER_FILE, generateSpringResourceManagerContext(modelName))) {
				ok = false;
			}

			// generate responder insert
			if (!writeResponderDML(configOutputPath, generateResponderDML(resourcesInfo))) {
				ok = false;
			}
		}
		
		return ok;
	}
	
	private boolean generateJPAEntity(EntityInfo entityInfo, File srcOutputPath) {
		//Generate JPA class
		if(entityInfo.isJpaEntity()) {
			if (!writeClass(formClassFilename(srcOutputPath.getPath(), entityInfo), generateJPAEntityClass(entityInfo))) {
				return false;
			}
		}
		return true;
	}

	private void addResourcesInfo(List<ResourceInfo> resourcesInfo, EntityInfo entityInfo, EntityInfo collectionEntityInfo, Commands commands) {
		//Entity resource
		String resourcePath = "GET+/" + entityInfo.getClazz() + "({id})";
		String commandType = commands.getGetEntityCommand();
		boolean isDefaultCommand = commands.isDefaultGetEntityCommand();
		resourcesInfo.add(new ResourceInfo(resourcePath, entityInfo, commandType, isDefaultCommand));

		//Collection resource 
		collectionEntityInfo.setFeedEntity();		//This is a feed of OEntities and should not exist as a JPA entity 
		resourcePath = "GET+/" + collectionEntityInfo.getClazz();
		commandType = commands.getGetEntitiesCommand(); 
		isDefaultCommand = commands.isDefaultGetEntitiesCommand();
		resourcesInfo.add(new ResourceInfo(resourcePath, collectionEntityInfo, commandType, isDefaultCommand));

		resourcePath = "POST+/" + collectionEntityInfo.getClazz();
		commandType = commands.getCreateEntityCommand(); 
		isDefaultCommand = commands.isDefaultCreateEntityCommand();
		resourcesInfo.add(new ResourceInfo(resourcePath, collectionEntityInfo, commandType, isDefaultCommand));
	}
	
	/**
	 * Utility method to form class filename.
	 * @param srcTargetDir
	 * @param entityInfo
	 * @return
	 */
	public static String formClassFilename(String srcTargetDir, EntityInfo entityInfo) {
		return srcTargetDir + "/" + entityInfo.getPackageAsPath() + "/" + entityInfo.getClazz() + ".java";
	}
	
	protected boolean writeClass(String classFileName, String generatedClass) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(classFileName);
			fos.write(generatedClass.getBytes("UTF-8"));
		} catch (IOException e) {
			// TODO add slf4j logger here
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// don't hide original exception
			}
		}
		return true;
	}
	
	public EntityInfo createEntityInfoFromEdmEntityType(EdmType type) {
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
			keyInfo = new FieldInfo(keyName, javaType(key), null);
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

			FieldInfo field = new FieldInfo(property.getName(), javaType(property.getType()), annotations);
			properties.add(field);
		}
		
		//Check if user has specified the name of the JPA entities
		String jpaNamespace = System.getProperty("jpaNamespace");
		boolean isJpaEntity = (jpaNamespace == null || jpaNamespace.equals(entityType.getNamespace()));
		return new EntityInfo(entityType.getName(), entityType.getNamespace(), keyInfo, properties, isJpaEntity);
	}
	
	public EntityInfo createEntityInfoFromEntityMetadata(String namespace, EntityMetadata entityMetadata) {
		FieldInfo keyInfo = null;
		List<FieldInfo> properties = new ArrayList<FieldInfo>();
		for(String propertyName : entityMetadata.getPropertyVocabularyKeySet()) {
			String type = entityMetadata.getTermValue(propertyName, TermValueType.TERM_NAME);
			if(entityMetadata.getTermValue(propertyName, TermIdField.TERM_NAME).equals("true")) {
				keyInfo = new FieldInfo(propertyName, javaType(MetadataOData4j.termValueToEdmType(type)), null);
			}
			else {
				List<String> annotations = new ArrayList<String>();
				if(entityMetadata.getTermValue(propertyName, TermValueType.TERM_NAME).equals(TermValueType.TIMESTAMP)) {
					annotations.add("@Temporal(TemporalType.TIMESTAMP)");
				}
				FieldInfo field = new FieldInfo(propertyName, javaType(MetadataOData4j.termValueToEdmType(type)), annotations);
				properties.add(field);
			}
		}
		
		//Check if user has specified the name of the JPA entities
		String jpaNamespace = System.getProperty("jpaNamespace");
		boolean isJpaEntity = (jpaNamespace == null || jpaNamespace.equals(namespace));
		return new EntityInfo(entityMetadata.getEntityName(), namespace, keyInfo, properties, isJpaEntity);
	}

	/*
	 * Create a EntityModel object from a Metadata container
	 */
	private EntityModel createEntityModelFromMetadata(String namespace, Metadata metadata) {
		//Create the entity model
		EntityModel entityModel = new EntityModel(namespace);
		for (EntityMetadata entityMetadata: metadata.getEntitiesMetadata().values()) {
			EMEntity emEntity = new EMEntity(entityMetadata.getEntityName());
			for(String propertyName : entityMetadata.getPropertyVocabularyKeySet()) {
				EMProperty emProperty = new EMProperty(propertyName);
				Vocabulary propertyVoc = entityMetadata.getPropertyVocabulary(propertyName);
				if(propertyVoc != null) {
					for(Term term : propertyVoc.getTerms()) {
						emProperty.addVocabularyTerm(new EMTerm(term.getName(), term.getValue()));
					}
				}
				emEntity.addProperty(emProperty);
			}
			entityModel.addEntity(emEntity);
		}
		return entityModel;
	}
	
	private String javaType(EdmType type) {
		// TODO support complex type keys?
		assert(type.isSimple());
		String javaType = null;
		if (EdmSimpleType.INT64 == type) {
			javaType = "Long";
		} else if (EdmSimpleType.INT32 == type) {
			javaType = "Integer";
		} else if (EdmSimpleType.INT16 == type) {
			javaType = "Integer";
		} else if (EdmSimpleType.STRING == type) {
			javaType = "String";
		} else if (EdmSimpleType.DATETIME == type) {
			javaType = "java.util.Date";
		} else if (EdmSimpleType.TIME == type) {
			javaType = "java.util.Date";
		} else if (EdmSimpleType.DECIMAL == type) {
			javaType = "java.math.BigDecimal";
		} else if (EdmSimpleType.SINGLE == type) {
			javaType = "Float";
		} else if (EdmSimpleType.DOUBLE == type) {
			javaType = "Double";
		} else if (EdmSimpleType.BOOLEAN == type) {
			javaType = "Boolean";
		} else if (EdmSimpleType.GUID == type) {
			javaType = "String";
		} else if (EdmSimpleType.BINARY == type) {
			javaType = "String";
		} else {
			// TODO support types other than Long and String
			throw new RuntimeException("Entity property type " + type.getFullyQualifiedTypeName() + " not supported");
		}
		return javaType;
	}
	
	/*
	 * Create a property with vocabulary term from the Edmx property 
	 */
	private EMProperty createEMProperty(EdmProperty property) {
		EMProperty emProperty = new EMProperty(property.getName());
		if(property.isNullable()) {
			emProperty.addVocabularyTerm(new EMTerm(TermMandatory.TERM_NAME, "true"));
		}
		
		//Set the value type vocabulary term
		EdmType type = property.getType();
		if (type.equals(EdmSimpleType.DATETIME) || 
			type.equals(EdmSimpleType.TIME)) {
			emProperty.addVocabularyTerm(new EMTerm(TermValueType.TERM_NAME, TermValueType.TIMESTAMP));
		}
		else if (type.equals(EdmSimpleType.INT64) || 
				 type.equals(EdmSimpleType.INT32) ||
				 type.equals(EdmSimpleType.INT16)) {
			emProperty.addVocabularyTerm(new EMTerm(TermValueType.TERM_NAME, TermValueType.INTEGER_NUMBER));
		}
		else if (type.equals(EdmSimpleType.SINGLE) || 
				 type.equals(EdmSimpleType.DOUBLE) ||
				 type.equals(EdmSimpleType.DECIMAL)) {
			emProperty.addVocabularyTerm(new EMTerm(TermValueType.TERM_NAME, TermValueType.NUMBER));
		}
		else if (type.equals(EdmSimpleType.BOOLEAN)) {
			emProperty.addVocabularyTerm(new EMTerm(TermValueType.TERM_NAME, TermValueType.BOOLEAN));
		}
		return emProperty;
	}	

	protected boolean writeJPAConfiguration(File sourceDir, String generatedPersistenceXML) {
		FileOutputStream fos = null;
		try {
			File metaInfDir = new File(sourceDir.getPath() + "/META-INF");
			metaInfDir.mkdirs();
			fos = new FileOutputStream(new File(metaInfDir, JPA_CONFIG_FILE));
			fos.write(generatedPersistenceXML.getBytes("UTF-8"));
		} catch (IOException e) {
			// TODO add slf4j logger here
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// don't hide original exception
			}
		}
		return true;
	}

	protected boolean writeSpringConfiguration(File sourceDir, String filename, String generatedSpringXML) {
		FileOutputStream fos = null;
		try {
			File metaInfDir = new File(sourceDir.getPath() + "/META-INF");
			metaInfDir.mkdirs();
			fos = new FileOutputStream(new File(metaInfDir, filename));
			fos.write(generatedSpringXML.getBytes("UTF-8"));
		} catch (IOException e) {
			// TODO add slf4j logger here
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// don't hide original exception
			}
		}
		return true;
	}

	protected boolean writeResponderDML(File sourceDir, String generateResponderDML) {
		FileOutputStream fos = null;
		try {
			File metaInfDir = new File(sourceDir.getPath() + "/META-INF");
			metaInfDir.mkdirs();
			fos = new FileOutputStream(new File(metaInfDir, RESPONDER_INSERT_FILE));
			fos.write(generateResponderDML.getBytes("UTF-8"));
		} catch (IOException e) {
			// TODO add slf4j logger here
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// don't hide original exception
			}
		}
		return true;
	}
	
	protected boolean writeBehaviourClass(String path, String generatedBehaviourClass) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(path));
			fos.write(generatedBehaviourClass.getBytes("UTF-8"));
		} catch (IOException e) {
			// TODO add slf4j logger here
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// don't hide original exception
			}
		}
		return true;
	}

	protected boolean writeMetadata(File sourceDir, String generatedMetadata) {
		FileOutputStream fos = null;
		try {
			File metaInfDir = new File(sourceDir.getPath());
			metaInfDir.mkdirs();
			fos = new FileOutputStream(new File(metaInfDir, METADATA_FILE));
			fos.write(generatedMetadata.getBytes("UTF-8"));
		} catch (IOException e) {
			// TODO add slf4j logger here
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// don't hide original exception
			}
		}
		return true;
	}
	
	/**
	 * Generate a JPA Entity from the provided info
	 * @precondition {@link EntityInfo} non null
	 * @precondition {@link EntityInfo} contain a valid package, class name, key, and fields
	 * @postcondition A valid java class, that may be serialised and compiled, or pushed into
	 * the {@link ClassLoader.defineClass}
	 * @param jpaEntityClass
	 * @return
	 */
	public String generateJPAEntityClass(EntityInfo jpaEntityClass) {
		assert(jpaEntityClass != null);
		
		VelocityContext context = new VelocityContext();
		context.put("jpaentity", jpaEntityClass);
		
		Template t = ve.getTemplate("/JPAEntity.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}

	/**
	 * Generate the JPA configuration for the provided JPA entities.
	 * @param resourcesInfo
	 * @return
	 */
	public String generateJPAConfiguration(List<ResourceInfo> resourcesInfo) {
		VelocityContext context = new VelocityContext();
		context.put("resourcesInfo", resourcesInfo);
		
		Template t = ve.getTemplate("/persistence.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}

	/**
	 * Generate the Spring configuration for the provided resources.
	 * @param resourcesInfo
	 * @return
	 */
	public String generateSpringConfiguration(List<ResourceInfo> resourcesInfo, String entityContainerNamespace, InteractionModel interactionModel) {
		VelocityContext context = new VelocityContext();
		context.put("resourcesInfo", resourcesInfo);
		context.put("entityContainerNamespace", entityContainerNamespace);
		context.put("behaviourClass", entityContainerNamespace + ".Behaviour");
		context.put("interactionModel", interactionModel);
		
		Template t = ve.getTemplate("/spring-beans.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}

	/**
	 * Generate the Spring configuration for the provided resources.
	 * @param entityContainerNamespace
	 * @return
	 */
	public String generateSpringResourceManagerContext(String entityContainerNamespace) {
		VelocityContext context = new VelocityContext();
		context.put("entityContainerNamespace", entityContainerNamespace);
		
		Template t = ve.getTemplate("/resourcemanager-context.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}
	
	/**
	 * Generate the responder_insert.sql provided resources.
	 * @param resourcesInfo
	 * @return
	 */
	public String generateResponderDML(List<ResourceInfo> resourcesInfo) {
		VelocityContext context = new VelocityContext();
		context.put("resourcesInfo", resourcesInfo);
		
		Template t = ve.getTemplate("/responder_insert.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}

	/**
	 * Generate the behaviour class.
	 * @param resourcesInfo
	 * @return
	 */
	public String generateBehaviourClass(String entityContainerNamespace, InteractionModel interactionModel) {
		VelocityContext context = new VelocityContext();
		context.put("behaviourNamespace", entityContainerNamespace);
		context.put("interactionModel", interactionModel);
		
		Template t = ve.getTemplate("/behaviour.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}

	/**
	 * Generate the metadata file
	 * @param entityModel The entity model
	 * @return The generated metadata
	 */
	public String generateMetadata(EntityModel entityModel) {
		VelocityContext context = new VelocityContext();
		context.put("entityModel", entityModel);
		
		Template t = ve.getTemplate("/metadata.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}
	
	public static void main(String[] args) {
		boolean ok = true;
		if (args != null && args.length == 2) {
			String edmxFilePath = args[0]; 
			String targetDirectoryStr = args[1]; 
			File edmxFile = new File(edmxFilePath);
			File targetDirectory = new File(targetDirectoryStr);
			
			// check our configuration
			if (!edmxFile.exists()) {
				System.out.println("EDMX file not found");
				ok = false;
			}
			if (!targetDirectory.exists() || !targetDirectory.isDirectory()) {
				System.out.println("Target directory is invalid");
				ok = false;
			}
			
			if (ok) {
				JPAResponderGen rg = new JPAResponderGen();
				System.out.println("Writing source and configuration to [" + targetDirectory + "]");
				ok = rg.generateArtifacts(edmxFilePath, targetDirectory, targetDirectory);
			}
		} else {
			ok = false;
		}
		if (!ok) {
			System.out.print(usage());
		}

	}

	public static String usage() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n")
		.append("Generates an IRIS JPA responder from an EDMX file.\n")
		.append("\n")
		.append("java ").append(JPAResponderGen.class.getName()).append(" [EDMX file] [target directory]\n");
		return sb.toString();
	}
	
}
