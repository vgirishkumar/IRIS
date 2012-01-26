package com.temenos.interaction.commands.odata;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OEntityKey.class, GETEntityCommand.class})
public class TestPowermockGETEntityCommand {

	@Test
	public void testEntityKeyType() {
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
				
		mockStatic(OEntityKey.class);
        when(OEntityKey.create(anyLong())).thenReturn(mock(OEntityKey.class));

		GETEntityCommand gec = new GETEntityCommand("entity", mockProducer);
		
		// test our method
		RESTResponse rr = gec.get("1L", null);
		
		PowerMockito.verifyStatic();
//		OEntityKey.create(new Long(1L));
		OEntityKey.create("1L");
		
		assertNotNull(rr);
		assertTrue(rr.getResource() instanceof EntityResource);
	}

}
