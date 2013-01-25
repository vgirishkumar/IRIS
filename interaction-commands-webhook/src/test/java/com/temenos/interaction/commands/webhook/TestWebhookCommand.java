package com.temenos.interaction.commands.webhook;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.wink.client.MockHttpServer;
import org.junit.Test;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.EntityResource;

public class TestWebhookCommand {

	public int serverPort;
	
	@Test(expected = AssertionError.class)
	public void testUrlSupplied() {
		new WebhookCommand(null).execute(null);
	}
	
	@Test
	public void testFormData() {
		WebhookCommand c = new WebhookCommand("");

		EntityProperties fields = new EntityProperties();
		fields.setProperty(new EntityProperty("first_name", "Bob"));
		fields.setProperty(new EntityProperty("last_name", "Dodge"));
		fields.setProperty(new EntityProperty("email", "test@test.com"));
		Entity entity = new Entity("blah", fields);
		
		assertEquals("email=test@test.com&first_name=Bob&last_name=Dodge", c.getFormData(entity));
	}

	@Test
	public void testNullPropFormData() {
		WebhookCommand c = new WebhookCommand("");

		EntityProperties fields = new EntityProperties();
		fields.setProperty(new EntityProperty("first_name", null));
		fields.setProperty(new EntityProperty("last_name", "Dodge"));
		Entity entity = new Entity("blah", fields);
		
		assertEquals("first_name=&last_name=Dodge", c.getFormData(entity));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleEntity() {
		// use this neat little http server to capture our webhook post data
		MockHttpServer httpServer = new MockHttpServer(34567);
		httpServer.startServer();
		serverPort = httpServer.getServerPort();
		
		WebhookCommand c = new WebhookCommand("http://127.0.0.1:" + serverPort + "/service");

		EntityProperties fields = new EntityProperties();
		fields.setProperty(new EntityProperty("first_name", "Bob"));
		fields.setProperty(new EntityProperty("last_name", "Dodge"));
		fields.setProperty(new EntityProperty("email", "test@test.com"));
		EntityResource<Entity> before = new EntityResource<Entity>(new Entity("blah", fields));
		
		MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
		MultivaluedMap<String, String> queryParams = mock(MultivaluedMap.class);
		InteractionContext ctx = new InteractionContext(pathParams, queryParams, mock(ResourceState.class), mock(Metadata.class));
		ctx.setResource(before);
		c.execute(ctx);

		assertEquals("POST", httpServer.getRequestMethod());
		assertEquals("email=test@test.com&first_name=Bob&last_name=Dodge", 
				httpServer.getRequestContentAsString());
		
		httpServer.stopServer();
	}

}
