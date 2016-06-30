/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.emc.ia.sdk.sip.ingestion.dto.Services;
import com.emc.ia.sdk.support.rest.HttpClient;
import com.emc.ia.sdk.support.rest.Link;
import com.emc.ia.sdk.support.rest.LinkContainer;
import com.emc.ia.sdk.support.rest.RestClient;
import com.emc.ia.sdk.support.test.TestCase;


public class WhenMakingRestCalls extends TestCase {

  private static final String URI = "http://identifiers.emc.com/aips";
  private static List<Header> headers = new ArrayList<Header>();

  private final HttpClient httpClient = mock(HttpClient.class);
  private final RestClient restClient = new RestClient(httpClient);

  @BeforeClass
  public static void initClass() {
    headers.add(new BasicHeader("AuthToken", "XYZ123ABC"));
    headers.add(new BasicHeader("Accept", "application/hal+json"));
  }

  @Before
  public void init() {
    restClient.setHeaders(headers);
  }

  @Test
  public void shouldExecuteGetSuccessfully() throws IOException {
    HttpGet getRequest = new HttpGet();
    Services resource = new Services();
    when(httpClient.httpGetRequest(URI, headers)).thenReturn(getRequest);
    when(httpClient.execute(getRequest, Services.class)).thenReturn(resource);

    Services homeResource = restClient.get(URI, Services.class);

    assertNotNull(homeResource);
    verify(httpClient).httpGetRequest(URI, headers);
    verify(httpClient).execute(any(HttpGet.class), eq(Services.class));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IOException.class)
  public void shouldThrowExceptionWhenGetIsCalled() throws IOException {
    HttpGet getRequest = new HttpGet();
    when(httpClient.httpGetRequest(URI, headers)).thenReturn(getRequest);
    when(httpClient.execute(getRequest, Services.class)).thenThrow(ClientProtocolException.class);
    restClient.get(URI, Services.class);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IOException.class)
  public void shouldThrowExceptionWhenGetIsCalled2() throws IOException {
    HttpGet getRequest = new HttpGet();
    when(httpClient.httpGetRequest(URI, headers)).thenReturn(getRequest);
    when(httpClient.execute(getRequest, Services.class)).thenThrow(IOException.class);

    restClient.get(URI, Services.class);
  }

  @Test
  public void shouldExecutePutSuccessfully() throws IOException {
    HttpPut putRequest = new HttpPut();
    Services resource = new Services();
    when(httpClient.httpPutRequest(URI, headers)).thenReturn(putRequest);
    when(httpClient.execute(putRequest, Services.class)).thenReturn(resource);

    Services homeResource = restClient.put(URI, Services.class);

    assertNotNull(homeResource);
    verify(httpClient).httpPutRequest(URI, headers);
    verify(httpClient).execute(any(HttpPut.class), eq(Services.class));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IOException.class)
  public void shouldThrowExceptionWhenPutIsCalled() throws IOException {
    HttpPut putRequest = new HttpPut();
    when(httpClient.httpPutRequest(URI, headers)).thenReturn(putRequest);
    when(httpClient.execute(putRequest, Services.class)).thenThrow(ClientProtocolException.class);

    restClient.put(URI, Services.class);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IOException.class)
  public void shouldThrowExceptionWhenPutIsCalled2() throws IOException {
    HttpPut putRequest = new HttpPut();
    when(httpClient.httpPutRequest(URI, headers)).thenReturn(putRequest);
    when(httpClient.execute(putRequest, Services.class)).thenThrow(IOException.class);

    restClient.put(URI, Services.class);
  }

  @Test
  public void shouldCloseHttpClientConnection() {
    restClient.close();

    verify(httpClient).close();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldFollowLinks() throws IOException {
    LinkContainer state = new LinkContainer();
    String relation = randomString();
    String uri = randomString();
    Link link = new Link();
    link.setHref(uri);
    state.getLinks().put(relation, link);

    restClient.follow(state, relation, LinkContainer.class);

    verify(httpClient).httpGetRequest(eq(uri), any(List.class));
  }

}
