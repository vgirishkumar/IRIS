package com.temenos.interaction.sdk;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestJPAEntityInfo {

	@Test
	public void testGetPackageAsPath() {
		JPAEntityInfo ei = new JPAEntityInfo("BlahClass", "com", null, null);
		assertEquals("com", ei.getPackageAsPath());
		JPAEntityInfo ei1 = new JPAEntityInfo("BlahClass", "com.temenos", null, null);
		assertEquals("com/temenos", ei1.getPackageAsPath());
		JPAEntityInfo ei2 = new JPAEntityInfo("BlahClass", "com.temenos.stuff", null, null);
		assertEquals("com/temenos/stuff", ei2.getPackageAsPath());
		JPAEntityInfo ei3 = new JPAEntityInfo("BlahClass", "", null, null);
		assertEquals("", ei3.getPackageAsPath());
		JPAEntityInfo ei4 = new JPAEntityInfo("BlahClass", null, null, null);
		assertEquals("", ei4.getPackageAsPath());
	}

	@Test
	public void testGetFQTypeName() {
		JPAEntityInfo ei = new JPAEntityInfo("BlahClass", "com.temenos.stuff", null, null);
		assertEquals("com.temenos.stuff.BlahClass", ei.getFQTypeName());
	}

	@Test (expected = AssertionError.class)
	public void testGetFQTypeNameNullClass() {
		JPAEntityInfo ei = new JPAEntityInfo(null, "com.temenos.stuff", null, null);
		ei.getFQTypeName();
	}

	@Test
	public void testGetFQTypeNameNullPackage() {
		JPAEntityInfo ei = new JPAEntityInfo("BlahClass", null, null, null);
		assertEquals("BlahClass", ei.getFQTypeName());
	}

}
