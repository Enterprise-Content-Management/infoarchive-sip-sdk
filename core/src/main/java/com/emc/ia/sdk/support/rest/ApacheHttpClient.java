/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ApacheHttpClient {

  private static final int STATUS_CODE_RANGE_MIN = 200;
  private static final int STATUS_CODE_RANGE_MAX = 300;

  private final CloseableHttpClient client;
  private final ObjectMapper mapper;

  public ApacheHttpClient() {
    HttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
    client = HttpClients.custom().setConnectionManager(manager).build();
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public <T> T get(String uri, Collection<Header> headers, Class<T> type) throws IOException {
    return execute(newGet(uri, headers), type);
  }

  private HttpGet newGet(String uri, Collection<Header> headers) {
    Objects.requireNonNull(uri, "Missing URI");
    HttpGet result = new HttpGet(uri);
    setHeaders(result, headers);
    return result;
  }

  private void setHeaders(HttpRequestBase request, Collection<Header> headers) {
    for (Header header : headers) {
      request.addHeader(new BasicHeader(header.getName(), header.getValue()));
    }
  }

  protected <T> T execute(HttpUriRequest request, Class<T> type) throws IOException {
    Objects.requireNonNull(request, "Missing request");
    return client.execute(request, getResponseHandler(request.getMethod(), request.getURI().toString(), type));
  }

  <T> ResponseHandler<T> getResponseHandler(String method, String uri, Class<T> type) {
    return response -> {
      StatusLine statusLine = response.getStatusLine();
      int status = statusLine.getStatusCode();
      HttpEntity entity = response.getEntity();
      String body = entity == null ? "" : EntityUtils.toString(entity);
      if (!isOk(status)) {
        throw new HttpResponseException(status,
            String.format("%n%s %s%n==> %d %s%n%s", method, uri, status, statusLine.getReasonPhrase(), body));
      }
      if (body.isEmpty() || type == null) {
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

  public <T> T put(String uri, Collection<Header> headers, Class<T> type) throws IOException {
    return execute(newPut(uri, headers), type);
  }

  private HttpPut newPut(String uri, Collection<Header> headers) {
    Objects.requireNonNull(uri, "Missing URI");
    HttpPut result = new HttpPut(uri);
    setHeaders(result, headers);
    return result;
  }

  public <T> T post(String uri, Collection<Header> headers, String payload, Class<T> type) throws IOException {
    HttpPost request = newPost(uri, headers);
    request.setEntity(new StringEntity(payload));
    return execute(request, type);
  }

  private HttpPost newPost(String uri, Collection<Header> headers) {
    Objects.requireNonNull(uri, "Missing URI");
    HttpPost result = new HttpPost(uri);
    setHeaders(result, headers);
    return result;
  }

  public <T> T post(String uri, Collection<Header> headers, Class<T> type, Part... parts) throws IOException {
    HttpPost request = newPost(uri, headers);
    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
    for (Part part : parts) {
      entityBuilder.addPart(part.getName(), newContentBody(part));
    }
    request.setEntity(entityBuilder.build());
    return execute(request, type);
  }

  private ContentBody newContentBody(Part part) throws UnsupportedEncodingException {
    ContentType contentType = ContentType.create(part.getMediaType());
    if (part instanceof TextPart) {
      TextPart textPart = (TextPart)part;
      return new StringBody(textPart.getText(), contentType);
    }
    if (part instanceof BinaryPart) {
      BinaryPart binaryPart = (BinaryPart)part;
      return new InputStreamBody(binaryPart.getData(), contentType, binaryPart.getDownloadName());
    }
    throw new IllegalArgumentException("Unhandled part type: " + part.getClass().getName());
  }

  public void close() {
    IOUtils.closeQuietly(client);
  }

}
