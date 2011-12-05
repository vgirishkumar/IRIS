package com.temenos.interaction.example.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.Responses;

import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.example.country.CountryResource;
import com.temenos.interaction.example.country.GetCountryCommand;

public class TestGetCountryCommand {

	@Test(expected=AssertionError.class)
	public void testExecuteNoEntityManager() {
		GetCountryCommand command = new GetCountryCommand();
		command.get("123");
	}

	@Test
	public void testExecuteNotFound() {
		GetCountryCommand command = new GetCountryCommand();
		ODataProducer mockP = mock(ODataProducer.class);
		when(mockP.getEntity(anyString(), (OEntityKey) isNull(), (QueryInfo) isNull())).thenReturn(null);
		command.setProducer(mockP);
		RESTResponse resp = command.get("123");
		assertNotNull(resp);
		assertEquals(Response.Status.NOT_FOUND, resp.getStatus());
	}

	@Test
	public void testExecuteOK() {
		GetCountryCommand command = new GetCountryCommand();
		ODataProducer mockP = mock(ODataProducer.class);
		OEntity mockEntity = mock(OEntity.class);
		EntityResponse response = Responses.entity(mockEntity);
		when(mockP.getEntity(anyString(), any(OEntityKey.class), (QueryInfo) isNull())).thenReturn(response);
		command.setProducer(mockP);
		RESTResponse resp = command.get("123");
		assertNotNull(resp);
		assertEquals(Response.Status.OK, resp.getStatus());
		assertNotNull(command.getResource());
		assertEquals(CountryResource.class, command.getResource().getClass());
		assertEquals(mockEntity, ((CountryResource)command.getResource()).getEntity());
	}

}
