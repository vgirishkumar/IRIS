package com.temenos.interaction.core.media.edmx;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.odata4j.core.ImmutableList;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmAssociationEnd;
import org.odata4j.edm.EdmAssociationSet;
import org.odata4j.edm.EdmAssociationSetEnd;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmMultiplicity;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;

import com.temenos.interaction.core.resource.MetaDataResource;

public class TestEdmxMetaDataWriter {

	public final static String NAMESPACE = "MyNamespace";
	
	public final static String EXPECTED_FLIGHT_EDMX = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
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

	
	@SuppressWarnings("unchecked")
	@Test
	public void testWriteMetadataResource() throws Exception {
		MetaDataResource<EdmDataServices> mr = mock(MetaDataResource.class);
		
		EdmDataServices mockEDS = createMockFlightEdmDataServices();

		//Mock MetadataResource
		when(mr.getMetadata()).thenReturn(mockEDS);
		
		//Serialize metadata resource
		EdmxMetaDataProvider p = new EdmxMetaDataProvider();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(mr, MetaDataResource.class, EdmDataServices.class, null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(EXPECTED_FLIGHT_EDMX, responseString);
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
		EdmxMetaDataProvider p = new EdmxMetaDataProvider();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(EXPECTED_FLIGHT_EDMX, responseString);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testManyToManyNavProperty() throws Exception {
		MetaDataResource<EdmDataServices> mr = mock(MetaDataResource.class);
		
		EdmDataServices mockEDS = createMockAirportFlightsEdmDataServices();

		//Mock MetadataResource
		when(mr.getMetadata()).thenReturn(mockEDS);
		
		//Serialize metadata resource
		EdmxMetaDataProvider p = new EdmxMetaDataProvider();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(mr, MetaDataResource.class, EdmDataServices.class, null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
				"<edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">" +
				"<edmx:DataServices m:DataServiceVersion=\"1.0\">" +
				"<Schema xmlns=\"http://schemas.microsoft.com/ado/2006/04/edm\" Namespace=\"MyNamespace\">" +
				"<EntityType Name=\"Airport\">" +
				"<Key><PropertyRef Name=\"MyId\"></PropertyRef></Key><Property Name=\"MyId\" Type=\"Edm.String\" Nullable=\"false\"></Property>" +
				"<NavigationProperty Name=\"Flights\" Relationship=\"MyNamespace.Airport_Flights\" FromRole=\"Airport_Flights_Source\" ToRole=\"Airport_Flights_Target\"></NavigationProperty>" +
				"</EntityType>" +
				"<EntityType Name=\"Flight\"><Key><PropertyRef Name=\"MyId\"></PropertyRef></Key><Property Name=\"MyId\" Type=\"Edm.String\" Nullable=\"false\"></Property></EntityType>" +
				"<Association Name=\"Airport_Flights\"><End Role=\"Airport_Flights_Source\" Type=\"MyNamespace.Airport\" Multiplicity=\"*\"></End><End Role=\"Airport_Flights_Target\" Type=\"MyNamespace.Flight\" Multiplicity=\"*\"></End></Association>" +
				"<EntityContainer Name=\"MyEntityContainer\" m:IsDefaultEntityContainer=\"true\">" +
				"<EntitySet Name=\"Airports\" EntityType=\"MyNamespace.Airport\"></EntitySet>" +
				"<EntitySet Name=\"Flights\" EntityType=\"MyNamespace.Flight\"></EntitySet>" +
				"<AssociationSet Name=\"Airport_Flights\" Association=\"MyNamespace.Airport_Flights\">" +
				"<End Role=\"Airport_Flights_Source\" EntitySet=\"Airports\"></End>" +
				"<End Role=\"Airport_Flights_Target\" EntitySet=\"Flights\"></End>" +
				"</AssociationSet>"+
				"</EntityContainer>" +
				"</Schema>" +
				"</edmx:DataServices>" +
				"</edmx:Edmx>";
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
		EdmEntityType.Builder flightEntityType = EdmEntityType.newBuilder().setNamespace(NAMESPACE).setAlias("MyAlias").setName("Flight").addKeys(keys).addProperties(properties);
		List<EdmEntityType.Builder> entityTypeList = new ArrayList<EdmEntityType.Builder>();
		entityTypeList.add(flightEntityType);
		
		// Entity Sets
		EdmEntitySet.Builder eesFlights = EdmEntitySet.newBuilder().setName("Flights").setEntityType(flightEntityType);
		List<EdmEntitySet.Builder> mockEntitySets = new ArrayList<EdmEntitySet.Builder>();
		mockEntitySets.add(eesFlights);
		
		// Container
		EdmEntityContainer.Builder eec = EdmEntityContainer.newBuilder().setName("MyEntityContainer").setIsDefault(true)
				.addEntitySets(mockEntitySets);
		List<EdmEntityContainer.Builder> mockEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
		mockEntityContainers.add(eec);
		
		// Schema
		EdmSchema.Builder es = EdmSchema.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").addEntityTypes(entityTypeList).addEntityContainers(mockEntityContainers);
		List<EdmSchema> mockSchemas = new ArrayList<EdmSchema>();
		mockSchemas.add(es.build());
		when(mockEDS.getSchemas()).thenReturn(ImmutableList.copyOf(mockSchemas));

		return mockEDS;
	}

	private EdmDataServices createMockAirportFlightsEdmDataServices() {
		EdmDataServices mockEDS = mock(EdmDataServices.class);

		//Mock EdmDataServices
		
		// Entity Types
		List<String> keys = new ArrayList<String>();
		keys.add("MyId");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("MyId").setType(EdmSimpleType.STRING).setNullable(false);
		properties.add(ep);
		EdmEntityType.Builder airportEntityType = EdmEntityType.newBuilder()
				.setNamespace(NAMESPACE)
				.setAlias("MyAlias")
				.setName("Airport")
				.addKeys(keys)
				.addProperties(properties);
		EdmEntityType.Builder flightEntityType = EdmEntityType.newBuilder().setNamespace(NAMESPACE).setAlias("MyAlias").setName("Flight").addKeys(keys).addProperties(properties);
		List<EdmEntityType.Builder> entityTypeList = new ArrayList<EdmEntityType.Builder>();
		entityTypeList.add(airportEntityType);
		entityTypeList.add(flightEntityType);
		
		// Associations
		EdmAssociationEnd.Builder aEnd = EdmAssociationEnd.newBuilder()
				.setRole("Airport_Flights_Source")
				.setType(airportEntityType)
				.setMultiplicity(EdmMultiplicity.MANY);
		EdmAssociationEnd.Builder fend = EdmAssociationEnd.newBuilder()
				.setRole("Airport_Flights_Target")
				.setType(flightEntityType)
				.setMultiplicity(EdmMultiplicity.MANY);
		EdmAssociation.Builder afAssociation = EdmAssociation.newBuilder()
				.setNamespace(NAMESPACE)
				.setName("Airport_Flights")
				.setEnds(aEnd, fend);
		List<EdmAssociation.Builder> mockAssociations = new ArrayList<EdmAssociation.Builder>();
		mockAssociations.add(afAssociation);
		
		// Navigation Properties
		airportEntityType.addNavigationProperties(EdmNavigationProperty
				.newBuilder("Flights")
				.setRelationship(afAssociation)
				.setFromTo(aEnd, fend));
		
		// Entity Sets
		EdmEntitySet.Builder eesAirports = EdmEntitySet.newBuilder().setName("Airports").setEntityType(airportEntityType);
		EdmEntitySet.Builder eesAirportFlights = EdmEntitySet.newBuilder().setName("Flights").setEntityType(flightEntityType);
		List<EdmEntitySet.Builder> mockEntitySets = new ArrayList<EdmEntitySet.Builder>();
		mockEntitySets.add(eesAirports);
		mockEntitySets.add(eesAirportFlights);
		
		// AssociationSet
		EdmAssociationSetEnd.Builder asEnd = EdmAssociationSetEnd.newBuilder().setRole(aEnd).setEntitySet(eesAirports);
		EdmAssociationSetEnd.Builder fsEnd = EdmAssociationSetEnd.newBuilder().setRole(fend).setEntitySet(eesAirportFlights);
		EdmAssociationSet.Builder afAssociationSet = EdmAssociationSet.newBuilder()
				.setName("Airport_Flights")
				.setAssociation(afAssociation)
				.setEnds(asEnd, fsEnd);
		List<EdmAssociationSet.Builder> mockAssociationSets = new ArrayList<EdmAssociationSet.Builder>();
		mockAssociationSets.add(afAssociationSet);
		
		// Container
		EdmEntityContainer.Builder eec = EdmEntityContainer.newBuilder().setName("MyEntityContainer").setIsDefault(true)
				.addEntitySets(mockEntitySets)
				.addAssociationSets(mockAssociationSets);
		List<EdmEntityContainer.Builder> mockEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
		mockEntityContainers.add(eec);
		
		// Schema
		EdmSchema.Builder es = EdmSchema.newBuilder().setNamespace(NAMESPACE).setAlias("MyAlias")
				.addEntityTypes(entityTypeList)
				.addAssociations(mockAssociations)
				.addEntityContainers(mockEntityContainers);
		List<EdmSchema> mockSchemas = new ArrayList<EdmSchema>();
		mockSchemas.add(es.build());
		when(mockEDS.getSchemas()).thenReturn(ImmutableList.copyOf(mockSchemas));

		return mockEDS;
	}

}
