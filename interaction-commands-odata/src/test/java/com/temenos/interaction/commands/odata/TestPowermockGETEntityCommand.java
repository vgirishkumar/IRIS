package com.temenos.interaction.commands.odata;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmType;
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

	class MyEdmType extends EdmType {
		public MyEdmType(String name) {
			super(name);
		}
		public boolean isSimple() { return false; }
	}
	
	@Test
	public void testEntityKeyTypeString() {
		ODataProducer mockProducer = createMockODataProducer("MyEntity", "Edm.String");
		GETEntityCommand gec = new GETEntityCommand("MyEntity", mockProducer);
		
		// test our method
		RESTResponse rr = gec.get("1", null);
		
		PowerMockito.verifyStatic();
		assertNotNull(rr);
		assertTrue(rr.getResource() instanceof EntityResource);
	}

	@Test
	public void testEntityKeyInt64() {
		ODataProducer mockProducer = createMockODataProducer("MyEntity", "Edm.Int64");
		GETEntityCommand gec = new GETEntityCommand("MyEntity", mockProducer);
		
		// test our method
		RESTResponse rr = gec.get("1", null);
		
		PowerMockito.verifyStatic();
		assertNotNull(rr);
		assertTrue(rr.getResource() instanceof EntityResource);
	}

	@Test
	public void testEntityKeyInt64Error() {
		ODataProducer mockProducer = createMockODataProducer("MyEntity", "Edm.Int64");
		GETEntityCommand gec = new GETEntityCommand("MyEntity", mockProducer);
		
		// test our method
		RESTResponse rr = gec.get("A1", null);
		
		PowerMockito.verifyStatic();
		assertNotNull(rr);
		assertTrue(rr.getResource() == null);
	}
	
	@Test
	public void testEntityKeyTimestamp() {
		ODataProducer mockProducer = createMockODataProducer("MyEntity", "Edm.DateTime");
		GETEntityCommand gec = new GETEntityCommand("MyEntity", mockProducer);
		
		// test our method
		RESTResponse rr = gec.get("2012-02-06 18:05:53", null);
		
		PowerMockito.verifyStatic();
		assertNotNull(rr);
		assertTrue(rr.getResource() instanceof EntityResource);
	}

	@Test
	public void testEntityKeyTime() {
		ODataProducer mockProducer = createMockODataProducer("MyEntity", "Edm.Time");
		GETEntityCommand gec = new GETEntityCommand("MyEntity", mockProducer);
		
		// test our method
		RESTResponse rr = gec.get("18:05:53", null);
		
		PowerMockito.verifyStatic();
		assertNotNull(rr);
		assertTrue(rr.getResource() instanceof EntityResource);
	}
	
	private ODataProducer createMockODataProducer(String entityName, String keyTypeName) {
		ODataProducer mockProducer = mock(ODataProducer.class);
		List<String> keys = new ArrayList<String>();
		keys.add("MyId");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("MyId").setType(new MyEdmType(keyTypeName));
		properties.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName(entityName).addKeys(keys).addProperties(properties);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName(entityName).setEntityType(eet);

		List<EdmEntityType> mockEntityTypes = new ArrayList<EdmEntityType>();
		mockEntityTypes.add(eet.build());

		EdmDataServices mockEDS = mock(EdmDataServices.class);
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(ees.build());
		when(mockEDS.getEntityTypes()).thenReturn(mockEntityTypes);
		when(mockProducer.getMetadata()).thenReturn(mockEDS);

		EntityResponse mockEntityResponse = mock(EntityResponse.class);
		when(mockEntityResponse.getEntity()).thenReturn(mock(OEntity.class));
		when(mockProducer.getEntity(anyString(), any(OEntityKey.class), any(QueryInfo.class))).thenReturn(mockEntityResponse);
				
		mockStatic(OEntityKey.class);
        when(OEntityKey.create(anyLong())).thenReturn(mock(OEntityKey.class));
        
        return mockProducer;
	}
	
}
