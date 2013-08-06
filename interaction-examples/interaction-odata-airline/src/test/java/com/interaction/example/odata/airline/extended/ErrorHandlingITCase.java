package com.interaction.example.odata.airline.extended;

import static org.junit.Assert.assertEquals;
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
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI))).build();
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
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(ConfigurationHelper.getTestEndpointUri(ConfigurationHelper.getTestEndpointUri(Configuration.TEST_ENDPOINT_URI))).build();
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
}
