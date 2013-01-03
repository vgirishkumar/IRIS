package com.temenos.interaction.commands.odata;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmType;

public class TestCommandHelper {

	class MyEdmType extends EdmType {
		public MyEdmType(String name) {
			super(name);
		}
		public boolean isSimple() { return false; }
	}

	@Test
	public void testEntityKeyString() {
		EdmDataServices mockEDS = createMockEdmDataServices("MyEntity", "Edm.String");
		try {
			OEntityKey key = CommandHelper.createEntityKey(mockEDS, "MyEntity", "MyId");
			assertEquals("SINGLE", key.getKeyType().toString());
			assertEquals("MyId", key.asSingleValue().toString());
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test(expected = Exception.class)
	public void testEntityKeyUnsupportedKeyType() throws Exception {
		EdmDataServices mockEDS = createMockEdmDataServices("MyEntity", null);
		try {
			CommandHelper.createEntityKey(mockEDS, "MyEntity", "MyId");
		}
		catch(AssertionError ae) {
			throw new Exception(ae.getMessage());
		}
	}
	
	private EdmDataServices createMockEdmDataServices(String entityName, String keyTypeName) {
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
        return mockEDS;
	}
}
