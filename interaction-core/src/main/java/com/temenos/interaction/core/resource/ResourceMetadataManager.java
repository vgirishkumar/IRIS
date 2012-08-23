package com.temenos.interaction.core.resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.stax2.XMLEventReader2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.MetadataOData4j;
import com.temenos.interaction.core.entity.MetadataParser;


/**
 * This class provides EDM metadata for the current service.
 */
public class ResourceMetadataManager {
	private final static Logger logger = LoggerFactory.getLogger(ResourceMetadataManager.class);

	private final static String EDMX_FILENAME = "service.edmx";
	private final static String METADATA_XML_FILE = "metadata.xml";

	private EdmDataServices edmMetadata = null;
	private Metadata metadata = null;

	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager(Metadata metadata)
	{
		this.metadata = metadata;
		try {
			edmMetadata = new MetadataOData4j(metadata).getMetadata();
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to create odata4j metadata: " + e.getMessage());
		}
	}

	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager()
	{
		edmMetadata = parseEdmx();
		metadata = parseMetadataXML();
		try {
			edmMetadata = new MetadataOData4j(metadata).getMetadata();
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to create odata4j metadata: " + e.getMessage());
		}
	}

	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager(boolean loadMetadataXML)
	{
		if(loadMetadataXML) {
			metadata = parseMetadataXML();
		}
		edmMetadata = parseEdmx();
	}
	
	/**
	 * Return the entity model metadata
	 * @return metadata
	 */
	public Metadata getResourceMetadata() {
		return this.metadata;
	}
	
	/**
	 * Return the odata4j metadata
	 * @return metadata
	 */
	public EdmDataServices getMetadata() {
		return this.edmMetadata;
	}

	/*
	 * Parse the XML metadata file
	 */
	protected Metadata parseMetadataXML() {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(METADATA_XML_FILE);
			if(is == null) {
				throw new Exception("Unable to load " + METADATA_XML_FILE + " from classpath.");
			}
			return new MetadataParser().parse(is);
		}
		catch(Exception e) {
			logger.error("Failed to parse " + METADATA_XML_FILE + ": " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to parse " + METADATA_XML_FILE + ": " + e.getMessage());
		}
	}
	
	/*
	 * Parse the EDMX file
	 */
	protected EdmDataServices parseEdmx() {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(EDMX_FILENAME);
			if(is == null) {
				throw new Exception("Unable to load resource from classpath.");
			}
			XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(is))); 
			return new EdmxFormatParser().parseMetadata(reader);
		}
		catch(Exception e) {
			logger.error("Failed to parse EDMX file " + EDMX_FILENAME + ": " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to parse EDMX file " + EDMX_FILENAME + ": " + e.getMessage());
		}
	}
}
