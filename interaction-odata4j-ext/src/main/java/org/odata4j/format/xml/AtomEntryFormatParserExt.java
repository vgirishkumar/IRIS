package org.odata4j.format.xml;

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
