package com.interaction.example.odata.northwind;

import org.core4j.Enumerable;
import org.junit.Assert;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

public class SimpleQueriesITCase {

	protected static final String endpointUri = "http://localhost:8080/responder/rest/";

	public SimpleQueriesITCase() throws Exception {
		super();
	}
	
	@Test
	public void testMetadata() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();

		EdmDataServices metadata = consumer.getMetadata();

		Assert.assertEquals(EdmSimpleType.STRING, 
				metadata.findEdmEntitySet("Customers").getType().findProperty("Country").getType());
	}

	@Test
	public void getUkCustomers() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();

		Enumerable<OEntity> customers = consumer
				.getEntities("Customers")
				.filter("Country eq 'UK'")
				.execute();
		
		Assert.assertEquals(7, customers.count());
	}

}
