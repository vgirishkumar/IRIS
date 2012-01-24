package com.temenos.interaction.core;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class TestCollectionResource {

	@Test
	public void testDeserialiseEntitiesObject() throws JAXBException {
		String testXMLString = "<collection-resource><Test/></collection-resource>";
		
		JAXBContext jc = JAXBContext.newInstance(CollectionResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        CollectionResource cr = (CollectionResource) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(cr);
        assertNotNull(cr.getEntities());
		assertEquals(1, cr.getEntities().size());
		assertTrue(cr.getEntities().iterator().next() instanceof NestedObject);
	}

	@Test
	public void testDeserialiseEntitiesObjectName() throws JAXBException {
		String testXMLString = "<collection-resource><Test><name>noah</name></Test></collection-resource>";
		
		JAXBContext jc = JAXBContext.newInstance(CollectionResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        CollectionResource cr = (CollectionResource) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(cr);
        assertNotNull(cr.getEntities());
		assertEquals(1, cr.getEntities().size());
		Object obj = cr.getEntities().iterator().next();
		assertTrue(obj instanceof NestedObject);
		assertEquals("noah", ((NestedObject)obj).getName());
	}

	@Test
	public void testDeserialiseEntitiesMultiple() throws JAXBException {
		String testXMLString = "<collection-resource><Test><name>Noah</name></Test><Test><name>Leo</name></Test></collection-resource>";
		
		JAXBContext jc = JAXBContext.newInstance(CollectionResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        CollectionResource cr = (CollectionResource) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(cr);
        assertNotNull(cr.getEntities());
		assertEquals(2, cr.getEntities().size());
		Iterator<Object> iterator = cr.getEntities().iterator();
		Object obj1 = iterator.next();
		assertTrue(obj1 instanceof NestedObject);
		assertEquals("Noah", ((NestedObject)obj1).getName());
		Object obj2 = iterator.next();
		assertTrue(obj2 instanceof NestedObject);
		assertEquals("Leo", ((NestedObject)obj2).getName());
	}

}
