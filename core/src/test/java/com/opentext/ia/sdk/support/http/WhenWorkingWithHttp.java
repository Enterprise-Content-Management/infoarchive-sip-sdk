/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.http.apache.ApacheHttpClient;
import com.opentext.ia.sdk.support.io.ByteArrayInputOutputStream;
import com.opentext.ia.test.TestCase;
import com.sun.net.httpserver.HttpServer;


class WhenWorkingWithHttp extends TestCase {

  private static final String PATH = "/echo";
  private String uri;
  private final HttpClient client = new ApacheHttpClient();
  private final Collection<Header> headers = Collections.emptyList();
  private HttpServer server;
  private Thread serverThread;

  @BeforeEach
  public void init() throws IOException {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    int port = server.getAddress().getPort();
    uri = "http://localhost:" + port + PATH;
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

  @AfterEach
  public void done() throws InterruptedException {
    client.close();
    server.stop(0);
    serverThread.join();
  }

  @Test
  void shouldHandleTextData() throws IOException {
    String expected = randomString(8);
    assertNull(client.get(uri, headers, String.class), "GET");
    assertEquals(expected, client.put(uri, headers, String.class, expected), "PUT");
    assertEquals(expected, client.post(uri, headers, String.class, expected), "POST");
  }

  @Test
  void shouldHandleBinaryData() throws IOException {
    byte[] expected = randomBytes();

    assertResponse(new byte[0], client.get(uri, headers, InputStream.class), "GET");
    assertResponse(expected, client.put(uri, headers, InputStream.class,
        new ByteArrayInputStream(expected)), "PUT");
    assertResponse(expected, client.post(uri, headers, InputStream.class,
        new ByteArrayInputStream(expected)), "POST");
  }

  private void assertResponse(byte[] expected, InputStream actual, String message)
      throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    IOUtils.copy(actual, output);
    assertArrayEquals(expected, output.toByteArray(), message);
  }

}
