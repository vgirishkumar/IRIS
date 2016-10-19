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


import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

import javax.ws.rs.core.GenericEntity;

import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.RESTResource;
import org.junit.Before;
import org.junit.Test;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.GenericError;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;

public class TestCommandHelper {

	private static final String ENTITY_TAG = "ABCDE";

	private Map<Transition, RESTResource> embedded;
	private Collection<Link> links;

	@Before
	public void setup() {
		embedded = new HashMap<>();
		links = new ArrayList<>();
	}

	@Test
	public void testCreateEntityResource() {
		EntityResource<GenericError> er = CommandHelper.createEntityResource(new GenericError("123", "My error message"), GenericError.class);

		GenericEntity<EntityResource<GenericError>> ge = er.getGenericEntity();
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class));
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class, GenericError.class));
	}
	
	@Test
	public void testCreateEntityResourceWithEntityName() {
		EntityResource<Entity> er = CommandHelper.createEntityResource("Customer", createMockEntity("Customer"), Entity.class);

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
	
	@Test
	public void testCreateEntityResourceWithoutExplicitEntityName() {
		EntityResource<Entity> er = CommandHelper.createEntityResource(createMockEntity("Customer"));

		GenericEntity<EntityResource<Entity>> ge = er.getGenericEntity();
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class));
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class, Entity.class));
		assertEquals("Customer", ge.getEntity().getEntityName());
		assertEquals("Customer", ge.getEntity().getEntity().getName());
	}

	@Test
	public void testCreateEntityResourceFromEntityResource() {
		EntityResource<Entity> er = CommandHelper.createEntityResource(createMockEntity("Customer"));
		er.setEmbedded(embedded);
		er.setLinks(links);
		er.setEntityTag(ENTITY_TAG);

		EntityResource<Entity> erCopy = CommandHelper.createEntityResource(er);

		assertNotSame(er, erCopy);
		assertEquals(er.getEmbedded(), erCopy.getEmbedded());
		assertEquals(er.getLinks(), erCopy.getLinks());
		assertEquals(er.getEntityTag(), erCopy.getEntityTag());
		assertEquals(embedded, erCopy.getEmbedded());
		assertEquals(links, erCopy.getLinks());
		assertEquals(ENTITY_TAG, erCopy.getEntityTag());
	}

	@Test
	public void testCreateEntityResourceFromNullEntityResource() {
		assertNull(CommandHelper.createEntityResource((EntityResource<?>) null));
	}

	@SuppressWarnings("unchecked")
	@Test
	public<E> void testGetEffectiveGenericTypeVariable() {
		Entity entity = createMockEntity("MyEntity");
		GenericEntity<E> ge = new GenericEntity<E>((E)entity) {};
		Type t = CommandHelper.getEffectiveGenericType(ge.getType(), entity);
		
		assertTrue(t instanceof TypeVariable);
		TypeVariable<?> tv = (TypeVariable<?>) t;
		assertEquals("Entity", tv.getName());
	}
	
	private Entity createMockEntity(String entityName) {
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("name", "Fred"));
		return new Entity(entityName, customerFields);
	}
}
