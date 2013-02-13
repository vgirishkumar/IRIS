package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class TestBeanTransformer {

	@Test
	public void testMapTransform() {
		Transformer t = new BeanTransformer();
		Map<String, Object> map = t.transform(new TestBean("A", 1));
		assertEquals(2, map.keySet().size());
		assertEquals("A", map.get("stringField"));
		assertEquals(1, map.get("intField"));
	}

}
