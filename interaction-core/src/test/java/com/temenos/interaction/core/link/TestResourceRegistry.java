package com.temenos.interaction.core.link;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.internal.InternalUtil;
import org.odata4j.stax2.XMLEventReader2;

import com.temenos.interaction.core.state.ResourceInteractionModel;

public class TestResourceRegistry {

	@Test
	public void testRebuildOEntityLinksAssociations() {
		InputStream in = ClassLoader.getSystemResourceAsStream("com/temenos/interaction/core/link/TestResourceRegistryEDMX.xml");
		XMLEventReader2 reader =  InternalUtil.newXMLEventReader(new BufferedReader(new InputStreamReader(in)));
		EdmxFormatParser formatParser = new EdmxFormatParser();
		EdmDataServices ds = formatParser.parseMetadata(reader);
		for (EdmEntityType type : ds.getEntityTypes()) {
			System.out.println("type: " + type.getName());
		}
		assertNotNull(ds.findEdmEntityType("AirlineModel.Flight"));
		
		EdmEntitySet entitySet = ds.findEdmEntitySet("Flight");
		OEntityKey entityKey = OEntityKey.create("123");
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		List<OLink> links = new ArrayList<OLink>();
		OEntity oEntity = OEntities.create(entitySet, entityKey, properties, links);
		
		ResourceRegistry rr = new ResourceRegistry();
		ResourceInteractionModel airport = mock(ResourceInteractionModel.class);
		when(airport.getEntityName()).thenReturn("FlightSchedule");
		when(airport.getResourcePath()).thenReturn("/FS/{id}");
		rr.add(airport);
		OEntity newOEntity = rr.rebuildOEntityLinks(oEntity, null);
		
		List<OLink> entityLinks = newOEntity.getLinks();
		assertEquals(1, entityLinks.size());
		assertEquals("/FS/{id}", entityLinks.get(0).getHref());
		assertEquals("http://schemas.microsoft.com/ado/2007/08/dataservices/related/FlightSchedule", entityLinks.get(0).getRelation());
		assertEquals("FlightSchedule", entityLinks.get(0).getTitle());
		
	}

}
