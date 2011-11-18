package com.temenos.interaction.example.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.odata4j.producer.resources.CrossDomainResourceConfig;
import org.odata4j.producer.resources.ODataResourceConfig;
import org.odata4j.producer.server.JerseyServer;

import com.sun.jersey.api.container.filter.LoggingFilter;

public class JerseyServerUtil {

  public static void hostODataServer(String baseUri) {
    JerseyServer server = startODataServer(baseUri);
    System.out.println("Press any key to exit");
    readLine();
    server.stop();
  }

  public static JerseyServer startODataServer(String baseUri) {
    JerseyServer server = new JerseyServer(baseUri);
    server.addAppResourceClasses(new ODataResourceConfig().getClasses());
    server.addRootResourceClasses(new CrossDomainResourceConfig().getClasses());

    server.addJerseyRequestFilter(LoggingFilter.class); // log all requests
    server.start();

    return server;

  }

  public static void readLine() {
    try {
      new BufferedReader(new InputStreamReader(System.in)).readLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
