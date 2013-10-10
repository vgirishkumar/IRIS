package com.temenos.interaction.media.odata.xml.error;

/*
 * #%L
 * interaction-media-odata-xml
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
