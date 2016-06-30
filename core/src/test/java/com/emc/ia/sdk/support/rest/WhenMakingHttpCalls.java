/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import com.emc.ia.sdk.support.test.TestCase;


public class WhenMakingHttpCalls extends TestCase {

  private static final String URL = "http://identifiers.emc.com/aips";
  private static final List<Header> HEADERS = new ArrayList<Header>();

  private final HttpClient httpClient = new HttpClient();
  private final HttpResponse response = mock(HttpResponse.class);
  private final StatusLine statusLine = mock(StatusLine.class);

  @Before
  public void init() {
    HEADERS.add(new BasicHeader("AuthToken", "XYZ123ABC"));
    HEADERS.add(new BasicHeader("Accept", "application/hal+json"));
    when(response.getStatusLine()).thenReturn(statusLine);
    when(statusLine.getStatusCode()).thenReturn(200);
  }

  @Test
  public void shouldReturnValidHttpGetRequest() {
    HttpGet request = httpClient.httpGetRequest(URL, HEADERS);

    assertRequest(request);
  }

  private void assertRequest(HttpRequestBase actual) throws ArrayComparisonFailure {
    assertNotNull(actual);
    assertEquals("URI", URI.create(URL), actual.getURI());
    assertArrayEquals("Headers", HEADERS.toArray(), actual.getAllHeaders());
  }

  @Test
  public void shouldReturnValidHttpPutRequest() {
    HttpPut request = httpClient.httpPutRequest(URL, HEADERS);

    assertRequest(request);
  }

  @Test
  public void shouldReturnValidHttpPostRequest() {
    HttpPost request = httpClient.httpPostRequest(URL, HEADERS);

    assertRequest(request);
  }

  @Test
  public void shouldExecuteHttpCall() throws IOException {
    String html = httpClient.execute(httpClient.httpGetRequest("http://www.google.com", Collections.emptyList()),
        String.class);

    assertTrue(html.contains("Google"));
  }

  @Test(expected = HttpResponseException.class)
  public void shouldThrowExceptionOnNonOkStatusCode() throws IOException {
    when(statusLine.getStatusCode()).thenReturn(400);

    getResponse(null);
  }

  private <T> T getResponse(Class<T> type) throws IOException {
    return httpClient.getResponseHandler(randomString(), randomString(), type).handleResponse(response);
  }

  @Test
  public void shouldReturnNullWhenNoEntity() throws IOException {
    Object body = getResponse(null);

    assertNull(body);
  }

  @Test
  public void shouldReturnBodyAsString() throws IOException {
    String expected = randomString();
    returnBody(expected);

    String actual = getResponse(String.class);

    assertEquals(expected, actual);
  }

  private void returnBody(String body) throws UnsupportedEncodingException {
    when(response.getEntity()).thenReturn(new StringEntity(body));
  }

  @Test
  public void shouldReturnBodyAsJson() throws IOException {
    String expected = randomString();
    returnBody("{ \"bar\": \"" + expected + "\" }");

    Foo actual = getResponse(Foo.class);

    assertEquals(expected, actual.getBar());
  }


  public static class Foo {

    private String bar;

    public String getBar() {
      return bar;
    }

    public void setBar(String bar) {
      this.bar = bar;
    }

  }

}
