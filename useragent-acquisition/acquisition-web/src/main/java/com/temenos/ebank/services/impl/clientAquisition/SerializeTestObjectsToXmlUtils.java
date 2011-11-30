/**
 * 
 */
package com.temenos.ebank.services.impl.clientAquisition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ConfigParamTable;
import com.temenos.ebank.domain.Nomencl;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Utility class for serializing objects to xml using the Xstream library. 
 * Useful for creating test data starting from what already exists in the database. 
 * @author vionescu
 *
 */
public class SerializeTestObjectsToXmlUtils {

	public static void serializeToXml(Object o, String xmlFile) {
		FileWriter fw = null;
		try {
			XStream xstream = new XStream(new DomDriver());
			String xmlString = xstream.toXML(o);
			// System.out.println(xmlString);
			File fileToWrite = new File(xmlFile);
			System.out.println(fileToWrite.getAbsolutePath());
			fileToWrite.createNewFile();
			fw = new FileWriter(fileToWrite);
			fw.write(xmlString);
		} catch (Exception e) {
			throw new RuntimeException("Error serializing oject to xml", e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Serializes a list of nomencls to xml. The nomencls must share the same group code.
	 * @param a The application to save to xml
	 * @param folderContainingXmls Folder where to save the xml file
	 */
	public static void serializeNomenclToXml(List<Nomencl> nomencls, String folderContainingXmls) {
		if (CollectionUtils.isNotEmpty(nomencls)) { 
			String groupCode = nomencls.get(0).getGroupCode();
		//"D:\\temp\\apprefs" 
			File fileName = new File (folderContainingXmls, groupCode + ".xml");
			if (!fileName.exists()) {
				serializeToXml(nomencls, fileName.getPath());	
			}
			
		}
	}
	
	
	/**
	 * Serializes an application to xml.
	 * @param a
	 * @param folderContainingXmls Folder where to save the xml file
	 */
	public static void serializeAppToXml(Application a, String folderContainingXmls) {
		//"D:\\temp\\apprefs" 
		serializeToXml(a, folderContainingXmls + "\\" +  a.getAppRef() + ".xml");
	}
	
	/**
	 * Serializes a config param table to xml.
	 * @param cpt
	 * @param folderContainingXmls Folder where to save the xml file
	 */
	public static void serializeConfigParamToXml(ConfigParamTable cpt, String folderContainingXmls) {
		File fileName = new File (folderContainingXmls, "configParamTable.xml");
		if (!fileName.exists()) {
			serializeToXml(cpt, fileName.getPath());	
		}
	}
}
