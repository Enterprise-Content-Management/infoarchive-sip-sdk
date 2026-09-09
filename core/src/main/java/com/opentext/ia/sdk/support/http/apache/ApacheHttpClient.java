/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.apache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.InputStreamBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;

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

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;


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
  private static final String NL = System.lineSeparator();

  private final CloseableHttpClient client;
  private final JsonMapper mapper;

  public ApacheHttpClient() {
    this(MAX_HTTP_CONNECTIONS, DEFAULT_CONNECTIONS_PER_ROUTE);
  }

  public ApacheHttpClient(String proxyHost, int proxyPort) {
    this(MAX_HTTP_CONNECTIONS, DEFAULT_CONNECTIONS_PER_ROUTE, proxyHost, proxyPort);
  }

  public ApacheHttpClient(int maxHttpConnections, int maxConnectionsPerRoute) {
    // Closing is handled by the client
    PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(); // NOPMD
    manager.setMaxTotal(maxHttpConnections);
    manager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
    client = HttpClients.custom()
      .setConnectionManager(manager)
      .build();
    mapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // added if you want byte-identical output to the old Jackson 2 behavior
            // otherwise it will use the new behavior of writing dates as ISO-8601 strings
            .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            .build();
  }

  public ApacheHttpClient(int maxHttpConnections, int maxConnectionsPerRoute, String proxyHost, int proxyPort) {
    // Closing is handled by the client
    PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(); // NOPMD
    manager.setMaxTotal(maxHttpConnections);
    manager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
    RequestConfig defaultRequestConfig = RequestConfig.custom()
      .setProxy(new HttpHost(proxyHost, proxyPort))
      .build();
    client = HttpClients.custom()
      .setConnectionManager(manager)
      .setDefaultRequestConfig(defaultRequestConfig)
      .build();
    mapper = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();
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

  private void setHeaders(ClassicHttpRequest request, Collection<Header> headers) {
    headers.stream()
        .map(header -> new BasicHeader(header.getName(), header.getValue()))
        .forEach(request::addHeader);
  }

  protected <T> T execute(ClassicHttpRequest request, Class<T> type) throws IOException {
    Objects.requireNonNull(request, "Missing request");
    return client.execute(request, getResponseHandler(
            request.getMethod(),
            request.getRequestUri(),
            request.getHeaders(),
            type));
  }

  protected <T> T execute(ClassicHttpRequest request, ResponseFactory<T> factory) throws IOException {
    return client.execute(request, response -> {
      int statusCode = response.getCode();
      String reasonPhrase = response.getReasonPhrase();

      if (!isOk(statusCode)) {
        throw requestFailed(request, response, statusCode, reasonPhrase);
      }

      return factory.create(new ApacheResponse(response), () -> { });
    });
  }

  private HttpException requestFailed(ClassicHttpRequest request, HttpEntityContainer httpResponse,
      int statusCode, String reasonPhrase) throws IOException {
    String method = request.getMethod();
    String uri = request.getRequestUri();
    HttpEntity entity = httpResponse.getEntity(); // NOPMD - consumed in finally
    String body;

    try {
      body = entity == null ? "" : EntityUtils.toString(entity);
    } catch (ParseException e) {
      body = "exception caught - body is unparseable";
    } finally {
      EntityUtils.consumeQuietly(entity);
    }

    return new HttpException(statusCode, String.format("%n%s %s%n==> %d %s%n%s",
            method, uri, statusCode, reasonPhrase, body));
  }

  private String toString(HttpEntity entity) throws IOException, ParseException {
    return entity == null ? "" : EntityUtils.toString(entity);
  }

  <T> HttpClientResponseHandler<T> getResponseHandler(String method, String uri,
        org.apache.hc.core5.http.Header[] headers, Class<T> type) {

    return response -> {
      int statusCode = response.getCode();
      boolean isBinary = InputStream.class.equals(type);
      String body;

      try (HttpEntity entity = response.getEntity()) {
         body = isBinary ? "<binary>" : toString(entity);

         if (!isOk(statusCode)) {
           throw new HttpException(statusCode, String.format(
                   "%n%s %s%n%s==> %d %s%n%s%n%s",
                   method,
                   uri,
                   headersToString(headers),
                   statusCode,
                   response.getReasonPhrase(),
                   headersToString(response.getHeaders()),
                   body));
         }
        return isBinary ? binaryResponse(entity, type) : textResponse(body, type);
      }
    };
  }

  private String headersToString(org.apache.hc.core5.http.Header... headers) {
    if (headers == null) {
      return "";
    }
    return Arrays.stream(headers)
        .map(org.apache.hc.core5.http.Header::toString)
        .collect(Collectors.joining(NL));
  }

  private boolean isOk(int status) {
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
  private <T> T textResponse(String body, Class<T> type) throws HttpException {
    if (type == null || body.isEmpty()) {
      return null;
    }
    if (type.equals(String.class)) {
      return type.cast(body);
    }
    try {
      return mapper.readValue(body, type);
    } catch (JacksonException e) {
      throw new HttpException(0, e);
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
      request.setEntity(new InputStreamEntity(payload, -1, ContentType.APPLICATION_OCTET_STREAM));
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
      request.setEntity(new InputStreamEntity(payload, -1, ContentType.APPLICATION_OCTET_STREAM));
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
    if (part instanceof TextPart textPart) {
        return new StringBody(textPart.getText(), contentType);
    }
    if (part instanceof BinaryPart binaryPart) {
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
