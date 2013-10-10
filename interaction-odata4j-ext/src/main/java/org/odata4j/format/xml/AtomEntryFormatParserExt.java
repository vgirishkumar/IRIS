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
import org.odata4j.format.xml.AtomEntryFormatParser;
import org.odata4j.internal.FeedCustomizationMapping;

public class AtomEntryFormatParserExt extends AtomEntryFormatParser {

	public AtomEntryFormatParserExt(EdmDataServices metadata,
			String entitySetName, OEntityKey entityKey,
			FeedCustomizationMapping fcMapping) {
		super(metadata, entitySetName, entityKey, fcMapping);
	}
	
	 @Override
	  public Entry parse(Reader reader) {
	    return new AtomFeedFormatParserExt(metadata, entitySetName, entityKey, fcMapping)
	        .parse(reader).entries.iterator().next();
	  }


}
