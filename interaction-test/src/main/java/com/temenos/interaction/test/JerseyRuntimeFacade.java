package com.temenos.interaction.test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;

import org.junit.Assert;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.behaviors.MethodTunnelingBehavior;
import org.odata4j.format.FormatType;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;
import org.odata4j.jersey.consumer.ODataJerseyConsumer.Builder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class JerseyRuntimeFacade implements RuntimeFacade {

  static {
    // ensure that the correct JAX-RS implementation is loaded
    RuntimeDelegate runtimeDelegate = new com.sun.jersey.server.impl.provider.RuntimeDelegateImpl();
    RuntimeDelegate.setInstance(runtimeDelegate);
    Assert.assertEquals(runtimeDelegate, RuntimeDelegate.getInstance());
  }

  @Override
  public ODataConsumer create(String endpointUri, FormatType format, String methodToTunnel) {
    Builder builder = ODataJerseyConsumer.newBuilder(endpointUri);

    if (format != null) {
      builder = builder.setFormatType(format);
    }

    if (methodToTunnel != null) {
      builder = builder.setClientBehaviors(new MethodTunnelingBehavior(methodToTunnel));
    }

    return builder.build();
  }

  @Override
  public String getWebResource(String uri) {
    WebResource webResource = new Client().resource(uri);
    return webResource.get(String.class);
  }

  @Override
  public String acceptAndReturn(String uri, MediaType mediaType) {
    uri = uri.replace(" ", "%20");
    WebResource webResource = new Client().resource(uri);
    return webResource.accept(mediaType).get(String.class);
  }

  @Override
  public String getWebResource(String uri, String accept) {
    String resource = new Client().resource(uri).accept(accept).get(String.class);
    return resource;
  }

  @Override
  public void accept(String uri, MediaType mediaType) {
    uri = uri.replace(" ", "%20");
    WebResource webResource = new Client().resource(uri);
    webResource.accept(mediaType);
  }

}
