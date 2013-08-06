package com.temenos.interaction.media.odata.xml.error;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;

import com.temenos.interaction.core.entity.GenericError;
import com.temenos.interaction.core.resource.EntityResource;

public class TestErrorWriter {

	public final static String NAMESPACE = "MyNamespace";
	
	public final static String EXPECTED_ODATA_TOP_LEVEL_ERROR = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
			"<error xmlns=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">" +
			"<code>UPSTREAM_SERVER_UNAVAILABLE</code>" +
			"<message xml:lang=\"en-US\">Failed to connect to resource manager.</message>" +
			"</error>";

	@Test
	public void testWriteGenericError() throws Exception {
		EntityResource<GenericError> mockErrorResource = createMockEntityResourceGenericError();

		ErrorProvider p = new ErrorProvider();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(mockErrorResource, EntityResource.class, GenericError.class, null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(EXPECTED_ODATA_TOP_LEVEL_ERROR, responseString);
	}

	@Test
	public void testWriteErrorWithGenericEntity() throws Exception {
		EntityResource<GenericError> mockErrorResource = createMockEntityResourceGenericError();

        //Wrap entity resource into a JAX-RS GenericEntity instance
		GenericEntity<EntityResource<GenericError>> ge = new GenericEntity<EntityResource<GenericError>>(mockErrorResource) {};
		
		ErrorProvider p = new ErrorProvider();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(ge.getEntity(), ge.getRawType(), ge.getType(), null, MediaType.APPLICATION_XML_TYPE, null, bos);

		String responseString = new String(bos.toByteArray(), "UTF-8");
		XMLAssert.assertXMLEqual(EXPECTED_ODATA_TOP_LEVEL_ERROR, responseString);
	}

	@SuppressWarnings("unchecked")
	private EntityResource<GenericError> createMockEntityResourceGenericError() {
		EntityResource<GenericError> er = mock(EntityResource.class);
				
		GenericError error = new GenericError("UPSTREAM_SERVER_UNAVAILABLE", "Failed to connect to resource manager.");
		when(er.getEntity()).thenReturn(error);
		when(er.getEntityName()).thenReturn("Flight");
		return er;
	}	
}
