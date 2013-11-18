package org.odata4j.jersey.consumer;

/*
 * #%L
 * interaction-example-odata-airline
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
import javax.ws.rs.ext.RuntimeDelegate;

import org.core4j.Enumerable;
import org.odata4j.consumer.AbstractODataConsumer;
import org.odata4j.consumer.ConsumerGetEntityRequest;
import org.odata4j.consumer.ODataClient;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataClientResponse;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityGetRequest;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.exceptions.ODataProducerException;
import org.odata4j.format.Entry;
import org.odata4j.format.FormatType;
import org.odata4j.format.xml.AtomEntryFormatParserExt;
import org.odata4j.internal.EntitySegment;
import org.odata4j.internal.InternalUtil;

/**
 * Extended OData Jersey consumer with additional support for e.g. Bag types.
 */
public class ODataJerseyConsumerExt extends AbstractODataConsumer {
	private ODataJerseyClient client;

	public ODataJerseyConsumerExt(String serviceRootUri, OClientBehavior... behaviors) {
		this(serviceRootUri, FormatType.ATOM, DefaultJerseyClientFactory.INSTANCE, behaviors);
	}

	public ODataJerseyConsumerExt(String serviceRootUri, FormatType formatType, JerseyClientFactory clientFactory, OClientBehavior... behaviors) {
		super(serviceRootUri);
		
		// ensure that a correct JAX-RS implementation (Jersey, server or default) is loaded
		if (!(RuntimeDelegate.getInstance() instanceof com.sun.jersey.core.spi.factory.AbstractRuntimeDelegate)) {
			RuntimeDelegate.setInstance(new com.sun.ws.rs.ext.RuntimeDelegateImpl());
		}		
		this.client = new ODataJerseyClient(formatType, clientFactory, behaviors);
	}
	
	@Override
	protected ODataClient getClient() {
		return client;
	}

	@Override
	public <T> OEntityGetRequest<T> getEntity(Class<T> entityType, String entitySetName, OEntityKey key) {
		return new ConsumerGetEntityRequest<T>(getClient(), null, getServiceRootUri(), getMetadata(), entitySetName, OEntityKey.create(key), null) {
			  @SuppressWarnings("unchecked")
			  @Override
			  public T execute() throws ODataProducerException {
			    String path = Enumerable.create(getSegments()).join("/");
			    ODataClientRequest request = ODataClientRequest.get(getServiceRootUri() + path);
			    ODataClientResponse response = getClient().getEntity(request);
			    if (response == null)
			      return null;

			    //  the first segment contains the entitySetName we start from
			    EdmEntitySet entitySet = getMetadata().getEdmEntitySet(getSegments().get(0).segment);
			    for (EntitySegment segment : getSegments().subList(1, getSegments().size())) {
			      EdmNavigationProperty navProperty = entitySet.getType().findNavigationProperty(segment.segment);
			      entitySet = getMetadata().getEdmEntitySet(navProperty.getToRole().getType());
			    }

			    OEntityKey key = Enumerable.create(getSegments()).last().key;

			    //Use the extended atom entry parser
				Entry entry = new AtomEntryFormatParserExt(getMetadata(), entitySet.getName(), key, null).parse(getClient().getFeedReader(response));

			    response.close();
			    return (T) InternalUtil.toEntity(OEntity.class, entry.getEntity());
			  }
		};
	}
	
}
