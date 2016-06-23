/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.emc.ia.sdk.sip.ingestion.dto.HomeResource;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionResponse;


public class WhenExecutingRestClient {

  private static final String URI = "http://identifiers.emc.com/aips";
  private static final List<Header> HEADERS = new ArrayList<Header>();

  private final HttpClientWrapper wrapper = mock(HttpClientWrapper.class);
  private final GenericRestClient client = new GenericRestClient();

  @BeforeClass
  public static void initClass() {
    HEADERS.add(new BasicHeader("AuthToken", "XYZ123ABC"));
    HEADERS.add(new BasicHeader("Accept", "application/hal+json"));
  }

  @Before
  public void init() {
    client.prepare(wrapper);
  }

  @Test
  public void shouldExecuteGetSuccessfully() throws IOException {
    HttpGet getRequest = new HttpGet();
    HomeResource resource = new HomeResource();
    when(wrapper.httpGetRequest(URI, HEADERS)).thenReturn(getRequest);
    when(wrapper.execute(getRequest, HomeResource.class)).thenReturn(resource);

    HomeResource homeResource = client.get(URI, HEADERS, HomeResource.class);

    assertNotNull(homeResource);
    verify(wrapper).httpGetRequest(URI, HEADERS);
    verify(wrapper).execute(any(HttpGet.class), eq(HomeResource.class));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenGetIsCalled() throws IOException {
    HttpGet getRequest = new HttpGet();
    when(wrapper.httpGetRequest(URI, HEADERS)).thenReturn(getRequest);
    when(wrapper.execute(getRequest, HomeResource.class)).thenThrow(ClientProtocolException.class);
    client.get(URI, HEADERS, HomeResource.class);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenGetIsCalled2() throws IOException {
    HttpGet getRequest = new HttpGet();
    when(wrapper.httpGetRequest(URI, HEADERS)).thenReturn(getRequest);
    when(wrapper.execute(getRequest, HomeResource.class)).thenThrow(RuntimeException.class);

    client.get(URI, HEADERS, HomeResource.class);
  }

  @Test
  public void shouldExecutePutSuccessfully() throws IOException {
    HttpPut putRequest = new HttpPut();
    HomeResource resource = new HomeResource();
    when(wrapper.httpPutRequest(URI, HEADERS)).thenReturn(putRequest);
    when(wrapper.execute(putRequest, HomeResource.class)).thenReturn(resource);

    HomeResource homeResource = client.put(URI, HEADERS, HomeResource.class);

    assertNotNull(homeResource);
    verify(wrapper).httpPutRequest(URI, HEADERS);
    verify(wrapper).execute(any(HttpPut.class), eq(HomeResource.class));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenPutIsCalled() throws IOException {
    HttpPut putRequest = new HttpPut();
    when(wrapper.httpPutRequest(URI, HEADERS)).thenReturn(putRequest);
    when(wrapper.execute(putRequest, HomeResource.class)).thenThrow(ClientProtocolException.class);
    client.put(URI, HEADERS, HomeResource.class);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenPutIsCalled2() throws IOException {
    HttpPut putRequest = new HttpPut();
    when(wrapper.httpPutRequest(URI, HEADERS)).thenReturn(putRequest);
    when(wrapper.execute(putRequest, HomeResource.class)).thenThrow(RuntimeException.class);
    client.put(URI, HEADERS, HomeResource.class);
  }

  @Test
  public void shouldExecutePostSuccessfully() throws IOException {
    HttpPost postRequest = new HttpPost();
    ReceptionResponse resource = new ReceptionResponse();

    when(wrapper.httpPostRequest(URI, HEADERS)).thenReturn(postRequest);
    when(wrapper.execute(postRequest, ReceptionResponse.class)).thenReturn(resource);

    String source = "This is the source of my input stream";
    InputStream in = IOUtils.toInputStream(source, "UTF-8");

    assertNotNull(client.post(URI, HEADERS, "This is a test message", in, ReceptionResponse.class));

    verify(wrapper).httpPostRequest(URI, HEADERS);
    verify(wrapper).execute(any(HttpPost.class), eq(ReceptionResponse.class));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenPostIsCalled() throws IOException {
    HttpPost postRequest = new HttpPost();
    String source = "This is the source of my input stream";
    InputStream in = IOUtils.toInputStream(source, "UTF-8");

    when(wrapper.httpPostRequest(URI, HEADERS)).thenReturn(postRequest);
    when(wrapper.execute(postRequest, ReceptionResponse.class)).thenThrow(ClientProtocolException.class);
    client.post(URI, HEADERS, "This is a test message", in, ReceptionResponse.class);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWhenPostIsCalled2() throws IOException {
    HttpPost postRequest = new HttpPost();
    String source = "This is the source of my input stream";
    InputStream in = IOUtils.toInputStream(source, "UTF-8");

    when(wrapper.httpPostRequest(URI, HEADERS)).thenReturn(postRequest);
    when(wrapper.execute(postRequest, ReceptionResponse.class)).thenThrow(RuntimeException.class);
    client.post(URI, HEADERS, "This is a test message", in, ReceptionResponse.class);
  }

  @Test
  public void shouldCloseHttpClientConnection() {
    client.close();

    verify(wrapper).close();
  }

}
