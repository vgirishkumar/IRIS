package com.temenos.interaction.core.media.edmx;

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
import org.odata4j.edm.EdmDataServices;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.temenos.interaction.core.hypermedia.ResourceRegistry;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.MetaDataResource;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EdmxMetaDataWriter.class})
public class TestEdmxMetadataProvider {

	@Test
	public void testAcceptMetaDataResource() {
		EdmxMetaDataProvider provider = new EdmxMetaDataProvider(mock(ResourceStateMachine.class));
		assertTrue(provider.isWriteable(MetaDataResource.class, EdmDataServices.class, null, MediaType.APPLICATION_XML_TYPE));
	}

	@Test(expected = AssertionError.class)
	public void testDoNoAcceptMetaDataResource() {
		EdmxMetaDataProvider provider = new EdmxMetaDataProvider(mock(ResourceStateMachine.class));
		assertTrue(provider.isWriteable(CollectionResource.class, EdmDataServices.class, null, MediaType.APPLICATION_XML_TYPE));
	}

	@Test
	public void testWrite() throws Exception {
		MetaDataResource<EdmDataServices> mr = new MetaDataResource<EdmDataServices>(mock(EdmDataServices.class));
		EdmxMetaDataProvider provider = new EdmxMetaDataProvider(mock(ResourceStateMachine.class));
		
		// make sure write does nothing
		mockStatic(EdmxMetaDataWriter.class);
	
		provider.writeTo(mr, MetaDataResource.class, EdmDataServices.class, null, MediaType.APPLICATION_XML_TYPE, null, new ByteArrayOutputStream());
		
		// turn on static verification
		verifyStatic();
		// verify our method called correctly
		EdmxMetaDataWriter.write(any(EdmDataServices.class), any(Writer.class), any(ResourceStateMachine.class));
	}
}
