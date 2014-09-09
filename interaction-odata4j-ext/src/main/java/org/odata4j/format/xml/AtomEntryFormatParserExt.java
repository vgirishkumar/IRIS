package org.odata4j.format.xml;

/*
 * #%L
 * interaction-odata4j-ext
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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


import java.io.Reader;

import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.Entry;
import org.odata4j.internal.FeedCustomizationMapping;

import com.temenos.interaction.odataext.entity.MetadataOData4j;

public class AtomEntryFormatParserExt extends AtomEntryFormatParser {

	MetadataOData4j metadataOdata4j;
	
	public AtomEntryFormatParserExt(MetadataOData4j metadataOdata4j,
			String entitySetName, OEntityKey entityKey,
			FeedCustomizationMapping fcMapping) {
		super(metadataOdata4j.getMetadata(), entitySetName, entityKey, fcMapping);
		
		this.metadataOdata4j = metadataOdata4j;
	}
	
	public AtomEntryFormatParserExt(EdmDataServices metadata,
			String entitySetName, OEntityKey entityKey,
			FeedCustomizationMapping fcMapping) {
		super(metadata, entitySetName, entityKey, fcMapping);
	}
	
	
	 @Override
	  public Entry parse(Reader reader) {
		 AtomFeedFormatParserExt parser = null;
		 
		if(metadataOdata4j == null) {
			parser = new AtomFeedFormatParserExt(metadata, entitySetName, entityKey, fcMapping);			
		} else {
		    parser = new AtomFeedFormatParserExt(metadataOdata4j, entitySetName, entityKey, fcMapping);			
		}
		
		return parser.parse(reader).entries.iterator().next();
	  }


}
