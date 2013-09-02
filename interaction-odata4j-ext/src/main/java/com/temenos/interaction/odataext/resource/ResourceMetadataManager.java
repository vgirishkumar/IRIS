package com.temenos.interaction.odataext.resource;

import org.odata4j.edm.EdmDataServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.TermFactory;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.odataext.entity.MetadataOData4j;


/**
 * This class provides EDM metadata for the current service.
 */
public class ResourceMetadataManager extends com.temenos.interaction.core.resource.ResourceMetadataManager {
	private final static Logger logger = LoggerFactory.getLogger(ResourceMetadataManager.class);

	private EdmDataServices edmMetadata = null;

	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager(Metadata metadata, ResourceStateMachine hypermediaEngine)
	{
		super(metadata, hypermediaEngine);
		edmMetadata = populateOData4jMetadata(metadata, hypermediaEngine);
	}

	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager(String metdataXml, ResourceStateMachine hypermediaEngine)
	{
		super(metdataXml, hypermediaEngine);
		edmMetadata = populateOData4jMetadata(getMetadata(), hypermediaEngine);
	}
	
	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager(ResourceStateMachine hypermediaEngine)
	{
		super(hypermediaEngine);
		edmMetadata = populateOData4jMetadata(getMetadata(), hypermediaEngine);
	}

	/**
	 * Construct the metadata object
	 */
	public ResourceMetadataManager(ResourceStateMachine hypermediaEngine, TermFactory termFactory)
	{
		super(hypermediaEngine, termFactory);
		edmMetadata = populateOData4jMetadata(getMetadata(), hypermediaEngine);
	}
	
	/**
	 * Return the odata4j metadata
	 * @return metadata
	 */
	public EdmDataServices getOData4jMetadata() {
		return this.edmMetadata;
	}
	
	/*
	 * Parse the XML metadata file with the default Vocabulary Term Factory
	 */
	protected Metadata parseMetadataXML() {
		return parseMetadataXML(new TermFactory());
	}

	/*
	 * Load the metadata from the metadata.xml file.
	 */
	protected EdmDataServices populateOData4jMetadata(Metadata metadata, ResourceStateMachine hypermediaEngine) {
		EdmDataServices edmMetadata;
		try {
			edmMetadata = new MetadataOData4j(metadata, hypermediaEngine).getMetadata();
		}
		catch(Exception e) {
			logger.error("Failed to create odata4j metadata", e);
			throw new RuntimeException("Failed to create odata4j metadata: " + e.getMessage());
		}
		return edmMetadata;
	}
}
