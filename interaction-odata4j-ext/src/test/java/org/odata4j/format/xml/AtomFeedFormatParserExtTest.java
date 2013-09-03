package org.odata4j.format.xml;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.Entry;
import org.odata4j.format.xml.AtomFeedFormatParser.AtomFeed;
import org.odata4j.internal.FeedCustomizationMapping;
import org.odata4j.stax2.XMLEventReader2;
import org.odata4j.stax2.XMLFactoryProvider2;

/**
 * This is to verify the extension written for Odata4j Issue https://code.google.com/p/odata4j/issues/detail?id=193
 * The change has been contributed to the project.
 * 
 * @author sjunejo
 *
 */

public class AtomFeedFormatParserExtTest {

	// Common
	private static EdmDataServices edmMetadata;
	private static final String entitySetName = "FtCommissionTypes";
	private static final String entityKeyValue = "SCTRDEFAULT";
	private static OEntityKey key;

	@BeforeClass
	public static void setup() {
		edmMetadata = getMetadata();
		key = OEntityKey.create(entityKeyValue);
	}

	@AfterClass
	public static void tearDown() {
		edmMetadata = null;
		key = null;
	}

	/**
	 * Basic Bag(...) type support
	 */
	@Test
	public void testParsePropertiesWithBag() {
		InputStream xml = getClass().getClassLoader().getResourceAsStream("issue193_entry_with_Bag.xml");
		FeedCustomizationMapping fcMapping = new FeedCustomizationMapping();
		AtomFeed feed = new AtomFeedFormatParserExt(edmMetadata, entitySetName, key,  fcMapping).parse(new InputStreamReader(xml));
		Assert.assertNotNull(feed);
		Entry entry = feed.entries.iterator().next();
		assertNotNull(entry);
		OEntity entity = entry.getEntity();
		assertNotNull(entity);

		// We can add more asserts here to verify each OProperty

	}

	/**
	 * This test will verify that we can parse Entry posted by Odata (.NET) Clients with Collection(...)
	 */
	@Test
	public void testParsePropertiesWithCollection() {
		InputStream xml = getClass().getClassLoader().getResourceAsStream("issue193_entry_with_Collection.xml");
		FeedCustomizationMapping fcMapping = new FeedCustomizationMapping();
		AtomFeed feed = new AtomFeedFormatParserExt(edmMetadata, entitySetName, key, fcMapping).parse(new InputStreamReader(xml));
		Assert.assertNotNull(feed);
		Entry entry = feed.entries.iterator().next();
		assertNotNull(entry);
		OEntity entity = entry.getEntity();
		assertNotNull(entity);
	}
	
	// Load the metadata
	private static EdmDataServices getMetadata() {
		InputStream metadataStream = AtomFeedFormatParserExtTest.class.getClassLoader().getResourceAsStream("issue193_metadata.xml");
		XMLEventReader2 reader = XMLFactoryProvider2.getInstance().newXMLInputFactory2().createXMLEventReader(new InputStreamReader(metadataStream));
		return new EdmxFormatParser().parseMetadata(reader);
	}

}
