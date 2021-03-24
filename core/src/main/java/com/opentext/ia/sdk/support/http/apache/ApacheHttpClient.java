/*
 * Copyright (c) OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.apache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
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
import com.opentext.ia.sdk.support.http.BinaryPart;
import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.HttpException;
import com.opentext.ia.sdk.support.http.Part;
import com.opentext.ia.sdk.support.http.ResponseFactory;
import com.opentext.ia.sdk.support.http.TextPart;
import com.opentext.ia.sdk.support.http.UriBuilder;
import com.opentext.ia.sdk.support.io.ByteArrayInputOutputStream;
import com.opentext.ia.sdk.support.io.IOStreams;
import com.opentext.ia.sdk.support.io.RuntimeIoException;


/**
 * Implementation of {@linkplain HttpClient} using the <a href="https://hc.apache.org/">Apache HttpComponents</a>
 * library.
 */
public class ApacheHttpClient implements HttpClient {

  private static final String MISSING_URI = "Missing URI";
  private static final int STATUS_CODE_RANGE_MIN = 200;
  private static final int STATUS_CODE_RANGE_MAX = 300;
  private static final int MAX_HTTP_CONNECTIONS = 50;
  private static final int DEFAULT_CONNECTIONS_PER_ROUTE = 50;
  private static final String NL = System.getProperty("line.separator");

  private final CloseableHttpClient client;
  private final ObjectMapper mapper;

  public ApacheHttpClient() {
    this(MAX_HTTP_CONNECTIONS, DEFAULT_CONNECTIONS_PER_ROUTE);
  }

  public ApacheHttpClient(String proxyHost, int proxyPort) {
    this(MAX_HTTP_CONNECTIONS, DEFAULT_CONNECTIONS_PER_ROUTE, proxyHost, proxyPort);
  }

  public ApacheHttpClient(int maxHttpConnections, int maxConnectionsPerRoute) {
    PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
    manager.setMaxTotal(maxHttpConnections);
    manager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
    client = HttpClients.custom()
      .setConnectionManager(manager)
      .build();
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public ApacheHttpClient(int maxHttpConnections, int maxConnectionsPerRoute, String proxyHost, int proxyPort) {
    PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
    manager.setMaxTotal(maxHttpConnections);
    manager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
    RequestConfig defaultRequestConfig = RequestConfig.custom()
      .setProxy(new HttpHost(proxyHost, proxyPort))
      .build();
    client = HttpClients.custom()
      .setConnectionManager(manager)
      .setDefaultRequestConfig(defaultRequestConfig)
      .build();
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public <T> T get(String uri, Collection<Header> headers, Class<T> type) throws IOException {
    return execute(newGet(uri, headers), type);
  }

  @Override
  public <T> T get(String uri, Collection<Header> headers, ResponseFactory<T> factory) throws IOException {
    return execute(newGet(uri, headers), factory);
  }

  private HttpGet newGet(String uri, Collection<Header> headers) {
    Objects.requireNonNull(uri, MISSING_URI);
    HttpGet result = new HttpGet(uri);
    setHeaders(result, headers);
    return result;
  }

  private void setHeaders(HttpRequestBase request, Collection<Header> headers) {
    headers.stream()
        .map(header -> new BasicHeader(header.getName(), header.getValue()))
        .forEach(request::addHeader);
  }

  protected <T> T execute(HttpRequestBase request, Class<T> type) throws IOException {
    Objects.requireNonNull(request, "Missing request");
    try {
      return client.execute(request, getResponseHandler(request.getMethod(), request.getURI().toString(),
          request.getAllHeaders(), type));
    } finally {
      request.releaseConnection();
    }
  }

  protected <T> T execute(HttpRequestBase request, ResponseFactory<T> factory) throws IOException {
    CloseableHttpResponse httpResponse = client.execute(request);
    Runnable closeResponse = () -> {
      IOStreams.close(httpResponse);
      request.releaseConnection();
    };
    boolean shouldCloseResponse = true;
    try {
      StatusLine statusLine = httpResponse.getStatusLine();
      if (!isOk(statusLine)) {
        throw requestFailed(request, httpResponse, statusLine);
      }
      T result = factory.create(new ApacheResponse(httpResponse), closeResponse);
      shouldCloseResponse = false;
      return result;
    } finally {
      if (shouldCloseResponse) {
        closeResponse.run();
      }
    }
  }

  private HttpException requestFailed(HttpRequestBase request, CloseableHttpResponse httpResponse,
      StatusLine statusLine) throws IOException {
    int statusCode = statusLine.getStatusCode();
    HttpEntity entity = httpResponse.getEntity();
    String body = toString(entity);
    String method = request.getMethod();
    URI uri = request.getURI();
    String reasonPhrase = statusLine.getReasonPhrase();
    return new HttpException(statusCode, String.format("%n%s %s%n==> %d %s%n%s", method, uri, statusCode,
        reasonPhrase, body));
  }

  private String toString(HttpEntity entity) throws IOException {
    return entity == null ? "" : EntityUtils.toString(entity);
  }

  <T> ResponseHandler<T> getResponseHandler(String method, String uri, org.apache.http.Header[] headers,
      Class<T> type) {
    return response -> {
      try {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        boolean isBinary = InputStream.class.equals(type);
        String body = isBinary ? "<binary>" : toString(entity);
        if (!isOk(statusLine)) {
          int status = statusLine.getStatusCode();
          throw new HttpException(status, String.format("%n%s %s%n%s==> %d %s%n%s%n%s", method, uri, headersToString(headers),
              status, statusLine.getReasonPhrase(), headersToString(response.getAllHeaders()), body));
        }
        return isBinary ? binaryResponse(entity, type) : textResponse(body, type);
      } finally {
        if (response instanceof CloseableHttpResponse) {
          IOStreams.close((CloseableHttpResponse)response);
        }
      }
    };
  }

  private String headersToString(org.apache.http.Header... headers) {
    if (headers == null) {
      return "";
    }
    return Arrays.stream(headers)
        .map(org.apache.http.Header::toString)
        .collect(Collectors.joining(NL));
  }

  private boolean isOk(StatusLine statusLine) {
    int status = statusLine.getStatusCode();
    return STATUS_CODE_RANGE_MIN <= status && status < STATUS_CODE_RANGE_MAX;
  }

  private <T> T binaryResponse(HttpEntity entity, Class<T> type) throws IOException {
    try (ByteArrayInputOutputStream output = new ByteArrayInputOutputStream()) {
      try (InputStream input = entity.getContent()) {
        IOUtils.copy(input, output);
      }
      return type.cast(output.getInputStream());
    }
  }

  @Nullable
  private <T> T textResponse(String body, Class<T> type) {
    if (type == null || body.isEmpty()) {
      return null;
    }
    if (type.equals(String.class)) {
      return type.cast(body);
    }
    try {
      return mapper.readValue(body, type);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  @Override
  public <T> T put(String uri, Collection<Header> headers, Class<T> type) throws IOException {
    return execute(newPut(uri, headers), type);
  }

  @Override
  public <T> T put(String uri, Collection<Header> headers, Class<T> type, String payload) throws IOException {
    HttpPut request = newPut(uri, headers);
    if (payload != null) {
      request.setEntity(new StringEntity(payload));
    }
    return execute(request, type);
  }

  private HttpPut newPut(String uri, Collection<Header> headers) {
    Objects.requireNonNull(uri, MISSING_URI);
    HttpPut result = new HttpPut(uri);
    setHeaders(result, headers);
    return result;
  }

  @Override
  public <T> T put(String uri, Collection<Header> headers, Class<T> type, InputStream payload) throws IOException {
    HttpPut request = newPut(uri, headers);
    if (payload != null) {
      request.setEntity(new InputStreamEntity(payload));
    }
    return execute(request, type);
  }

  @Override
  public <T> T put(String uri, Collection<Header> headers, Class<T> type, Part... parts) throws IOException {
    HttpPut request = newPut(uri, headers);
    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
    for (Part part : parts) {
      entityBuilder.addPart(part.getName(), newContentBody(part));
    }
    request.setEntity(entityBuilder.build());
    return execute(request, type);
  }

  @Override
  public <T> T post(String uri, Collection<Header> headers, Class<T> type, InputStream payload) throws IOException {
    HttpPost request = newPost(uri, headers);
    if (payload != null) {
      request.setEntity(new InputStreamEntity(payload));
    }
    return execute(request, type);
  }

  @Override
  public <T> T post(String uri, Collection<Header> headers, Class<T> type, String payload) throws IOException {
    HttpPost request = newPost(uri, headers);
    if (payload != null) {
      request.setEntity(new StringEntity(payload));
    }
    return execute(request, type);
  }

  private HttpPost newPost(String uri, Collection<Header> headers) {
    Objects.requireNonNull(uri, MISSING_URI);
    HttpPost result = new HttpPost(uri);
    setHeaders(result, headers);
    return result;
  }

  @Override
  public <T> T post(String uri, Collection<Header> headers, Class<T> type, Part... parts) throws IOException {
    HttpPost request = newPost(uri, headers);
    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
    for (Part part : parts) {
      entityBuilder.addPart(part.getName(), newContentBody(part));
    }
    request.setEntity(entityBuilder.build());
    return execute(request, type);
  }

  private ContentBody newContentBody(Part part) {
    ContentType contentType = ContentType.create(part.getMediaType());
    if (part instanceof TextPart) {
      TextPart textPart = (TextPart)part;
      return new StringBody(textPart.getText(), contentType);
    }
    if (part instanceof BinaryPart) {
      BinaryPart binaryPart = (BinaryPart)part;
      return new InputStreamBody(binaryPart.getData(), contentType, binaryPart.getDownloadName());
    }
    throw new IllegalArgumentException("Expected part type: " + part.getClass().getName());
  }

  @Override
  public <T> T delete(String uri, Collection<Header> headers, Class<T> type) throws IOException {
    HttpDelete request = new HttpDelete(uri);
    setHeaders(request, headers);
    return execute(request, type);
  }

  @Override
  public void close() {
    IOStreams.close(client);
  }

  @Override
  public UriBuilder uri(String baseUri) {
    return new ApacheUriBuilder(baseUri);
  }

}
