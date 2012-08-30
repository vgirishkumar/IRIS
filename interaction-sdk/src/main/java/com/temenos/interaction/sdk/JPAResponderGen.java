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

import com.temenos.interaction.sdk.entity.EMEntity;
import com.temenos.interaction.sdk.entity.EMProperty;
import com.temenos.interaction.sdk.entity.EMTerm;
import com.temenos.interaction.sdk.entity.EntityModel;
import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.InteractionModel;
import com.temenos.interaction.sdk.util.ReferentialConstraintParser;
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
	private final static String RESPONDER_INSERT_FILE = "responder_insert.sql";
	private final static String BEHAVIOUR_CLASS_FILE = "Behaviour.java";
	private final static String METADATA_FILE = "metadata.xml";

	/*
	 *  create a new instance of the engine
	 */
	VelocityEngine ve = new VelocityEngine();

	public JPAResponderGen() {
		// load .vm templates using classloader
		ve.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
		ve.init();
	}

	/**
	 * Generate JPA responder artifacts.  Including JPA classes, persistence.xml, and DML bootstrapping.
	 * @param is
	 * @param srcOutputPath
	 * @param configOutputPath
	 * @return
	 */
	public boolean generateArtifacts(String edmxFile, File srcOutputPath, File configOutputPath) {
		InputStream isEdmx;
		try {
			isEdmx = new FileInputStream(edmxFile);
		} catch (FileNotFoundException e) {
			return false;
		}		
		XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(isEdmx)));
		EdmDataServices ds = new EdmxFormatParser().parseMetadata(reader);
		
		boolean ok = true;
		List<ResourceInfo> resourcesInfo = new ArrayList<ResourceInfo>();

		//Make sure we have at least one entity container
		if(ds.getSchemas().size() == 0 || ds.getSchemas().get(0).getEntityContainers().size() == 0) {
			return false;
		}
		String entityContainerNamespace = ds.getSchemas().get(0).getEntityContainers().get(0).getName();
		
		// generate JPA classes
		for (EdmEntityType t : ds.getEntityTypes()) {
			EntityInfo entityInfo = createEntityInfoFromEdmEntityType(t);
			
			//Generate JPA class
			if(entityInfo.isJpaEntity()) {
				String fqOutputDir = srcOutputPath.getPath() + "/" + entityInfo.getPackageAsPath();
				new File(fqOutputDir).mkdirs();
				if (!writeClass(formClassFilename(srcOutputPath.getPath(), entityInfo), generateJPAEntityClass(entityInfo))) {
					return false;
				}
			}
			
			//Entity resource
			String resourcePath = "GET+/" + entityInfo.getClazz() + "({id})";
			String commandType = "com.temenos.interaction.commands.odata.GETEntityCommand"; 
			resourcesInfo.add(new ResourceInfo(resourcePath, entityInfo, commandType));

			//Collection resource 
			EntityInfo entityFeedInfo = createEntityInfoFromEdmEntityType(t);
			entityFeedInfo.setFeedEntity();		//This is a feed of OEntities and should not exist as a JPA entity 
			resourcePath = "GET+/" + entityFeedInfo.getClazz();
			commandType = "com.temenos.interaction.commands.odata.GETEntitiesCommand"; 
			resourcesInfo.add(new ResourceInfo(resourcePath, entityFeedInfo, commandType));

			resourcePath = "POST+/" + entityFeedInfo.getClazz();
			commandType = "com.temenos.interaction.commands.odata.CreateEntityCommand"; 
			resourcesInfo.add(new ResourceInfo(resourcePath, entityFeedInfo, commandType));
		}
		
		//Create interaction model
		InteractionModel interactionModel = new InteractionModel();
		for (EdmEntityType entityType : ds.getEntityTypes()) {
			//ResourceStateMachine with one collection and one resource entity state
			String entityName = entityType.getName();
			String collectionStateName = entityName.toLowerCase() + "s";
			String entityStateName = entityName.toLowerCase();
			String mappedEntityProperty = entityType.getKeys().size() > 0 ? entityType.getKeys().get(0) : "id";
			IMResourceStateMachine rsm = new IMResourceStateMachine(entityName, collectionStateName, entityStateName, mappedEntityProperty);
			interactionModel.addResourceStateMachine(rsm);
		}
			
		//Add navigation properties to interaction model
		for (EdmEntityType entityType : ds.getEntityTypes()) {
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
					String linkProperty = ReferentialConstraintParser.getLinkProperty(association.getName(), edmxFile);

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
		for (EdmEntityType entityType : ds.getEntityTypes()) {
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
		
		// generate persistence.xml
		if (!writeJPAConfiguration(configOutputPath, generateJPAConfiguration(resourcesInfo))) {
			ok = false;
		}

		// generate spring-beans.xml
		if (!writeSpringConfiguration(configOutputPath, generateSpringConfiguration(resourcesInfo, entityContainerNamespace, interactionModel))) {
			ok = false;
		}

		// generate responder insert
		if (!writeResponderDML(configOutputPath, generateResponderDML(resourcesInfo))) {
			ok = false;
		}
		
		// generate Behaviour class
		String behaviourFilePath = srcOutputPath + "/" + entityContainerNamespace.replace(".", "/") + "Model/" + BEHAVIOUR_CLASS_FILE;
		if (!writeBehaviourClass(behaviourFilePath, generateBehaviourClass(entityContainerNamespace, interactionModel))) {
			ok = false;
		}

		// generate metadata.xml
		if (!writeMetadata(configOutputPath, generateMetadata(entityModel))) {
			ok = false;
		}
		
		return ok;
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
	
	private boolean writeClass(String classFileName, String generatedClass) {
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

	private boolean writeJPAConfiguration(File sourceDir, String generatedPersistenceXML) {
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

	private boolean writeSpringConfiguration(File sourceDir, String generatedSpringXML) {
		FileOutputStream fos = null;
		try {
			File metaInfDir = new File(sourceDir.getPath() + "/META-INF");
			metaInfDir.mkdirs();
			fos = new FileOutputStream(new File(metaInfDir, SPRING_CONFIG_FILE));
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

	private boolean writeResponderDML(File sourceDir, String generatedSpringXML) {
		FileOutputStream fos = null;
		try {
			File metaInfDir = new File(sourceDir.getPath() + "/META-INF");
			metaInfDir.mkdirs();
			fos = new FileOutputStream(new File(metaInfDir, RESPONDER_INSERT_FILE));
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
	
	private boolean writeBehaviourClass(String path, String generatedBehaviourClass) {
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

	private boolean writeMetadata(File sourceDir, String generatedMetadata) {
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
