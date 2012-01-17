package com.temenos.interaction.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmType;

/**
 * Unit test for {@link ResponderGen}.
 */
public class ResponderGenTest {

	@Test
	public void testGenPOJOName() {
		ResponderGen rg = new ResponderGen();
		
		OEntity e = mock(OEntity.class);
		EdmEntityType t = mock(EdmEntityType.class);
		when(t.getFullyQualifiedTypeName()).thenReturn("AirlineModel.Flight");
		when(e.getType()).thenReturn(t);
		POJO p = rg.generatePOJOFromEntity(e);
		
		assertEquals("AirlineModel.Flight", p.getName());
	}
}
