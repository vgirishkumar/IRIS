package com.temenos.interaction.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestJPAEntityInfo {

	@Test
	public void testGetPackageAsPath() {
		EntityInfo ei = new EntityInfo("BlahClass", "com", null, null);
		assertEquals("com", ei.getPackageAsPath());
		EntityInfo ei1 = new EntityInfo("BlahClass", "com.temenos", null, null);
		assertEquals("com/temenos", ei1.getPackageAsPath());
		EntityInfo ei2 = new EntityInfo("BlahClass", "com.temenos.stuff", null, null);
		assertEquals("com/temenos/stuff", ei2.getPackageAsPath());
		EntityInfo ei3 = new EntityInfo("BlahClass", "", null, null);
		assertEquals("", ei3.getPackageAsPath());
		EntityInfo ei4 = new EntityInfo("BlahClass", null, null, null);
		assertEquals("", ei4.getPackageAsPath());
	}

	@Test
	public void testGetFQTypeName() {
		EntityInfo ei = new EntityInfo("BlahClass", "com.temenos.stuff", null, null);
		assertEquals("com.temenos.stuff.BlahClass", ei.getFQTypeName());
	}

	@Test (expected = AssertionError.class)
	public void testGetFQTypeNameNullClass() {
		EntityInfo ei = new EntityInfo(null, "com.temenos.stuff", null, null);
		ei.getFQTypeName();
		assertNotNull(ei.getClazz());
	}

	@Test
	public void testGetFQTypeNameNullPackage() {
		EntityInfo ei = new EntityInfo("BlahClass", null, null, null);
		assertEquals("BlahClass", ei.getFQTypeName());
	}

}
