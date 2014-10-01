package com.temenos.interaction.loader.xml.resource.action;

/*
 * #%L
 * interaction-dynamic-loader
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.loader.action.Action;
import com.temenos.interaction.loader.xml.XmlChangedEvent;
import com.temenos.interaction.odataext.entity.MetadataOData4j;

/**
 * This class performs the necessary updates to IRIS in memory meta data when the underlying meta data changes 
 *
 * @author mlambert
 *
 */
public class IRISMetadataChangedAction implements Action<XmlChangedEvent> {
	private Metadata metadata;
	private MetadataOData4j metadataOData4j;
	private Pattern pattern = Pattern.compile(".*-(.*).xml");
	
	private final Logger logger = LoggerFactory.getLogger(IRISMetadataChangedAction.class);	
		
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * @param metadataOData4j the metadataOData4j to set
	 */
	public void setMetadataOData4j(MetadataOData4j metadataOData4j) {
		this.metadataOData4j = metadataOData4j;
	}
	
	@Override
	public void execute(XmlChangedEvent event) {
		String filename = event.getResource().getFilename();

		// Unload all meta data relating to the entity

		Matcher matcher = pattern.matcher(filename);
		
		if(!matcher.find()) {
			logger.warn("Failed to retrieve entity name from " + filename);
			
			return;
		}
		
		String entityName = matcher.group(1);
		
		// Unload IRIS internal meta data
		metadata.unload(entityName);

		// Unload EDM meta data
		metadataOData4j.unloadMetadata(entityName);
	}
}
