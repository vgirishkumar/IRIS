package com.interaction.example.odata.northwind;

/*
 * #%L
 * interaction-example-odata-northwind
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
		OEntity customer = getEntity(getConsumer(), "UnknownEntity", 1);
		Assert.assertNull(customer);
	}

	@Test
	public void test404NoEntity() {
		OEntity customer = getEntity(getConsumer(), "Customers", "NOUSER");
		Assert.assertNull(customer);
	}

	@Test
	public void test500InvalidKey() {
		ODataConsumer consumer = getConsumer();
		OEntity customer = null;
		boolean exceptionThrown = true;
		try {
			customer = consumer.getEntity("Customers", 1).execute();
		} catch (Exception e) {
			exceptionThrown = true;
		}
		Assert.assertNull(customer);
		Assert.assertTrue("We expect a 500 error here", exceptionThrown);
	}
	
	// Helper method to get initialise OdataConsumer
	private ODataConsumer getConsumer() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();
		return consumer;
	}
	
	private OEntity getEntity(ODataConsumer consumer, String entityType, Object keyValue) {
		OEntity customer = null;
		boolean exceptionThrown = false;
		try {
			customer = consumer.getEntity(entityType, keyValue).execute();
		} catch (Exception e) {
			// With Odata4j 0.7 Release client expects all GET commands to return Status.OK OR Status.NO_CONTENT 
			// But with HTTP Specs for non-exiting URL the service should return 404 (NOT_FOUND)
			// Because of the above clash we are sticking to HTTP Specs, Odata4j Client throws exception which we need to catch
			exceptionThrown = true;
		}
		Assert.assertTrue(exceptionThrown);
		return customer;
	}
}
