package com.temenos.interaction.core.resource;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.temenos.interaction.core.NestedObject;

public class TestEntityResource {

	@SuppressWarnings("unchecked")
	@Test
	public void testEntityObject() throws JAXBException {
		String testXMLString = "<resource><Test/></resource>";
		
		JAXBContext jc = JAXBContext.newInstance(EntityResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        EntityResource<NestedObject> er = (EntityResource<NestedObject>) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(er);
        assertNotNull(er.getEntity());
		assertTrue(er.getEntity() instanceof NestedObject);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEntityObjectName() throws JAXBException {
		String testXMLString = "<resource><Test><name>noah</name></Test></resource>";
		
		JAXBContext jc = JAXBContext.newInstance(EntityResource.class, NestedObject.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        EntityResource<NestedObject> er = (EntityResource<NestedObject>) unmarshaller.unmarshal(new ByteArrayInputStream(testXMLString.getBytes()));
 
        assertNotNull(er);
        assertNotNull(er.getEntity());
		assertEquals("noah", ((NestedObject)er.getEntity()).getName());
	}

}
