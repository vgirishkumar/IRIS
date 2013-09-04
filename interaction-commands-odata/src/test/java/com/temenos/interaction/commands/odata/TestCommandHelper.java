package com.temenos.interaction.commands.odata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmType;

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ActionPropertyReference;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.ResourceTypeHelper;

public class TestCommandHelper {

	class MyEdmType extends EdmType {
		public MyEdmType(String name) {
			super(name);
		}
		public boolean isSimple() { return false; }
	}

	@Test
	public void testEntityKeyString() {
		EdmDataServices mockEDS = createMockEdmDataServices("MyEntity", "Edm.String");
		try {
			OEntityKey key = CommandHelper.createEntityKey(mockEDS, "MyEntity", "MyId");
			assertEquals("SINGLE", key.getKeyType().toString());
			assertEquals("MyId", key.asSingleValue().toString());
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test(expected = Exception.class)
	public void testEntityKeyUnsupportedKeyType() throws Exception {
		EdmDataServices mockEDS = createMockEdmDataServices("MyEntity", null);
		try {
			CommandHelper.createEntityKey(mockEDS, "MyEntity", "MyId");
		}
		catch(AssertionError ae) {
			throw new Exception(ae.getMessage());
		}
	}

	@Test
	public void testGetViewActionProperty() {
		try {
			InteractionContext ctx = createInteractionContext("MyEntity", "123");
			String prop = CommandHelper.getViewActionProperty(ctx, "filter");
			assertEquals("customer eq '123'", prop);
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetViewActionPropertyWithQueryParams() {
		try {
			InteractionContext ctx = createInteractionContextWithQueryParams("MyEntity", "123");
			String prop = CommandHelper.getViewActionProperty(ctx, "filter");
			assertEquals("customer eq '123'", prop);
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetViewActionPropertyWithMultipleParams() {
		try {
			InteractionContext ctx = createInteractionContextWithMultipleParams("Airport", "departureAirportCode", "123");
			String prop = CommandHelper.getViewActionProperty(ctx, "filter");
			assertEquals("departureAirportCode eq '123'", prop);

			ctx = createInteractionContextWithMultipleParams("Airport", "arrivalAirportCode", "456");
			prop = CommandHelper.getViewActionProperty(ctx, "filter");
			assertEquals("arrivalAirportCode eq '456'", prop);
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetViewActionPropertyWithQueryParamsContainingSpaces() {
		try {
			InteractionContext ctx = createInteractionContextWithQueryParams("MyEntity", "123 456 789");
			String prop = CommandHelper.getViewActionProperty(ctx, "filter");
			assertEquals("customer eq '123 456 789'", prop);
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCreateOEntityResource() {
		OEntity entity = createMockOEntity(createMockEdmDataServices("MyEntity", "Edm.String").getEdmEntitySet("MyEntity"));
		EntityResource<OEntity> er = CommandHelper.createEntityResource(entity);

		GenericEntity<EntityResource<OEntity>> ge = er.getGenericEntity();
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class));
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class, OEntity.class));
	}

	@Test
	public void testCreateOEntityResourceWithExplicitType() {
		OEntity entity = createMockOEntity(createMockEdmDataServices("MyEntity", "Edm.String").getEdmEntitySet("MyEntity"));
		String entityName = entity != null && entity.getEntityType() != null ? entity.getEntityType().getName() : null;
		EntityResource<OEntity> er = com.temenos.interaction.core.command.CommandHelper.createEntityResource(entityName, entity, OEntity.class);

		GenericEntity<EntityResource<OEntity>> ge = er.getGenericEntity();
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class));
		assertTrue(ResourceTypeHelper.isType(ge.getRawType(), ge.getType(), EntityResource.class, OEntity.class));
	}
	
	private EdmDataServices createMockEdmDataServices(String entityName, String keyTypeName) {
		List<String> keys = new ArrayList<String>();
		keys.add("MyId");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("MyId").setType(new MyEdmType(keyTypeName));
		properties.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName(entityName).addKeys(keys).addProperties(properties);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName(entityName).setEntityType(eet);

		List<EdmEntityType> mockEntityTypes = new ArrayList<EdmEntityType>();
		mockEntityTypes.add(eet.build());

		EdmDataServices mockEDS = mock(EdmDataServices.class);
		when(mockEDS.getEdmEntitySet(anyString())).thenReturn(ees.build());
		when(mockEDS.getEntityTypes()).thenReturn(mockEntityTypes);
        return mockEDS;
	}

	private OEntity createMockOEntity(EdmEntitySet ees) {
		OEntityKey entityKey = OEntityKey.create("123");
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("MyId", "123"));
		return OEntities.create(ees, entityKey, properties, new ArrayList<OLink>());
	}	
	
	@SuppressWarnings("unchecked")
	private InteractionContext createInteractionContext(String entity, String id) {
		ResourceState resourceState = mock(ResourceState.class);
		when(resourceState.getEntityName()).thenReturn(entity);
		when(resourceState.getUriSpecification()).thenReturn(new ODataUriSpecification().getTemplate("/" + entity, ODataUriSpecification.NAVPROPERTY_URI_TYPE));
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("id", id);
		Properties properties = new Properties();
		properties.put("filter", "customer eq '{id}'");
		when(resourceState.getViewAction()).thenReturn(new Action("GETEntitiesCommand", Action.TYPE.VIEW, properties));
		
        InteractionContext ctx = new InteractionContext(pathParams, mock(MultivaluedMap.class), resourceState, mock(Metadata.class));
        return ctx;
	}

	private InteractionContext createInteractionContextWithQueryParams(String entity, String id) {
		ResourceState resourceState = mock(ResourceState.class);
		when(resourceState.getEntityName()).thenReturn(entity);
		when(resourceState.getUriSpecification()).thenReturn(new ODataUriSpecification().getTemplate("/" + entity, ODataUriSpecification.NAVPROPERTY_URI_TYPE));
		MultivaluedMap<String, String> pathParams = new MultivaluedMapImpl<String>();
		pathParams.add("id", id);
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add("code", id);
		Properties properties = new Properties();
		properties.put("filter", "customer eq '{code}'");
		when(resourceState.getViewAction()).thenReturn(new Action("GETEntitiesCommand", Action.TYPE.VIEW, properties));
		
        InteractionContext ctx = new InteractionContext(pathParams, queryParams, resourceState, mock(Metadata.class));
        return ctx;
	}

	private InteractionContext createInteractionContextWithMultipleParams(String entity, String queryParamKey, String queryParamValue) {
		ResourceState resourceState = mock(ResourceState.class);
		when(resourceState.getEntityName()).thenReturn(entity);
		when(resourceState.getUriSpecification()).thenReturn(new ODataUriSpecification().getTemplate("/" + entity, ODataUriSpecification.NAVPROPERTY_URI_TYPE));
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl<String>();
		queryParams.add(queryParamKey, queryParamValue);

		Properties actionViewProperties = new Properties();
		actionViewProperties.put("entity", "Airport");
		ActionPropertyReference propRef= new ActionPropertyReference("myfilter");
		propRef.addProperty("__arrivalAirportCode", "arrivalAirportCode eq '{arrivalAirportCode}'");
		propRef.addProperty("__departureAirportCode", "departureAirportCode eq '{departureAirportCode}'");
		actionViewProperties.put("filter", propRef);
		when(resourceState.getViewAction()).thenReturn(new Action("GetMyEntities", Action.TYPE.VIEW, actionViewProperties));
		
        InteractionContext ctx = new InteractionContext(new MultivaluedMapImpl<String>(), queryParams, resourceState, mock(Metadata.class));
        return ctx;
	}
}
