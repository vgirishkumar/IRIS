package com.interaction.example.odata.northwind;

import junit.framework.Assert;

import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

public class ExceptionITCase extends AbstractNorthwindRuntimeTest {

	public ExceptionITCase(RuntimeFacadeType type) {
		super(type);
	}

	@Test
	public void test404NoEntityType() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();
		OEntity customer = null;
		try {
			customer = consumer.getEntity("UnknownEntity", 1).execute();
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		Assert.assertNull(customer);
	}

	@Test
	public void test404NoEntity() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();
		OEntity customer = null;
		try {
			customer = consumer.getEntity("Customers", "NOUSER").execute();
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		Assert.assertNull(customer);
	}

	@Test
	public void test500InvalidKey() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();
		OEntity customer = consumer.getEntity("Customers", 1).execute();
		Assert.assertNull(customer);
	}
}
