package com.temenos.interaction.core.resource;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.temenos.interaction.core.NestedObject;

public class TestCollectionResource {

	@SuppressWarnings("unchecked")
	@Test
	public void testDeserialiseEntitiesObject() throws JAXBException {
		String testXMLString = "<collection-resource><Test/></collection-resource>";
		
		JAXBContext jc = JAXBContext.newInstance(CollectionResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        CollectionResource<NestedObject> cr = (CollectionResource<NestedObject>) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(cr);
        assertNotNull(cr.getEntities());
		assertEquals(1, cr.getEntities().size());
		assertTrue(cr.getEntities().iterator().next() instanceof NestedObject);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDeserialiseEntitiesObjectName() throws JAXBException {
		String testXMLString = "<collection-resource><Test><name>noah</name></Test></collection-resource>";
		
		JAXBContext jc = JAXBContext.newInstance(CollectionResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        CollectionResource<NestedObject> cr = (CollectionResource<NestedObject>) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(cr);
        assertNotNull(cr.getEntities());
		assertEquals(1, cr.getEntities().size());
		Object obj = cr.getEntities().iterator().next();
		assertTrue(obj instanceof NestedObject);
		assertEquals("noah", ((NestedObject)obj).getName());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDeserialiseEntitiesMultiple() throws JAXBException {
		String testXMLString = "<collection-resource><Test><name>Noah</name></Test><Test><name>Leo</name></Test></collection-resource>";
		
		JAXBContext jc = JAXBContext.newInstance(CollectionResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        CollectionResource<NestedObject> cr = (CollectionResource<NestedObject>) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(cr);
        assertNotNull(cr.getEntities());
		assertEquals(2, cr.getEntities().size());
		Iterator<NestedObject> iterator = cr.getEntities().iterator();
		NestedObject obj1 = iterator.next();
		assertEquals("Noah", ((NestedObject)obj1).getName());
		NestedObject obj2 = iterator.next();
		assertEquals("Leo", ((NestedObject)obj2).getName());
	}

}
