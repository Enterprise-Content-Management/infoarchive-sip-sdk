/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.support.rest.HttpClient;


public class WhenExecutingHttpClientWrapper {

  private static final String URI = "http://identifiers.emc.com/aips";
  private static final List<Header> HEADERS = new ArrayList<Header>();

  private HttpClient wrapper;

  @Before
  public void init() {
    wrapper = new HttpClient();
    HEADERS.add(new BasicHeader("AuthToken", "XYZ123ABC"));
    HEADERS.add(new BasicHeader("Accept", "application/hal+json"));
  }

  @Test
  public void shouldReturnValidHttpGetRequest() {
    HttpGet httpGet = wrapper.httpGetRequest(URI, HEADERS);

    assertNotNull(httpGet);
    assertEquals(httpGet.toString(), "GET http://identifiers.emc.com/aips HTTP/1.1");
  }

  @Test
  public void shouldReturnValidHttpPutRequest() {
    HttpPut putRequest = wrapper.httpPutRequest(URI, HEADERS);

    assertNotNull(putRequest);
    assertEquals(putRequest.toString(), "PUT http://identifiers.emc.com/aips HTTP/1.1");
  }

  @Test
  public void shouldReturnValidHttpPostRequest() {
    HttpPost postRequest = wrapper.httpPostRequest(URI, HEADERS);

    assertNotNull(postRequest);
    assertEquals(postRequest.toString(), "POST http://identifiers.emc.com/aips HTTP/1.1");
  }

  @Test
  public void shouldExecuteHttpCall() throws IOException {
    String html = wrapper.execute(wrapper.httpGetRequest("http://www.google.com", Collections.emptyList()), String.class);
    assertTrue(html.contains("Google"));
  }

}
