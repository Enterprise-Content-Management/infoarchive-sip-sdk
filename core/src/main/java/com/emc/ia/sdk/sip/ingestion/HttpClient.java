/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HttpClient {

  private static final int STATUS_CODE_RANGE_MIN = 200;
  private static final int STATUS_CODE_RANGE_MAX = 300;

  private final CloseableHttpClient client;
  private final ObjectMapper mapper;

  public HttpClient() {
    final HttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
    client = HttpClients.custom().setConnectionManager(manager).build();

    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public <T> ResponseHandler<T> getResponseHandler(final String method, final String uri, final Class<T> type) {
    return response -> {
      StatusLine statusLine = response.getStatusLine();
      int status = statusLine.getStatusCode();
      HttpEntity entity = response.getEntity();
      String body = entity == null ? "" : EntityUtils.toString(entity);
      if (!isOk(status)) {
        throw new HttpResponseException(status,
            String.format("%n%s %s%n==> %d %s%n%s", method, uri, status, statusLine.getReasonPhrase(), body));
      }
      if (body.isEmpty()) {
        return null;
      }
      if (type.equals(String.class)) {
        return type.cast(body);
      }
      try {
        return mapper.readValue(body, type);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }

  private boolean isOk(int status) {
    return STATUS_CODE_RANGE_MIN <= status && status < STATUS_CODE_RANGE_MAX;
  }

  public HttpGet httpGetRequest(String uri, List<Header> headers) {
    Objects.requireNonNull(uri, "Missing URI");
    HttpGet getRequest = new HttpGet(uri);
    if (headers != null) {
      for (Header header : headers) {
        getRequest.addHeader(header);
      }
    }
    return getRequest;
  }

  public HttpPost httpPostRequest(String uri, List<Header> headers) {
    Objects.requireNonNull(uri, "Missing URI");
    HttpPost postRequest = new HttpPost(uri);
    if (headers != null) {
      for (Header header : headers) {
        postRequest.addHeader(header);
      }
    }
    return postRequest;
  }

  public HttpPut httpPutRequest(String uri, List<Header> headers) {
    Objects.requireNonNull(uri, "Missing URI");
    HttpPut putRequest = new HttpPut(uri);
    if (headers != null) {
      for (Header header : headers) {
        putRequest.addHeader(header);
      }
    }
    return putRequest;
  }

  public CloseableHttpResponse execute(HttpGet getRequest) throws IOException {
    return client.execute(getRequest);
  }

  public <T> T execute(HttpUriRequest request, Class<T> type) throws IOException {
    return client.execute(request, getResponseHandler(request.getMethod(), request.getURI().toString(), type));
  }

  public void close() {
    IOUtils.closeQuietly(client);
  }

}
