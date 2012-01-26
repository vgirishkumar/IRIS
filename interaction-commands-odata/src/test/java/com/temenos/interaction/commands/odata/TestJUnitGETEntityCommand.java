package com.temenos.interaction.commands.odata;

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
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;

public class TestJUnitGETEntityCommand {

	@Test(expected = AssertionError.class)
	public void testEntitySetName() {

		ODataProducer mockProducer = mock(ODataProducer.class);

		List<String> keys = new ArrayList<String>();
		List<EdmProperty> properties = null;
		List<EdmNavigationProperty> navProperties = null;
		EdmEntityType mockEntityType = new EdmEntityType("namespace", "alias", "entity", false, keys, properties, navProperties);
		EdmEntitySet mockEntitySet = new EdmEntitySet("entity", mockEntityType);

		EdmDataServices mockEDS = mock(EdmDataServices.class);
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(mockEntitySet);
		when(mockProducer.getMetadata()).thenReturn(mockEDS);

		EntityResponse mockEntityResponse = mock(EntityResponse.class);
		when(mockEntityResponse.getEntity()).thenReturn(mock(OEntity.class));
		when(mockProducer.getEntity(anyString(), any(OEntityKey.class), any(QueryInfo.class))).thenReturn(mockEntityResponse);
				
		new GETEntityCommand("DOESNOTMATCH", mockProducer);
	}

}
