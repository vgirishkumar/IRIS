package com.temenos.interaction.sdk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataOData4j;
import com.temenos.interaction.core.entity.vocabulary.Term;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermMandatory;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;
import com.temenos.interaction.sdk.adapter.InteractionAdapter;
import com.temenos.interaction.sdk.adapter.edmx.EDMXAdapter;
import com.temenos.interaction.sdk.command.Commands;
import com.temenos.interaction.sdk.command.Parameter;
import com.temenos.interaction.sdk.entity.EMEntity;
import com.temenos.interaction.sdk.entity.EMProperty;
import com.temenos.interaction.sdk.entity.EMTerm;
import com.temenos.interaction.sdk.entity.EntityModel;
import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.transition.IMCollectionStateTransition;
import com.temenos.interaction.sdk.interaction.transition.IMEntityStateTransition;
import com.temenos.interaction.sdk.interaction.transition.IMTransition;
import com.temenos.interaction.sdk.interaction.InteractionModel;
import com.temenos.interaction.sdk.rimdsl.RimDslGenerator;

/**
 * This class is the main entry point to the IRIS SDK. It is a simple front end
 * for generating JPA classes, and associated configuration files. With these
 * classes and config files in place we can then fill a mock database with
 * appropriate values and enable IRIS to respond to resource requests from User
 * Agents.
 * 
 */
public class JPAResponderGen {

	public final static String JPA_CONFIG_FILE = "jpa-persistence.xml";
	public final static String SPRING_CONFIG_FILE = "spring-beans.xml";
	public final static String SPRING_RESOURCEMANAGER_FILE = "resourcemanager-context.xml";
	public final static String RESPONDER_INSERT_FILE = "responder_insert.sql";
	public final static String RESPONDER_SETTINGS_FILE = "responder.properties";
	public final static String BEHAVIOUR_CLASS_FILE = "Behaviour.java";
	public final static String METADATA_FILE = "metadata.xml";

	public final static Parameter COMMAND_SERVICE_DOCUMENT = new Parameter("ServiceDocument", false, "");
	public final static Parameter COMMAND_EDM_DATA_SERVICES = new Parameter("edmDataServices", true, "");
	public final static Parameter COMMAND_METADATA = new Parameter("Metadata", false, "");
	public final static Parameter COMMAND_METADATA_SOURCE_ODATAPRODUCER = new Parameter("producer", true, "odataProducer");
	public final static Parameter COMMAND_METADATA_SOURCE_MODEL = new Parameter("edmMetadata", true, "edmMetadata");
			
	private final boolean strictOData; 		//Indicates whether it should generate strict odata paths etc. (e.g. Flight(1)/flightschedule rather than FlightSchedule(2051))
	
	/*
	 *  create a new instance of the engine
	 */
	VelocityEngine ve = new VelocityEngine();

	/**
	 * Construct an instance of this class
	 */
	public JPAResponderGen() {
		this(true);
	}

	/**
	 * Construct an instance of this class
	 * @param strictOData indicates whether to generate a strict odata model
	 */
	public JPAResponderGen(boolean strictOData) {
		this.strictOData = strictOData;
		
		// load .vm templates using classloader
		ve.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
		ve.init();
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
	public boolean generateArtifacts(InteractionAdapter adapter, File srcOutputPath, File configOutputPath, boolean generateMockResponder) {
		return generateArtifacts(adapter.getEntityModel(), adapter.getInteractionModel(), adapter.getCommands(), adapter.getEntitiesInfo(), srcOutputPath, configOutputPath, generateMockResponder);
	}
	public boolean generateArtifacts(InteractionAdapter adapter, File srcOutputPath, File configOutputPath) {
		return generateArtifacts(adapter, srcOutputPath, configOutputPath, true);
	}

	public boolean generateArtifacts(Metadata metadata, InteractionModel interactionModel, File srcOutputPath, File configOutputPath, boolean generateMockResponder) {
		//Create commands
		Commands commands = getDefaultCommands();
		
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
		//Create the entity model
		String modelName = metadata.getModelName();
		EntityModel entityModel = createEntityModelFromMetadata(modelName, metadata);
		
		//Obtain resource information
		String namespace = modelName + Metadata.MODEL_SUFFIX;
		List<EntityInfo> entitiesInfo = new ArrayList<EntityInfo>();
		for (EntityMetadata entityMetadata: metadata.getEntitiesMetadata().values()) {
			EntityInfo entityInfo = createEntityInfoFromEntityMetadata(namespace, entityMetadata);
			addNavPropertiesToEntityInfo(entityInfo, interactionModel);
			entitiesInfo.add(entityInfo);
		}

		return generateArtifacts(entityModel, interactionModel, commands, entitiesInfo, srcOutputPath, configOutputPath, generateMockResponder);
	}

	/**
	 * Generate project artefacts from conceptual interaction and metadata models.
	 * @param entityModel the entity metadata 
	 * @param interactionModel Conceptual interaction model
	 * @param commands Commands
	 * @param srcOutputPath Path to output directory
	 * @param configOutputPath Path to configuration files directory
	 * @param generateMockResponder Indicates whether to generate artifacts for a mock responder 
	 * @return true if successful, false otherwise
	 */
	public boolean generateArtifacts(EntityModel entityModel, 
			InteractionModel interactionModel, 
			Commands commands, 
			List<EntityInfo> entitiesInfo,
			File srcOutputPath, 
			File configOutputPath, 
			boolean generateMockResponder) {
		boolean ok = true;

		//Write other artefacts
		if(!writeArtefacts(entityModel.getModelName(), entitiesInfo, commands, entityModel, interactionModel, srcOutputPath, configOutputPath, generateMockResponder)) {
			ok = false;
		}
		return ok;
	}
	
	/**
	 * Returns a character stream representing the RIM from the conceptual interaction and metadata models.
	 * @param interactionModel Conceptual interaction model
	 * @param commands Commands
	 * @return RIM as character stream 
	 * @throws Exception
	 */
	public InputStream getRIM(InteractionModel interactionModel, Commands commands) throws Exception {
		RimDslGenerator rimDslGenerator = new RimDslGenerator(ve);
		String dsl = rimDslGenerator.generateRimDsl(interactionModel, commands, strictOData);
		return new ByteArrayInputStream(dsl.getBytes());
	}
	
	private boolean writeArtefacts(String modelName, List<EntityInfo> entitiesInfo, Commands commands, EntityModel entityModel, InteractionModel interactionModel, File srcOutputPath, File configOutputPath, boolean generateMockResponder) {
		boolean ok = true;
		String namespace = modelName + Metadata.MODEL_SUFFIX;
		
		//Create the source directory
		new File(srcOutputPath + "/" + namespace.replace(".", "/")).mkdirs();
		
		// generate metadata.xml
		if (!writeMetadata(configOutputPath, generateMetadata(entityModel))) {
			ok = false;
		}
		
		// generate spring configuration files
		if (!writeSpringConfiguration(configOutputPath, SPRING_CONFIG_FILE, generateSpringConfiguration(namespace, modelName, commands))) {
			ok = false;
		}

		// generate the rim DSL
		RimDslGenerator rimDslGenerator = new RimDslGenerator(ve);
		String rimDslFilename = modelName + ".rim";
		if (!writeRimDsl(configOutputPath, rimDslFilename, rimDslGenerator.generateRimDsl(interactionModel, commands, strictOData))) {
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
			if (!writeJPAConfiguration(configOutputPath, generateJPAConfiguration(entitiesInfo))) {
				ok = false;
			}

			// generate spring configuration for JPA database
			if (!writeSpringConfiguration(configOutputPath, SPRING_RESOURCEMANAGER_FILE, generateSpringResourceManagerContext(modelName))) {
				ok = false;
			}

			// generate responder insert
			if (!writeResponderDML(configOutputPath, generateResponderDML(entitiesInfo))) {
				ok = false;
			}

			// generate responder settings
			if (!writeResponderSettings(configOutputPath, generateResponderSettings(modelName))) {
				ok = false;
			}
		}
		
		return ok;
	}
	
	private boolean generateJPAEntity(EntityInfo entityInfo, File srcOutputPath) {
		//Generate JPA class
		if(entityInfo.isJpaEntity()) {
			String path = srcOutputPath.getPath() + "/" + entityInfo.getPackageAsPath();
			if (!writeClass(path, formClassFilename(path, entityInfo), generateJPAEntityClass(entityInfo))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Utility method to form class filename.
	 * @param srcTargetDir
	 * @param entityInfo
	 * @return
	 */
	public static String formClassFilename(String path, EntityInfo entityInfo) {
		return path + "/" + entityInfo.getClazz() + ".java";
	}
	
	protected boolean writeClass(String path, String classFileName, String generatedClass) {
		FileOutputStream fos = null;
		try {
			new File(path).mkdirs();
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
				if(type.equals(TermValueType.TIMESTAMP)) {
					annotations.add("@Temporal(TemporalType.TIMESTAMP)");
				}
				else if(type.equals(TermValueType.DATE)) {
					annotations.add("@Temporal(TemporalType.DATE)");
				}
				else if(type.equals(TermValueType.TIME)) {
					annotations.add("@Temporal(TemporalType.TIME)");
				}
				FieldInfo field = new FieldInfo(propertyName, javaType(MetadataOData4j.termValueToEdmType(type)), annotations);
				properties.add(field);
			}
		}
		
		//Check if user has specified the name of the JPA entities
		String jpaNamespace = System.getProperty("jpaNamespace");
		boolean isJpaEntity = (jpaNamespace == null || jpaNamespace.equals(namespace));
		return new EntityInfo(entityMetadata.getEntityName(), namespace, keyInfo, properties, new ArrayList<JoinInfo>(), isJpaEntity);
	}

	/*
	 * Create a EntityModel object from a Metadata container
	 */
	private EntityModel createEntityModelFromMetadata(String modelName, Metadata metadata) {
		//Create the entity model
		EntityModel entityModel = new EntityModel(modelName);
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
	
	public static String javaType(EdmType type) {
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
	public static EMProperty createEMProperty(EdmProperty property) {
		EMProperty emProperty = new EMProperty(property.getName());
		if(property.isNullable()) {
			emProperty.addVocabularyTerm(new EMTerm(TermMandatory.TERM_NAME, "true"));
		}
		
		//Set the value type vocabulary term
		EdmType type = property.getType();
		if (type.equals(EdmSimpleType.DATETIME)) {
			emProperty.addVocabularyTerm(new EMTerm(TermValueType.TERM_NAME, TermValueType.TIMESTAMP));
		}
		else if (type.equals(EdmSimpleType.TIME)) {
				emProperty.addVocabularyTerm(new EMTerm(TermValueType.TERM_NAME, TermValueType.TIME));
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
			File f = new File(metaInfDir, filename);
			if(!f.exists()) {
				fos = new FileOutputStream(f);
				fos.write(generatedSpringXML.getBytes("UTF-8"));
			}
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
	
	protected boolean writeMetadata(File sourceDir, String generatedMetadata) {
		FileOutputStream fos = null;
		try {
			File metaInfDir = new File(sourceDir.getPath());
			metaInfDir.mkdirs();
			File f = new File(metaInfDir, METADATA_FILE);
			if(!f.exists()) {
				fos = new FileOutputStream(f);
				fos.write(generatedMetadata.getBytes("UTF-8"));
			}
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

	protected boolean writeRimDsl(File sourceDir, String rimDslFilename, String generatedRimDsl) {
		FileOutputStream fos = null;
		try {
			File dir = new File(sourceDir.getPath());
			dir.mkdirs();
			File f = new File(dir, rimDslFilename);
			if(!f.exists()) {
				fos = new FileOutputStream(f);
				fos.write(generatedRimDsl.getBytes("UTF-8"));
			}
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
	
	protected boolean writeResponderSettings(File sourceDir, String generateResponderSettings) {
		FileOutputStream fos = null;
		try {
			File metaInfDir = new File(sourceDir.getPath());
			metaInfDir.mkdirs();
			File f = new File(metaInfDir, RESPONDER_SETTINGS_FILE);
			if(!f.exists()) {
				fos = new FileOutputStream(f);
				fos.write(generateResponderSettings.getBytes("UTF-8"));
			}
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
	 * @param entitiesInfo
	 * @return
	 */
	public String generateJPAConfiguration(List<EntityInfo> entitiesInfo) {
		VelocityContext context = new VelocityContext();
		context.put("entitiesInfo", entitiesInfo);
		
		Template t = ve.getTemplate("/jpa-persistence.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}

	/**
	 * Generate the Spring configuration for the provided resources.
	 */
	public String generateSpringConfiguration(String namespace, String modelName, Commands commands) {
		VelocityContext context = new VelocityContext();
		context.put("behaviourClass", namespace + "." + modelName + "Behaviour");
		context.put("commands", commands);
		
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
	 * @return
	 */
	public String generateResponderDML(List<EntityInfo> entitiesInfo) {
		VelocityContext context = new VelocityContext();
		context.put("entitiesInfo", entitiesInfo);
		
		Template t = ve.getTemplate("/responder_insert.vm");
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
	
	/**
	 * Generate responder settings 
	 * @return
	 */
	public String generateResponderSettings(String responderName) {
		VelocityContext context = new VelocityContext();
		context.put("responderName", responderName);
		
		Template t = ve.getTemplate("/responder_settings.vm");
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
				ok = rg.generateArtifacts(new EDMXAdapter(edmxFilePath), targetDirectory, targetDirectory, true);
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

	/**
	 * Returns a list of default commands
	 * @return
	 */
	public static Commands getDefaultCommands() {
		Commands commands = new Commands();

		//Add RIM events
		commands.addRimEvent("GET", Commands.HTTP_COMMAND_GET);
		commands.addRimEvent("POST", Commands.HTTP_COMMAND_POST);
		commands.addRimEvent("PUT", Commands.HTTP_COMMAND_PUT);
		commands.addRimEvent("DELETE", Commands.HTTP_COMMAND_DELETE);
		
		//Add commands
		commands.addCommand(Commands.GET_SERVICE_DOCUMENT, "com.temenos.interaction.commands.odata.GETMetadataCommand", COMMAND_SERVICE_DOCUMENT, COMMAND_EDM_DATA_SERVICES);
		commands.addCommand(Commands.GET_METADATA, "com.temenos.interaction.commands.odata.GETMetadataCommand", COMMAND_METADATA, COMMAND_EDM_DATA_SERVICES);
		commands.addCommand(Commands.GET_ENTITY, "com.temenos.interaction.commands.odata.GETEntityCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.GET_ENTITIES, "com.temenos.interaction.commands.odata.GETEntitiesCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.CREATE_ENTITY, "com.temenos.interaction.commands.odata.CreateEntityCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.GET_NAV_PROPERTY, "com.temenos.interaction.commands.odata.GETNavPropertyCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.UPDATE_ENTITY, "com.temenos.interaction.commands.odata.UpdateEntityCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		commands.addCommand(Commands.DELETE_ENTITY, "com.temenos.interaction.commands.odata.DeleteEntityCommand", COMMAND_METADATA_SOURCE_ODATAPRODUCER);
		
		return commands;
	}
	
	/*
	 * Add transition properties to entity info
	 */
	public static void addNavPropertiesToEntityInfo(EntityInfo entityInfo, InteractionModel interactionModel) {
		IMResourceStateMachine rsm = interactionModel.findResourceStateMachine(entityInfo.getClazz());
		for(IMTransition transition : rsm.getEntityStateTransitions()) {
			List<FieldInfo> properties = entityInfo.getAllFieldInfos();
			List<String> annotations = new ArrayList<String>();
			if (transition instanceof IMCollectionStateTransition) {
				//Transition to collection state
				//TODO fix reciprocal links
				//annotations.add("@OneToMany(cascade = CascadeType.ALL, mappedBy = \"" + transition.getTargetStateName() + "\")");
				//properties.add(new FieldInfo(transition.getTargetStateName(), "Collection<" + transition.getTargetEntityName() + ">", annotations));
			} else if(transition instanceof IMEntityStateTransition){
				//Transition to entity state
				IMEntityStateTransition t = (IMEntityStateTransition) transition;
				annotations.add("@JoinColumn(name = \"" + t.getLinkProperty() + "\", referencedColumnName = \"" + t.getTargetResourceStateMachine().getMappedEntityProperty() + "\", insertable = false, updatable = false)");
				annotations.add("@ManyToOne(optional = false)");
				properties.add(new FieldInfo(t.getTargetState().getName(), t.getTargetResourceStateMachine().getEntityName(), annotations));
			}
		}
	}
	
}
