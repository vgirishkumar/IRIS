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


/**
 * This class provides EDM metadata for the current service.
 */
public class ResourceMetadataManager {
	private final static Logger logger = LoggerFactory.getLogger(ResourceMetadataManager.class);

	private final static String EDMX_FILENAME = "service.edmx";
	private EdmDataServices edmMetadata = null;
	
	private Metadata metadata = null;

	/**
	 * Construct the odata metadata object
	 */
	public ResourceMetadataManager(Metadata metadata)
	{
		this.metadata = metadata;
		edmMetadata = parseEdmx();
	}

	/**
	 * Construct the odata metadata object
	 */
	public ResourceMetadataManager()
	{
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
