package com.temenos.interaction.core.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.odata4j.edm.EdmDataServices;
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

	private final static String METADATA_XML_FILE = "metadata.xml";

	private EdmDataServices edmMetadata = null;
	private Metadata metadata = null;

	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager(Metadata metadata)
	{
		this.metadata = metadata;
		edmMetadata = populateOData4jMetadata(metadata);
	}

	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager(String metdataXml)
	{
		metadata = parseMetadataXML(metdataXml);
		edmMetadata = populateOData4jMetadata(metadata);
	}
	
	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager()
	{
		metadata = parseMetadataXML();
		edmMetadata = populateOData4jMetadata(metadata);
	}

	/**
	 * Return the entity model metadata
	 * @return metadata
	 */
	public Metadata getMetadata() {
		return this.metadata;
	}
	
	/**
	 * Return the odata4j metadata
	 * @return metadata
	 */
	public EdmDataServices getOData4jMetadata() {
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
	 * Parse the XML metadata string
	 */
	protected Metadata parseMetadataXML(String xml) {
		try {
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			return new MetadataParser().parse(is);
		}
		catch(Exception e) {
			logger.error("Failed to parse metadata xml: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to parse metadata xml: " + e.getMessage());
		}
	}

	/*
	 * Load the metadata from the metadata.xml file.
	 */
	protected EdmDataServices populateOData4jMetadata(Metadata metadata) {
		EdmDataServices edmMetadata;
		try {
			edmMetadata = new MetadataOData4j(metadata).getMetadata();
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to create odata4j metadata: " + e.getMessage());
		}
		return edmMetadata;
	}
}
