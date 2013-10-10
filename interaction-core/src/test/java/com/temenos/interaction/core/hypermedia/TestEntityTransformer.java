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
