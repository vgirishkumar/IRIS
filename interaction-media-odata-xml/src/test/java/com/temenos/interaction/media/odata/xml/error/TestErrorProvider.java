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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.io.ByteArrayOutputStream;
import java.io.Writer;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.entity.GenericError;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ErrorWriter.class})
public class TestErrorProvider {

	@Test
	public void testAcceptMetaDataResource() {
		ErrorProvider provider = new ErrorProvider();
		assertTrue(provider.isWriteable(EntityResource.class, GenericError.class, null, MediaType.APPLICATION_XML_TYPE));
	}

	@Test(expected = AssertionError.class)
	public void testDoNoAcceptCollectionResource() {
		ErrorProvider provider = new ErrorProvider();
		assertTrue(provider.isWriteable(CollectionResource.class, GenericError.class, null, MediaType.APPLICATION_XML_TYPE));
	}

	@Test
	public void testWrite() throws Exception {
		EntityResource<GenericError> mr = new EntityResource<GenericError>(mock(GenericError.class));
		ErrorProvider provider = new ErrorProvider();
		
		// make sure write does nothing
		mockStatic(ErrorWriter.class);
	
		MultivaluedMap<String, Object> httpHeaders = new MultivaluedMapImpl<Object>();
		provider.writeTo(mr, EntityResource.class, GenericError.class, null, MediaType.APPLICATION_XML_TYPE, httpHeaders, new ByteArrayOutputStream());
		
		// turn on static verification
		verifyStatic();
		// verify our method called correctly
		ErrorWriter.write(any(GenericError.class), any(Writer.class));
		//check the response headers
		assertEquals(MediaType.APPLICATION_XML, httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE));
	}

	@Test
	public void testWriteAcceptAtomXml() throws Exception {
		EntityResource<GenericError> mr = new EntityResource<GenericError>(mock(GenericError.class));
		ErrorProvider provider = new ErrorProvider();
		mockStatic(ErrorWriter.class);
		MultivaluedMap<String, Object> httpHeaders = new MultivaluedMapImpl<Object>();
		provider.writeTo(mr, EntityResource.class, GenericError.class, null, MediaType.APPLICATION_ATOM_XML_TYPE, httpHeaders, new ByteArrayOutputStream());

		//check the response headers
		assertEquals(MediaType.APPLICATION_XML, httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE));
	}
}
