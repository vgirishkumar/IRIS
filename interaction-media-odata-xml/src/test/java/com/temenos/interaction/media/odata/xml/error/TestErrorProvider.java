package com.temenos.interaction.media.odata.xml.error;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.io.ByteArrayOutputStream;
import java.io.Writer;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
	
		provider.writeTo(mr, EntityResource.class, GenericError.class, null, MediaType.APPLICATION_XML_TYPE, null, new ByteArrayOutputStream());
		
		// turn on static verification
		verifyStatic();
		// verify our method called correctly
		ErrorWriter.write(any(GenericError.class), any(Writer.class));
	}
}
