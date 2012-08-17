package com.temenos.interaction.core.media.atomsvc;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.UriInfo;

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

import com.temenos.interaction.core.ExtendedMediaTypes;
import com.temenos.interaction.core.resource.EntityResource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestServiceDocumentProvider {
	public class MockServiceDocumentProvider extends ServiceDocumentProvider {
		public void setUriInfo(UriInfo uriInfo) {
			super.setUriInfo(uriInfo);
		}
	};
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWriteServiceDocumentResource() throws Exception {
		EntityResource<EdmDataServices> mr = mock(EntityResource.class);
		
		EdmDataServices mockEDS = createMockFlightEdmDataServices();

		//Mock ServiceDocumentResource
		when(mr.getEntity()).thenReturn(mockEDS);
		
		//Serialize service document resource
		MockServiceDocumentProvider p = new MockServiceDocumentProvider();
		UriInfo uriInfo = mock(UriInfo.class);
		URI uri = new URI("http://localhost:8080/responder/rest");
		when(uriInfo.getBaseUri()).thenReturn(uri);
		p.setUriInfo(uriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(mr, EntityResource.class, EdmDataServices.class, null, ExtendedMediaTypes.APPLICATION_ATOMSVC_XML_TYPE, null, bos);

		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><service xmlns=\"http://www.w3.org/2007/app\" xml:base=\"http://localhost:8080/responder/rest\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:app=\"http://www.w3.org/2007/app\"><workspace><atom:title>Default</atom:title><collection href=\"Flight\"><atom:title>Flight</atom:title></collection></workspace></service>";
		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(expectedXML, responseString);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWriteServiceDocumentResourceGenericEntity() throws Exception {
		EntityResource<EdmDataServices> mr = mock(EntityResource.class);
		
		EdmDataServices mockEDS = createMockFlightEdmDataServices();

		//Mock ServiceDocumentResource
		when(mr.getEntity()).thenReturn(mockEDS);
		
        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<EntityResource<EdmDataServices>> ge = new GenericEntity<EntityResource<EdmDataServices>>(mr) {};
		
		//Serialize service document resource
		MockServiceDocumentProvider p = new MockServiceDocumentProvider();
		UriInfo uriInfo = mock(UriInfo.class);
		URI uri = new URI("http://localhost:8080/responder/rest");
		when(uriInfo.getBaseUri()).thenReturn(uri);
		p.setUriInfo(uriInfo);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(),ge.getType(), null, ExtendedMediaTypes.APPLICATION_ATOMSVC_XML_TYPE, null, bos);

		String expectedXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><service xmlns=\"http://www.w3.org/2007/app\" xml:base=\"http://localhost:8080/responder/rest\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:app=\"http://www.w3.org/2007/app\"><workspace><atom:title>Default</atom:title><collection href=\"Flight\"><atom:title>Flight</atom:title></collection></workspace></service>";
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

		List<EdmEntitySet> eesList = new ArrayList<EdmEntitySet>();
		eesList.add(ees.build());
		when(mockEDS.getEntitySets()).thenReturn(eesList);

		return mockEDS;
	}
}
