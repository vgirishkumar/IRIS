package com.temenos.interaction.core.decorator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.junit.Test;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmEntitySet;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;

public class TestJSONStreamingDecorator {

	@Test
	public void testDecorateRESTResponse() throws IOException {
		JSONStreamingDecorator decorator = new JSONStreamingDecorator();
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("name", "Aaron"));
		properties.add(OProperties.string("phone", "999"));
		
		EdmEntitySet entityMetaData = mock(EdmEntitySet.class);
		List<OLink> links = new ArrayList<OLink>();
		OEntity entity = OEntities.create(entityMetaData, OEntityKey.create("123"), properties, links);
		
		EntityResource resource = mock(EntityResource.class);
		when(resource.getOEntity()).thenReturn(entity);

		RESTResponse rr = new RESTResponse(Response.Status.OK, resource, null);
		StreamingOutput so = decorator.decorateRESTResponse(rr);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		so.write(baos);
		
		assertEquals("{\"name\":\"Aaron\",\"phone\":\"999\"}", new String(baos.toByteArray()));
	}

}
