package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;

public class TestStrategyBasedTransformer {

	@Test
	public void testDefaultTransformerBean() {
		Transformer t = new StrategyBasedTransformer();
		Map<String, Object> map = t.transform(new TestBean("A", 1));
		assertEquals(2, map.keySet().size());
		assertEquals("A", map.get("stringField"));
		assertEquals(1, map.get("intField"));
	}

	@Test
	public void testDefaultTransformerEntity() {
		EntityProperties props = new EntityProperties();
		props.setProperty(new EntityProperty("stringField", "A"));
		props.setProperty(new EntityProperty("intField", 1));
		Entity testEntity = new Entity("TestEntity", props);
		
		Transformer t = new StrategyBasedTransformer();
		Map<String, Object> map = t.transform(testEntity);
		assertEquals(2, map.keySet().size());
		assertEquals("A", map.get("stringField"));
		assertEquals(1, map.get("intField"));
	}
	
	@Test
	public void testDefaultTransformerNull() {
		Transformer t = new StrategyBasedTransformer();
		Map<String, Object> map = t.transform(null);
		assertEquals(0, map.keySet().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSuppliedStrategyNull() {
		new StrategyBasedTransformer(null);
	}

	@Test
	public void testSuppliedStrategyNone() {
		Transformer t = new StrategyBasedTransformer(new ArrayList<Transformer>());
		t.transform(null);
	}

	@Test
	public void testSuppliedStrategyCollection() {
		List<Transformer> transformers = new ArrayList<Transformer>();
		transformers.add(new BeanTransformer());
		Transformer t = new StrategyBasedTransformer(transformers);
		Map<String, Object> map = t.transform(new TestBean("A", 1));
		assertEquals(2, map.keySet().size());
		assertEquals("A", map.get("stringField"));
		assertEquals(1, map.get("intField"));
	}

}
