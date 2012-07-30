package com.temenos.interaction.commands.odata;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.wink.common.internal.MultivaluedMapImpl;
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
import org.odata4j.producer.EntityQueryInfo;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.resource.EntityResource;

public class TestGETLinkEntityCommand {

	class MyEdmType extends EdmType {
		public MyEdmType(String name) {
			super(name);
		}
		public boolean isSimple() { return false; }
	}

	private ODataProducer createMockODataProducer(String entityName, String linkProperty, String linkEntityName) {
		ODataProducer mockProducer = mock(ODataProducer.class);

		//Add entity
		List<String> keys = new ArrayList<String>();
		keys.add("id");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		properties.add(EdmProperty.newBuilder("id").setType(new MyEdmType("Edm.String")));
		properties.add(EdmProperty.newBuilder(linkProperty).setType(new MyEdmType("Edm.String")));
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName(entityName).addKeys(keys).addProperties(properties);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName(entityName).setEntityType(eet);

		//Add link entity
		keys = new ArrayList<String>();
		keys.add("id");
		properties = new ArrayList<EdmProperty.Builder>();
		properties.add(EdmProperty.newBuilder("id").setType(new MyEdmType("Edm.String")));
		EdmEntityType.Builder linkEet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName(linkEntityName).addKeys(keys).addProperties(properties);
		EdmEntitySet.Builder linkEes = EdmEntitySet.newBuilder().setName(linkEntityName).setEntityType(linkEet);
		
		List<EdmEntityType> mockEntityTypes = new ArrayList<EdmEntityType>();
		mockEntityTypes.add(eet.build());
		mockEntityTypes.add(linkEet.build());

		EdmDataServices mockEDS = mock(EdmDataServices.class);
		when(mockEDS.getEdmEntitySet(entityName)).thenReturn(ees.build());
		when(mockEDS.getEdmEntitySet(linkEntityName)).thenReturn(linkEes.build());
		when(mockEDS.getEntityTypes()).thenReturn(mockEntityTypes);
		when(mockProducer.getMetadata()).thenReturn(mockEDS);
		
        return mockProducer;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetLinkEntity() {
		ODataProducer mockProducer = createMockODataProducer("Customer", "addressId", "Address");

		//Create an OEntity
		OEntityKey entityKey = OEntityKey.create("CUSTOMER_FRED");
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("id", "CUSTOMER_FRED"));
		properties.add(OProperties.string("addressId", "ADDRESS_001"));
		OEntity customer = OEntities.create(mockProducer.getMetadata().getEdmEntitySet("Customer"), entityKey, properties, new ArrayList<OLink>());
		EntityResponse er = mock(EntityResponse.class);
		when(er.getEntity()).thenReturn(customer);
		when(mockProducer.getEntity(eq("Customer"), any(OEntityKey.class), any(EntityQueryInfo.class))).thenReturn(er);

		//Create an OEntity
		entityKey = OEntityKey.create("ADDRESS_001");
		properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("id", "ADDRESS_001"));
		OEntity address = OEntities.create(mockProducer.getMetadata().getEdmEntitySet("Address"), entityKey, properties, new ArrayList<OLink>());
		mockProducer.createEntity("Address", address);
		EntityResponse ler = mock(EntityResponse.class);
		when(ler.getEntity()).thenReturn(address);
		when(mockProducer.getEntity(eq("Address"), any(OEntityKey.class), any(EntityQueryInfo.class))).thenReturn(ler);
		
		//Get link entity
		GETLinkEntityCommand command = new GETLinkEntityCommand("Customer", "addressId", "Address", mockProducer);
		RESTResponse response = command.get("id", new MultivaluedMapImpl<String, String>());
		
		//Check result
		assertEquals(Response.Status.OK, response.getStatus());
		EntityResource<OEntity> entityResource = (EntityResource<OEntity>) response.getResource();
		OEntity entity = entityResource.getEntity();
		OProperty<String> propLinkEntityId = (OProperty<String>) entity.getProperty("id");
		assertEquals("ADDRESS_001", propLinkEntityId.getValue());
	}
	
	@Test
	public void testInvalidLinkEntity() {
		ODataProducer mockProducer = createMockODataProducer("Customer", "addressId", "Address");
		GETLinkEntityCommand command = new GETLinkEntityCommand("Customer", "addressId", "Address", mockProducer);
		RESTResponse response = command.get("id", new MultivaluedMapImpl<String, String>());
		assertEquals(Response.Status.NOT_ACCEPTABLE, response.getStatus());
	}
}
