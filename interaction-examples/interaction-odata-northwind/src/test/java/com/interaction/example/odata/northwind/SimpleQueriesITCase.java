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


import org.core4j.Enumerable;
import org.junit.Assert;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

public class SimpleQueriesITCase {
	protected static final String endpointUri = "http://localhost:8080/northwind/Northwind.svc/";

	public SimpleQueriesITCase() {
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

	@Test
	public void getCategory() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();
		OEntity category = consumer.getEntity("Categories", 1).execute();
		Assert.assertEquals(1, category.getEntityKey().asSingleValue());
	}

	@Test
	public void getCategoryName() {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();
		OEntity category = consumer.getEntity("Categories", 1).execute();
		Assert.assertEquals("Beverages", category.getProperty("CategoryName").getValue());
	}
}
