package com.temenos.interaction.core.command;

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


import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.GenericEntity;

import org.junit.Test;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.GenericError;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;

public class TestCommandHelper {

	@Test
	public void testCreateEntityResource() {
		EntityResource<GenericError> er = CommandHelper.createEntityResource(new GenericError("123", "My error message"));

		GenericEntity<EntityResource<GenericError>> ge = er.getGenericEntity();
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class));
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class, GenericError.class));
	}
	
	@Test
	public void testCreateEntityResourceWithEntityName() {
		EntityResource<Entity> er = CommandHelper.createEntityResource("Customer", createMockEntity("Customer"));

		GenericEntity<EntityResource<Entity>> ge = er.getGenericEntity();
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class));
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class, Entity.class));
	}

	@Test
	public void testCreateEntityResourceWithExplicitType() {
		EntityResource<Entity> er = CommandHelper.createEntityResource("Customer", createMockEntity("Customer"), Entity.class);

		GenericEntity<EntityResource<Entity>> ge = er.getGenericEntity();
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class));
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class, Entity.class));
	}

	@Test(expected=AssertionError.class)
	public void testCreateEntityResourceWithWrongExplicitType() {
		EntityResource<Entity> er = CommandHelper.createEntityResource("Customer", createMockEntity("Customer"), GenericError.class);

		GenericEntity<EntityResource<Entity>> ge = er.getGenericEntity();
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class));
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class, Entity.class));
	}
	
	private Entity createMockEntity(String entityName) {
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("name", "Fred"));
		return new Entity(entityName, customerFields);
	}
}
