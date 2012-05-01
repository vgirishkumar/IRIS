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
	 * @precondition File edmxFile exists on the file system
	 * @postcondition JPA Entity classes written to file system as valid Java source
	 * @postcondition JPA persistence.xml written to file system, configured to inmemory database
	 * @postcondition a boolean flag indicating a successful result will be returned
	 * @invariant enough free space on the file system
	 * @param edmxFile
	 * @param srcOutputPath
	 * @param configOutputPath
	 */
	public boolean generateArtifacts(File edmxFile, File sourceOutputPath, File configOutputPath) {
		try {
			InputStream is = new FileInputStream(edmxFile);
			return generateArtifacts(is, sourceOutputPath, configOutputPath);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Generate JPA responder artifacts.  Including JPA classes, persistence.xml, and DML bootstrapping.
	 * @param is
	 * @param srcOutputPath
	 * @param configOutputPath
	 * @return
	 */
	public boolean generateArtifacts(InputStream is, File srcOutputPath, File configOutputPath) {
		XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(is)));
		EdmDataServices ds = new EdmxFormatParser().parseMetadata(reader);
		
		boolean ok = true;
		List<ResourceInfo> resourcesInfo = new ArrayList<ResourceInfo>();
		
		// generate JPA classes
		for (EdmEntityType t : ds.getEntityTypes()) {
			JPAEntityInfo entityInfo = createJPAEntityInfoFromEdmEntityType(t);
			String fqOutputDir = srcOutputPath.getPath() + "/" + entityInfo.getPackageAsPath();
			new File(fqOutputDir).mkdirs();
			if (writeClass(formClassFilename(srcOutputPath.getPath(), entityInfo), generateJPAEntityClass(entityInfo))) {
				String resourcePath = "/" + entityInfo.getClazz() + "({id})";
				String commandType = "com.temenos.interaction.commands.odata.GETEntityCommand"; 
				resourcesInfo.add(new ResourceInfo(resourcePath, entityInfo, commandType));
				
				//Navigation properties linking to itself with multiplicity are considered to relate a resource with a collection resource
				if(t.getNavigationProperties() != null) {
					for (EdmNavigationProperty np : t.getNavigationProperties()) {
						EdmAssociation ea = np.getRelationship();
						EdmAssociationEnd end1 = ea.getEnd1();
						EdmAssociationEnd end2 = ea.getEnd2();
						if(end1.getType().equals(end2.getType()) && (
								end1.getMultiplicity().equals(EdmMultiplicity.MANY) ||
								end2.getMultiplicity().equals(EdmMultiplicity.MANY))) {
							JPAEntityInfo entityFeedInfo = createJPAEntityInfoFromEdmEntityType(t);
							entityFeedInfo.setFeedEntity();		//This is a feed of OEntities and should not exist as a JPA entity 
							resourcePath = "/" + entityFeedInfo.getClazz();
							commandType = "com.temenos.interaction.commands.odata.GETEntitiesCommand"; 
							resourcesInfo.add(new ResourceInfo(resourcePath, entityFeedInfo, commandType));
						}					
					}
				}
					
			} else {
				ok = false;
			}
		}
		
		// generate persistence.xml
		if (!writeJPAConfiguration(configOutputPath, generateJPAConfiguration(resourcesInfo))) {
			ok = false;
		}

		// generate spring-beans.xml
		if (!writeSpringConfiguration(configOutputPath, generateSpringConfiguration(resourcesInfo))) {
			ok = false;
		}

		// generate responder insert
		if (!writeResponderDML(configOutputPath, generateResponderDML(resourcesInfo))) {
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
	public static String formClassFilename(String srcTargetDir, JPAEntityInfo entityInfo) {
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
	
	public JPAEntityInfo createJPAEntityInfoFromEdmEntityType(EdmType type) {
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
		
		return new JPAEntityInfo(entityType.getName(), entityType.getNamespace(), keyInfo, properties);
	}
	
	private String javaType(EdmType type) {
		// TODO support complex type keys?
		assert(type.isSimple());
		String javaType = null;
		if (EdmSimpleType.INT64 == type) {
			javaType = "Long";
		} else if (EdmSimpleType.INT32 == type) {
			javaType = "Integer";
		} else if (EdmSimpleType.STRING == type) {
			javaType = "String";
		} else if (EdmSimpleType.DATETIME == type) {
			javaType = "java.util.Date";
		} else if (EdmSimpleType.TIME == type) {
			javaType = "java.util.Date";
		} else {
			// TODO support types other than Long and String
			throw new RuntimeException("Entity property type not supported");
		}
		return javaType;
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
	
	/**
	 * Generate a JPA Entity from the provided info
	 * @precondition {@link JPAEntityInfo} non null
	 * @precondition {@link JPAEntityInfo} contain a valid package, class name, key, and fields
	 * @postcondition A valid java class, that may be serialised and compiled, or pushed into
	 * the {@link ClassLoader.defineClass}
	 * @param jpaEntityClass
	 * @return
	 */
	public String generateJPAEntityClass(JPAEntityInfo jpaEntityClass) {
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
	public String generateSpringConfiguration(List<ResourceInfo> resourcesInfo) {
		VelocityContext context = new VelocityContext();
		context.put("resourcesInfo", resourcesInfo);
		
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
				ok = rg.generateArtifacts(edmxFile, targetDirectory, targetDirectory);
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
