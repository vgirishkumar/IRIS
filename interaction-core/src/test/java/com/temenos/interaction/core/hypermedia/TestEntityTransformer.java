package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;

public class TestEntityTransformer {

	@Test
	public void testMapTransform() {
		Entity entity = createMockEntity();
		
		Transformer t = new EntityTransformer();
		Map<String, Object> map = t.transform(entity);
		
		assertEquals(2, map.keySet().size());
		assertEquals("123", map.get("id"));
		assertEquals("EI218", map.get("flight"));
	}

	@Test
	public void testTypeNotSupported() {
		EntityTransformer transformer = new EntityTransformer();
		assertNull(transformer.transform(""));
	}

	private Entity createMockEntity() {
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("id", "123"));
		customerFields.setProperty(new EntityProperty("flight", "EI218"));
		Entity mock = new Entity("Flight", customerFields);
		return mock;
	}	
}
