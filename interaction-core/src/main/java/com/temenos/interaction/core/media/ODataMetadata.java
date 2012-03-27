package com.temenos.interaction.core.media;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.stax2.XMLEventReader2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class provides EDM metadata for the current service.
 */
public class ODataMetadata {
	private final static Logger logger = LoggerFactory.getLogger(ODataMetadata.class);

	private final static String EDMX_FILENAME = "service.edmx";
	private EdmDataServices metadata = null;

	/**
	 * Construct the odata metadata object
	 */
	public ODataMetadata()
	{
		metadata = parseEdmx();
	}
	
	/**
	 * Return the metadata
	 * @return metadata
	 */
	public EdmDataServices getMetadata() {
		return this.metadata;
	}

	/*
	 * Parse the EDMX file
	 */
	protected EdmDataServices parseEdmx() {
		try {
			InputStreamReader stream = new InputStreamReader(getClass().getResourceAsStream("/" + EDMX_FILENAME));
			XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(stream)); 
			return new EdmxFormatParser().parseMetadata(reader);
		}
		catch(Exception e) {
			logger.error("Failed to parse EDMX file " + EDMX_FILENAME);
			throw new RuntimeException("Failed to parse EMDX file " + EDMX_FILENAME);
		}
	}
}
