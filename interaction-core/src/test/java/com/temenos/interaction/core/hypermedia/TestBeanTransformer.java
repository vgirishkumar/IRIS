package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
