package com.temenos.interaction.commands.odata;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;

import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.resource.ServiceDocumentResource;

public class TestGETMetadataCommand {

	@Test
	public void testMetadataResource() {
		ODataProducer mockProducer = createMockODataProducer();
		
		GETMetadataCommand command = new GETMetadataCommand("Metadata", mockProducer);
		RESTResponse rr = command.get("1", null);
		assertNotNull(rr);
		assertTrue(rr.getResource() instanceof MetaDataResource);
	}

	@Test
	public void testServiceDocumentResource() {
		ODataProducer mockProducer = createMockODataProducer();
		
		GETMetadataCommand command = new GETMetadataCommand("ServiceDocument", mockProducer);
		RESTResponse rr = command.get("1", null);
		assertNotNull(rr);
		assertTrue(rr.getResource() instanceof ServiceDocumentResource);
	}

	private ODataProducer createMockODataProducer() {
		ODataProducer mockProducer = mock(ODataProducer.class);
	
		List<String> keys = new ArrayList<String>();
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName("MyEntity").addKeys(keys);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("MyEntity").setEntityType(eet);
	
		EdmDataServices mockEDS = mock(EdmDataServices.class);
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(ees.build());
		when(mockProducer.getMetadata()).thenReturn(mockEDS);
	
		EntityResponse mockEntityResponse = mock(EntityResponse.class);
		when(mockEntityResponse.getEntity()).thenReturn(mock(OEntity.class));
		when(mockProducer.getEntity(anyString(), any(OEntityKey.class), any(QueryInfo.class))).thenReturn(mockEntityResponse);
		
		return mockProducer;
	}			
}
