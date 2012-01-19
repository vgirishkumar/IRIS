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
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityType;
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
	public static void main(String[] args) {
		boolean ok = false;
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
				ok = rg.generateArtifacts(edmxFile, targetDirectory);
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
	 * @precondition File edmxFile exists on the file system
	 * @postcondition JPA Entity classes written to file system as valid Java source
	 * @postcondition JPA persistence.xml written to file system, configured to inmemory database
	 * @postcondition a boolean flag indicating a successful result will be returned
	 * @invariant enough free space on the file system
	 * @param is
	 */
	public boolean generateArtifacts(File edmxFile, File outputPath) {
		try {
			InputStream is = new FileInputStream(edmxFile);
			return generateArtifacts(is, outputPath);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * 
	 * @param is
	 * @param outputPath
	 * @return
	 */
	public boolean generateArtifacts(InputStream is, File outputPath) {
		XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(is)));
		EdmDataServices ds = EdmxFormatParser.parseMetadata(reader);
		
		
		// generate JPA classes
		for (EdmEntityType t : ds.getEntityTypes()) {
			JPAEntityInfo entityInfo = createJPAEntityClassFromEdmEntityType(t);
			String fqOutputDir = outputPath.getPath() + "/" + entityInfo.getPackageAsPath();
			new File(fqOutputDir).mkdirs();
			writeClass(formClassFilename(outputPath.getPath(), entityInfo), generateJPAEntityClass(entityInfo));
			
		}

		// generate persistence.xml
		
		return true;
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
	
	private void writeClass(String classFileName, String generatedClass) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(classFileName);
			fos.write(generatedClass.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// don't hide original exception
			}
		}
		
	}
	
	public JPAEntityInfo createJPAEntityClassFromEdmEntityType(EdmType type) {
		if (!(type instanceof EdmEntityType))
			return null;
		
		EdmEntityType entityType = (EdmEntityType) type;
		// think OData4j only support single keys at the moment
		FieldInfo keyInfo = null;
		if (entityType.getKeys().size() > 0) {
			String keyName = entityType.getKeys().get(0);
			EdmType key = null;
			for (EdmProperty e : entityType.getProperties()) {
				if (e.name.equals(keyName)) {
					key = e.type;
				}
			}
			keyInfo = new FieldInfo(keyName, javaType(key));
		}
		
		List<FieldInfo> properties = new ArrayList<FieldInfo>();
		for (EdmProperty property : entityType.getProperties()) {
			FieldInfo field = new FieldInfo(property.name, javaType(property.type));
			if (!field.equals(keyInfo)) {
				properties.add(field);
			}
		}
		
		return new JPAEntityInfo(entityType.name, entityType.namespace, keyInfo, properties);
	}
	
	private String javaType(EdmType type) {
		// TODO support complex type keys?
		// TODO support types other than Long and String
		assert(type.isSimple());
		String javaType = "String";
		if (EdmSimpleType.INT64 == type) {
			javaType = "Long";
		} else if (EdmSimpleType.STRING == type) {
			javaType = "String";
		}
		return javaType;
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
		
		/*
		 *  create a new instance of the engine
		 */
		VelocityEngine ve = new VelocityEngine();
		// load .vm templates using classloader
		ve.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
		ve.init();

		VelocityContext context = new VelocityContext();
		context.put("jpaentity", jpaEntityClass);
		
		
		Template t = ve.getTemplate("/JPAEntity.vm");
		StringWriter sw = new StringWriter();
		t.merge(context, sw);
		return sw.toString();
	}
	
}
