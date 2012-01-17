package com.temenos.interaction.sdk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityType;
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
public class ResponderGen {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		
		ResponderGen rg = new ResponderGen();
		rg.test();
	}
	
	public POJO generatePOJOFromEntity(OEntity entity) {
		return new POJO(entity.getType().getFullyQualifiedTypeName());
	}
	
	public void test() {
	    InputStream is = getClass().getResourceAsStream("/edmx.xml");
		XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(is)));
		EdmDataServices ds = EdmxFormatParser.parseMetadata(reader);
		for (EdmEntityType t : ds.getEntityTypes()) {
			System.out.println(t.toString());
		}
	}
	
}
