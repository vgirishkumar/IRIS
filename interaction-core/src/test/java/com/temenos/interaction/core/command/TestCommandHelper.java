package com.temenos.interaction.core.command;

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
