/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HttpClientWrapper {

  private static final int STATUS_CODE_RANGE_MIN = 200;
  private static final int STATUS_CODE_RANGE_MAX = 300;

  private final CloseableHttpClient client;
  private final ObjectMapper mapper;

  public HttpClientWrapper() {
    final HttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
    client = HttpClients.custom().setConnectionManager(manager).build();

    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public <T> ResponseHandler<T> getResponseHandler(final Class<T> type) {
    return response -> {
      int status = response.getStatusLine().getStatusCode();

      // TODO - need to add logging - print status line and status code

      if (status >= STATUS_CODE_RANGE_MIN && status < STATUS_CODE_RANGE_MAX) {
        try {
          HttpEntity entity = response.getEntity();
          if (entity == null) {
            return null;
          } else {
            String body = EntityUtils.toString(entity);
            return mapper.readValue(body, type);
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      } else {
        throw new ClientProtocolException("Unexpected response status: " + status);
      }
    };

  }

  public HttpGet httpGetRequest(String uri, List<Header> headers) {
    HttpGet getRequest = new HttpGet(uri);
    if (headers != null) {
      for (Header header : headers) {
        getRequest.addHeader(header);
      }
    }
    return getRequest;
  }

  public HttpPost httpPostRequest(String uri, List<Header> headers) {
    HttpPost postRequest = new HttpPost(uri);
    if (headers != null) {
      for (Header header : headers) {
        postRequest.addHeader(header);
      }
    }
    return postRequest;
  }

  public HttpPut httpPutRequest(String uri, List<Header> headers) {
    HttpPut putRequest = new HttpPut(uri);
    if (headers != null) {
      for (Header header : headers) {
        putRequest.addHeader(header);
      }
    }
    return putRequest;
  }

  public CloseableHttpResponse execute(HttpGet getRequest) throws ClientProtocolException, IOException {
    return client.execute(getRequest);
  }

  public <T> T execute(HttpGet getRequest, final Class<T> type) throws ClientProtocolException, IOException {
    return client.execute(getRequest, getResponseHandler(type));
  }

  public <T> T execute(HttpPost postRequest, final Class<T> type) throws ClientProtocolException, IOException {
    return client.execute(postRequest, getResponseHandler(type));
  }

  public <T> T execute(HttpPut putRequest, final Class<T> type) throws ClientProtocolException, IOException {
    return client.execute(putRequest, getResponseHandler(type));
  }

  public void close() {
    IOUtils.closeQuietly(client);
  }

}
