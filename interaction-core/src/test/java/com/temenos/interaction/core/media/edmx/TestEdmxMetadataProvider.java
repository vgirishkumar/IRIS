package com.temenos.interaction.core.media.edmx;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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

import com.temenos.interaction.core.resource.MetaDataResource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEdmxMetadataProvider {

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
		p.writeTo(mr, MetaDataResource.class, null, null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><edmx:DataServices m:DataServiceVersion=\"1.0\"><Schema xmlns=\"http://schemas.microsoft.com/ado/2006/04/edm\" Namespace=\"MyNamespace\"><EntityType Name=\"Flight\"><Key><PropertyRef Name=\"MyId\"></PropertyRef></Key><Property Name=\"MyId\" Type=\"Edm.String\" Nullable=\"false\"></Property></EntityType><EntityContainer Name=\"MyEntityContainer\" m:IsDefaultEntityContainer=\"false\"><EntitySet Name=\"Flight\" EntityType=\"MyNamespace.Flight\"></EntitySet></EntityContainer></Schema></edmx:DataServices></edmx:Edmx>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}

	private EdmDataServices createMockFlightEdmDataServices() {
		EdmDataServices mockEDS = mock(EdmDataServices.class);

		//Mock EdmDataServices
		List<String> keys = new ArrayList<String>();
		keys.add("MyId");
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("MyId").setType(EdmSimpleType.STRING);
		properties.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").setName("Flight").addKeys(keys).addProperties(properties);
		EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Flight").setEntityType(eet);
		List<EdmEntityType.Builder> mockEntityTypes = new ArrayList<EdmEntityType.Builder>();
		mockEntityTypes.add(eet);
		List<EdmEntitySet.Builder> mockEntitySets = new ArrayList<EdmEntitySet.Builder>();
		mockEntitySets.add(ees);
		EdmEntityContainer.Builder eec = EdmEntityContainer.newBuilder().setName("MyEntityContainer").addEntitySets(mockEntitySets);
		List<EdmEntityContainer.Builder> mockEntityContainers = new ArrayList<EdmEntityContainer.Builder>();
		mockEntityContainers.add(eec);
		EdmSchema.Builder es = EdmSchema.newBuilder().setNamespace("MyNamespace").setAlias("MyAlias").addEntityTypes(mockEntityTypes).addEntityContainers(mockEntityContainers);
		List<EdmSchema> mockSchemas = new ArrayList<EdmSchema>();
		mockSchemas.add(es.build());
		when(mockEDS.getSchemas()).thenReturn(ImmutableList.copyOf(mockSchemas));

		return mockEDS;
	}
}
