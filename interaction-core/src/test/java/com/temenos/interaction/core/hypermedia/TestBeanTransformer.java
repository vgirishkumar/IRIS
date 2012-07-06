package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.temenos.interaction.core.hypermedia.BeanTransformer;
import com.temenos.interaction.core.hypermedia.Transformer;

public class TestBeanTransformer {

	class TestBean {
		TestBean(String s, int i) {
			stringField = s;
			intField = i;
		}
		private String stringField;
		private int intField;
		public String getStringField() { return stringField; }
		public int getIntField() { return intField; }
	};
	
	@Test
	public void testMapTransform() {
		Transformer t = new BeanTransformer();
		Map<String, Object> map = t.transform(new TestBean("A", 1));
		assertEquals(2, map.keySet().size());
		assertEquals("A", map.get("stringField"));
		assertEquals(1, map.get("intField"));
	}

}
