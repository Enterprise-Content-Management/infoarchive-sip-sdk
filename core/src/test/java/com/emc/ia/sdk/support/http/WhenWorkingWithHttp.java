/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.io.ByteArrayInputOutputStream;
import com.emc.ia.sdk.support.test.TestCase;
import com.sun.net.httpserver.HttpServer;


public class WhenWorkingWithHttp extends TestCase {

  private static final int PORT = 8000;
  private static final String PATH = "/echo";
  private static final String URI = "http://localhost:" + PORT + PATH;

  private final HttpClient client = new ApacheHttpClient();
  private final Collection<Header> headers = Collections.emptyList();
  private HttpServer server;
  private Thread serverThread;

  @Before
  public void init() throws IOException {
    server = HttpServer.create(new InetSocketAddress(PORT), 0);
    server.createContext(PATH, httpExchange -> {
      ByteArrayInputOutputStream content = new ByteArrayInputOutputStream();
      long size = IOUtils.copy(httpExchange.getRequestBody(), content);
      httpExchange.sendResponseHeaders(200, size);
      try (InputStream input = content.getInputStream()) {
        IOUtils.copy(input, httpExchange.getResponseBody());
      }
      httpExchange.close();
    });
    server.setExecutor(null);
    serverThread = new Thread(() -> server.start());
    serverThread.start();
  }

  @After
  public void done() throws InterruptedException {
    client.close();
    server.stop(0);
    serverThread.join();
  }

  @Test
  public void shouldHandleTextData() throws Exception {
    String expected = randomString(8);
    assertNull("GET", client.get(URI, headers, String.class));
    assertEquals("PUT",  expected, client.put(URI,  headers, String.class, expected));
    assertEquals("POST", expected, client.post(URI, headers, String.class, expected));
  }

  @Test
  public void shouldHandleBinaryData() throws Exception {
    byte[] expected = randomBytes();

    assertResponse("GET", new byte[0], client.get(URI, headers, InputStream.class));
    assertResponse("PUT", expected, client.put(URI, headers, InputStream.class,
        new ByteArrayInputStream(expected)));
    assertResponse("POST", expected, client.post(URI, headers, InputStream.class,
        new ByteArrayInputStream(expected)));
  }

  private void assertResponse(String message, byte[] expected, InputStream actual) throws IOException, ArrayComparisonFailure {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    IOUtils.copy(actual, output);
    assertArrayEquals(message, expected, output.toByteArray());
  }

}
