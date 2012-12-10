package com.temenos.interaction.core.media.edmx;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.odata4j.core.ImmutableList;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.resource.MetaDataResource;

public class TestEdmxMetaDataWriter {

	@SuppressWarnings("unchecked")
	@Test
	public void testWriteMetadataResource() throws Exception {
		MetaDataResource<EdmDataServices> mr = mock(MetaDataResource.class);
		
		EdmDataServices mockEDS = createMockFlightEdmDataServices();

		//Mock MetadataResource
		when(mr.getMetadata()).thenReturn(mockEDS);
		
		//Serialize metadata resource
		EdmxMetaDataProvider p = new EdmxMetaDataProvider(mock(ResourceStateMachine.class));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(mr, MetaDataResource.class, EdmDataServices.class, null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"1.0\"><Schema xmlns=\"http://schemas.microsoft.com/ado/2006/04/edm\" Namespace=\"MyNamespace\"><EntityType Name=\"Flight\"><Key><PropertyRef Name=\"MyId\"></PropertyRef></Key><Property Name=\"MyId\" Type=\"Edm.String\" Nullable=\"false\"></Property></EntityType><EntityContainer Name=\"MyEntityContainer\" m:IsDefaultEntityContainer=\"true\"><EntitySet Name=\"Flights\" EntityType=\"MyNamespace.Flight\"></EntitySet></EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWriteMetadataResourceGenericEntity() throws Exception {
		MetaDataResource<EdmDataServices> mr = mock(MetaDataResource.class);
		
		EdmDataServices mockEDS = createMockFlightEdmDataServices();

		//Mock MetadataResource
		when(mr.getMetadata()).thenReturn(mockEDS);

        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<MetaDataResource<EdmDataServices>> ge = new GenericEntity<MetaDataResource<EdmDataServices>>(mr) {};
		
		//Serialize metadata resource
		EdmxMetaDataProvider p = new EdmxMetaDataProvider(mock(ResourceStateMachine.class));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
				"<edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">" +
				"<edmx:DataServices m:DataServiceVersion=\"1.0\">" +
				"<Schema xmlns=\"http://schemas.microsoft.com/ado/2006/04/edm\" Namespace=\"MyNamespace\">" +
				"<EntityType Name=\"Flight\">" +
				"<Key><PropertyRef Name=\"MyId\"></PropertyRef></Key>" +
				"<Property Name=\"MyId\" Type=\"Edm.String\" Nullable=\"false\"></Property>" +
				"</EntityType>" +
				"<EntityContainer Name=\"MyEntityContainer\" m:IsDefaultEntityContainer=\"true\">" +
				"<EntitySet Name=\"Flights\" EntityType=\"MyNamespace.Flight\"></EntitySet>" +
				"</EntityContainer>" +
				"</Schema>" +
				"</edmx:DataServices>" +
				"</edmx:Edmx>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWriteMetadataResourceNavProperties() throws Exception {
		MetaDataResource<EdmDataServices> mr = mock(MetaDataResource.class);
		
		EdmDataServices mockEDS = createMockFlightEdmDataServices();

		//Mock MetadataResource
		when(mr.getMetadata()).thenReturn(mockEDS);
		// mock resource interaction (which creates Navigation Property in metadata)
		ResourceState flight = new ResourceState("Flight", "Flights", new ArrayList<Action>(), "/Flights");
		ResourceState airport = new ResourceState("Airport", "Airports", new ArrayList<Action>(), "/Airports");
		flight.addTransition(airport);
		ResourceStateMachine rsm = new ResourceStateMachine(flight);
		
		//Serialize metadata resource
		EdmxMetaDataProvider p = new EdmxMetaDataProvider(rsm);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(mr, MetaDataResource.class, EdmDataServices.class, null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"1.0\"><Schema xmlns=\"http://schemas.microsoft.com/ado/2006/04/edm\" Namespace=\"MyNamespace\">" 
				+ "<EntityType Name=\"Flight\"><Key><PropertyRef Name=\"MyId\"></PropertyRef></Key><Property Name=\"MyId\" Type=\"Edm.String\" Nullable=\"false\"></Property>" 
				+ "<NavigationProperty Name=\"Airports\" Relationship=\"MyNamespace.Flights_Airports\" FromRole=\"Flight_Source\" ToRole=\"Airport_Target\"></NavigationProperty>"
				+ "</EntityType>"
				+ "<Association Name=\"Flights_Airports\"><End Role=\"Flight_Source\" Type=\"MyNamespace.Flight\" Multiplicity=\"*\"></End><End Role=\"Airport_Target\" Type=\"MyNamespace.Airport\" Multiplicity=\"0..1\"></End></Association>"
				+ "<EntityContainer Name=\"MyEntityContainer\" m:IsDefaultEntityContainer=\"true\">"
				+ "<EntitySet Name=\"Flights\" EntityType=\"MyNamespace.Flight\"></EntitySet>"
				+ "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWriteMetadataResourceAssociationSets() throws Exception {
		MetaDataResource<EdmDataServices> mr = mock(MetaDataResource.class);
		
		EdmDataServices mockEDS = createMockFlightEdmDataServices();

		//Mock MetadataResource
		when(mr.getMetadata()).thenReturn(mockEDS);
		// mock resource interaction (which creates Navigation Property in metadata)
		CollectionResourceState flight = new CollectionResourceState("Flight", "Flights", new ArrayList<Action>(), "/Flights");
		CollectionResourceState airport = new CollectionResourceState("Airport", "Airports", new ArrayList<Action>(), "/Airports");
		flight.addTransitionForEachItem("GET", airport, new HashMap<String, String>());
		ResourceStateMachine rsm = new ResourceStateMachine(flight);
		
		//Serialize metadata resource
		EdmxMetaDataProvider p = new EdmxMetaDataProvider(rsm);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(mr, MetaDataResource.class, EdmDataServices.class, null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"1.0\"><Schema xmlns=\"http://schemas.microsoft.com/ado/2006/04/edm\" Namespace=\"MyNamespace\">" 
				+ "<EntityType Name=\"Flight\"><Key><PropertyRef Name=\"MyId\"></PropertyRef></Key><Property Name=\"MyId\" Type=\"Edm.String\" Nullable=\"false\"></Property>" 
				+ "<NavigationProperty Name=\"Airports\" Relationship=\"MyNamespace.Flights_Airports\" FromRole=\"Flight_Source\" ToRole=\"Airport_Target\"></NavigationProperty>"
				+ "</EntityType>"
				+ "<Association Name=\"Flights_Airports\"><End Role=\"Flight_Source\" Type=\"MyNamespace.Flight\" Multiplicity=\"*\"></End><End Role=\"Airport_Target\" Type=\"MyNamespace.Airport\" Multiplicity=\"*\"></End></Association>"
				+ "<EntityContainer Name=\"MyEntityContainer\" m:IsDefaultEntityContainer=\"true\">"
				+ "<EntitySet Name=\"Flights\" EntityType=\"MyNamespace.Flight\"></EntitySet>"
				+ "<AssociationSet Name=\"Flights_Airports\" Association=\"MyNamespace.Flights_Airports\"><End Role=\"Flight_Source\" EntitySet=\"Flights\"></End><End Role=\"Airport_Target\" EntitySet=\"Airports\"></End></AssociationSet>"
				+ "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testManyToManyNavProperty() throws Exception {
		MetaDataResource<EdmDataServices> mr = mock(MetaDataResource.class);
		
		EdmDataServices mockEDS = createMockFlightAirportsEdmDataServices();

		//Mock MetadataResource
		when(mr.getMetadata()).thenReturn(mockEDS);
		// mock resource interaction (which creates Navigation Property in metadata)
		ResourceState initial = new ResourceState("ROOT", "initial", new ArrayList<Action>(), "/");
		CollectionResourceState flights = new CollectionResourceState("Flight", "Flights", new ArrayList<Action>(), "/Flights");
		ResourceState flight = new ResourceState("Flight", "flight", new ArrayList<Action>(), "/Flights({id})");
		CollectionResourceState airports = new CollectionResourceState("Airport", "FlightAirports", new ArrayList<Action>(), "/Flights({id})/Airports");
		initial.addTransition(flights);
		flight.addTransition("GET", airports, new HashMap<String, String>());
		flights.addTransitionForEachItem("GET", airports, new HashMap<String, String>());
		ResourceStateMachine rsm = new ResourceStateMachine(flights);
		
		//Serialize metadata resource
		EdmxMetaDataProvider p = new EdmxMetaDataProvider(rsm);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(mr, MetaDataResource.class, EdmDataServices.class, null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"1.0\"><Schema xmlns=\"http://schemas.microsoft.com/ado/2006/04/edm\" Namespace=\"MyNamespace\">" 
				+ "<EntityType Name=\"Flight\"><Key><PropertyRef Name=\"MyId\"></PropertyRef></Key><Property Name=\"MyId\" Type=\"Edm.String\" Nullable=\"false\"></Property>" 
				+ "<NavigationProperty Name=\"FlightAirports\" Relationship=\"MyNamespace.Flights_FlightAirports\" FromRole=\"Flight_Source\" ToRole=\"Airport_Target\"></NavigationProperty>"
				+ "</EntityType>"
				+ "<Association Name=\"Flights_FlightAirports\"><End Role=\"Flight_Source\" Type=\"MyNamespace.Flight\" Multiplicity=\"*\"></End><End Role=\"Airport_Target\" Type=\"MyNamespace.Airport\" Multiplicity=\"*\"></End></Association>"
				+ "<EntityContainer Name=\"MyEntityContainer\" m:IsDefaultEntityContainer=\"true\">"
				+ "<EntitySet Name=\"Flights\" EntityType=\"MyNamespace.Flight\"></EntitySet>"
				+ "<EntitySet Name=\"FlightAirports\" EntityType=\"MyNamespace.Flight\"></EntitySet>"
				+ "<AssociationSet Name=\"Flights_FlightAirports\" Association=\"MyNamespace.Flights_FlightAirports\"><End Role=\"Flight_Source\" EntitySet=\"Flights\"></End><End Role=\"Airport_Target\" EntitySet=\"FlightAirports\"></End></AssociationSet>"
				+ "</EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}

	private EdmDataServices createMockFlightEdmDataServices() {
		EdmDataServices mockEDS = mock(EdmDataServices.class);

		//Mock EdmDataServices
		List<String> keys = new ArrayList<String>();
		keys.add("MyId");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("MyId").setType(EdmSimpleType.STRING).setNullable(false);
		properties.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName("Flight").addKeys(keys).addProperties(properties);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Flights").setEntityType(eet);
		List<EdmEntityType.Builder> mockEntityTypes = new ArrayList<EdmEntityType.Builder>();
		mockEntityTypes.add(eet);
		List<EdmEntitySet.Builder> mockEntitySets = new ArrayList<EdmEntitySet.Builder>();
		mockEntitySets.add(ees);
		EdmEntityContainer.Builder eec = EdmEntityContainer.newBuilder().setName("MyEntityContainer").setIsDefault(true).addEntitySets(mockEntitySets);
		List<EdmEntityContainer.Builder> mockEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
		mockEntityContainers.add(eec);
		EdmSchema.Builder es = EdmSchema.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").addEntityTypes(mockEntityTypes).addEntityContainers(mockEntityContainers);
		List<EdmSchema> mockSchemas = new ArrayList<EdmSchema>();
		mockSchemas.add(es.build());
		when(mockEDS.getSchemas()).thenReturn(ImmutableList.copyOf(mockSchemas));

		return mockEDS;
	}

	private EdmDataServices createMockFlightAirportsEdmDataServices() {
		EdmDataServices mockEDS = mock(EdmDataServices.class);

		//Mock EdmDataServices
		List<String> keys = new ArrayList<String>();
		keys.add("MyId");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("MyId").setType(EdmSimpleType.STRING).setNullable(false);
		properties.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName("Flight").addKeys(keys).addProperties(properties);
		EdmEntitySet.Builder eesFlights = EdmEntitySet.newBuilder().setName("Flights").setEntityType(eet);
		EdmEntitySet.Builder eesFlightAirports = EdmEntitySet.newBuilder().setName("FlightAirports").setEntityType(eet);
		List<EdmEntityType.Builder> mockEntityTypes = new ArrayList<EdmEntityType.Builder>();
		mockEntityTypes.add(eet);
		List<EdmEntitySet.Builder> mockEntitySets = new ArrayList<EdmEntitySet.Builder>();
		mockEntitySets.add(eesFlights);
		mockEntitySets.add(eesFlightAirports);
		EdmEntityContainer.Builder eec = EdmEntityContainer.newBuilder().setName("MyEntityContainer").setIsDefault(true).addEntitySets(mockEntitySets);
		List<EdmEntityContainer.Builder> mockEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
		mockEntityContainers.add(eec);
		EdmSchema.Builder es = EdmSchema.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").addEntityTypes(mockEntityTypes).addEntityContainers(mockEntityContainers);
		List<EdmSchema> mockSchemas = new ArrayList<EdmSchema>();
		mockSchemas.add(es.build());
		when(mockEDS.getSchemas()).thenReturn(ImmutableList.copyOf(mockSchemas));

		return mockEDS;
	}

}
