package com.interaction.example.odata.airline.extended;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OError;
import org.odata4j.exceptions.ODataProducerException;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

import com.interaction.example.odata.airline.Configuration;
import com.interaction.example.odata.airline.ConfigurationHelper;

/**
 * Test error responses and exception handling
 */
public class ErrorHandlingITCase {

	private final static String EXTENDED_ENTITYSET_NAME = "Extended";

	public ErrorHandlingITCase() throws Exception {
		super();
	}
	
	@Test
	public void flightError503ServiceUnavailable() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();
		try {
			consumer.
					getEntity(EXTENDED_ENTITYSET_NAME, 123).
					nav("error503").
					execute();
			fail("error503 should have returned an odata error response.");
		}
		catch(ODataProducerException ope) {
			OError error = ope.getOError();
			assertEquals("UPSTREAM_SERVER_UNAVAILABLE", error.getCode());
			assertEquals("Failed to connect to resource manager.", error.getMessage());
		}
	}

	@Test
	public void flightError504GatewayTimeout() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();
		try {
			consumer.
					getEntity(EXTENDED_ENTITYSET_NAME, 123).
					nav("error504").
					execute();
			fail("error504 should have returned an odata error response.");
		}
		catch(ODataProducerException ope) {
			OError error = ope.getOError();
			assertEquals("UPSTREAM_SERVER_TIMEOUT", error.getCode());
			assertEquals("Request has timed out.", error.getMessage());
		}
	}

	@Test
	public void flightError500CommandRuntimeException() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();
		try {
			consumer.
					getEntity(EXTENDED_ENTITYSET_NAME, 123).
					nav("error500RuntimeException").
					execute();
			fail("error500RuntimeException should have thrown an exception.");
		}
		catch(RuntimeException re) {
			assertTrue(re.getMessage().contains("Unknown fatal error"));
		}
	}

	@Test
	public void flightError403Forbidden() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();
		try {
			consumer.
					getEntity(EXTENDED_ENTITYSET_NAME, 123).
					nav("error403").
					execute();
			fail("error403 should have returned an odata error response.");
		}
		catch(ODataProducerException ope) {
			OError error = ope.getOError();
			assertEquals("AUTHORISATION_FAILURE", error.getCode());
			assertEquals("User is not allowed to access this resource.", error.getMessage());
		}
	}

	@Test
	public void flightError500RequestFailure() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();
		try {
			consumer.
					getEntity(EXTENDED_ENTITYSET_NAME, 123).
					nav("error500RequestFailure").
					execute();
			fail("error500RequestFailure should have returned an odata error response.");
		}
		catch(ODataProducerException ope) {
			OError error = ope.getOError();
			assertEquals("FAILURE", error.getCode());
			assertEquals("Error while processing request.", error.getMessage());
		}
	}

	@Test
	public void flightError500InvalidRequest() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI)).build();
		try {
			consumer.
					getEntity(EXTENDED_ENTITYSET_NAME, 123).
					nav("error400").
					execute();
			fail("error400 should have returned an odata error response.");
		}
		catch(ODataProducerException ope) {
			OError error = ope.getOError();
			assertEquals("INVALID_REQUEST", error.getCode());
			assertEquals("Resource manager: 4 validation errors.", error.getMessage());
		}
	}
}
