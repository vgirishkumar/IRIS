package com.temenos.interaction.commands.webhook;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.wink.client.MockHttpServer;
import org.junit.Test;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.resource.EntityResource;

public class TestWebhookCommand {

	public int serverPort;
	
	@Test
	public void testFormData() {
		WebhookCommand c = new WebhookCommand("");

		EntityProperties fields = new EntityProperties();
		fields.setProperty(new EntityProperty("first_name", "Bob"));
		fields.setProperty(new EntityProperty("last_name", "Dodge"));
		fields.setProperty(new EntityProperty("email", "test@test.com"));
		Entity entity = new Entity("blah", fields);
		
		Map<String,Object> map = c.transform(entity);
		assertEquals("email=test@test.com&first_name=Bob&last_name=Dodge", c.getFormData(map));
	}

	@Test
	public void testNullPropFormData() {
		WebhookCommand c = new WebhookCommand("");

		EntityProperties fields = new EntityProperties();
		fields.setProperty(new EntityProperty("first_name", null));
		fields.setProperty(new EntityProperty("last_name", "Dodge"));
		Entity entity = new Entity("blah", fields);
		
		Map<String,Object> map = c.transform(entity);
		assertEquals("first_name=&last_name=Dodge", c.getFormData(map));
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

	@SuppressWarnings("unchecked")
	@Test
	public void testNullDisabled() {
		WebhookCommand c = new WebhookCommand(null);

		EntityProperties fields = new EntityProperties();
		EntityResource<Entity> before = new EntityResource<Entity>(new Entity("blah", fields));
		
		InteractionContext ctx = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), mock(ResourceState.class), mock(Metadata.class));
		ctx.setResource(before);
		InteractionCommand.Result result = c.execute(ctx);
		
		assertEquals("Should return success code, operating as disabled", InteractionCommand.Result.SUCCESS, result);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEmptyStringDisabled() {
		WebhookCommand c = new WebhookCommand("");

		EntityProperties fields = new EntityProperties();
		EntityResource<Entity> before = new EntityResource<Entity>(new Entity("blah", fields));
		
		InteractionContext ctx = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), mock(ResourceState.class), mock(Metadata.class));
		ctx.setResource(before);
		InteractionCommand.Result result = c.execute(ctx);
		
		assertEquals("Should return success code, operating as disabled", InteractionCommand.Result.SUCCESS, result);
	}

	
}
