package org.odata4j.jersey.consumer;

/*
 * #%L
 * interaction-odata4j-ext
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.odata4j.consumer.ODataClient;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataClientResponse;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OCollection;
import org.odata4j.core.OComplexObject;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.format.FormatType;
import org.odata4j.format.xml.AtomFeedFormatParserExtTest;
import org.odata4j.format.xml.EdmxFormatParser;
import org.odata4j.stax2.XMLEventReader2;
import org.odata4j.stax2.XMLFactoryProvider2;

import com.sun.jersey.api.client.ClientResponse;

public class TestODataJerseyConsumerExt {

	@Test(expected=Exception.class)
	public void testNullServiceUri() {
		new ODataJerseyConsumerExt(null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetEntityWithBag() {
		ODataConsumer consumer = new ODataJerseyConsumerExt("http://localhost/MyTest.svc", FormatType.ATOM, DefaultJerseyClientFactory.INSTANCE) {
			@Override
			protected ODataClient getClient() {
				ODataClient client = mock(ODataClient.class);
				InputStream xml = getClass().getClassLoader().getResourceAsStream("issue193_entry_with_Bag.xml");
				when(client.getFeedReader(any(ODataClientResponse.class))).thenReturn(new InputStreamReader(xml));
				ClientResponse clientResponse = mock(ClientResponse.class);
				when(client.getEntity(any(ODataClientRequest.class))).thenReturn(new JerseyClientResponse(clientResponse));
				return client;
			}

			@Override
			public EdmDataServices getMetadata() {
				InputStream metadataStream = AtomFeedFormatParserExtTest.class.getClassLoader().getResourceAsStream("issue193_metadata.xml");
				XMLEventReader2 reader = XMLFactoryProvider2.getInstance().newXMLInputFactory2().createXMLEventReader(new InputStreamReader(metadataStream));
				return new EdmxFormatParser().parseMetadata(reader);
			}
			
		};
		OEntity entity = consumer.getEntity("FtCommissionTypes", "SCTRDEFAULT").execute();
		assertNotNull(entity);
		assertEquals("FtCommissionTypes", entity.getEntitySetName());
		OProperty<?> shortDescriptions = entity.getProperty("FtCommissionType_ShortDescrMvGroup");
		OCollection<OComplexObject> shortDescriptionCollection = (OCollection) shortDescriptions.getValue();
		assertEquals("Sec Default", shortDescriptionCollection.iterator().next().getProperty("ShortDescr").getValue());
	}
}
