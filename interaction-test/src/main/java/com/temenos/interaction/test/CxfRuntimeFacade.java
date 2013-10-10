package com.temenos.interaction.test;

/*
 * #%L
 * interaction-test
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


import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.cxf.consumer.ODataCxfConsumer;
import org.odata4j.format.FormatType;

public class CxfRuntimeFacade implements RuntimeFacade {

  private static final long REQUEST_TIMEOUT = 10 * 60 * 1000; // 10 minutes for debugging

  static {
    // ensure that the correct JAX-RS implementation is loaded
    RuntimeDelegate runtimeDelegate = new org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl();
    RuntimeDelegate.setInstance(runtimeDelegate);
    Assert.assertEquals(runtimeDelegate, RuntimeDelegate.getInstance());
  }

  @Override
  public ODataConsumer create(String endpointUri, FormatType format, String methodToTunnel) {
    return ODataCxfConsumer.create(endpointUri);
  }

  @Override
  public String acceptAndReturn(String uri, MediaType mediaType) {
    uri = uri.replace(" ", "%20");
    WebClient client = creatWebClient(uri);

    String resource = client.accept(mediaType).get(String.class);
    return resource;
  }

  @Override
  public String getWebResource(String uri, String accept) {
    uri = uri.replace(" ", "%20");
    WebClient client = creatWebClient(uri);

    String resource = client.accept(accept).get(String.class);
    return resource;
  }

  @Override
  public void accept(String uri, MediaType mediaType) {
    uri = uri.replace(" ", "%20");
    WebClient client = creatWebClient(uri);
    client.accept(mediaType);
  }
  
  @Override
  public String getWebResource(String uri) {
    WebClient client = this.creatWebClient(uri);
    
    String resource = client.get(String.class);
    return resource;
  }

  private WebClient creatWebClient(String uri) {
    WebClient client = WebClient.create(uri);
    // request timeout for debugging
    WebClient.getConfig(client).getHttpConduit().getClient().setReceiveTimeout(CxfRuntimeFacade.REQUEST_TIMEOUT);
    return client;
  }
}
