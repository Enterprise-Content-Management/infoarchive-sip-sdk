/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
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
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import com.emc.ia.sdk.support.test.TestCase;


public class WhenMakingHttpCalls extends TestCase {

  private static final String URL = "http://identifiers.emc.com/aips";
  private static final List<Header> HEADERS = new ArrayList<Header>();

  private HttpClient httpClient;

  @Before
  public void init() {
    httpClient = new HttpClient();
    HEADERS.add(new BasicHeader("AuthToken", "XYZ123ABC"));
    HEADERS.add(new BasicHeader("Accept", "application/hal+json"));
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
    HttpResponse response = mock(HttpResponse.class);
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(400);
    when(response.getStatusLine()).thenReturn(statusLine);

    httpClient.getResponseHandler(randomString(), randomString(), null).handleResponse(response);
  }

}
