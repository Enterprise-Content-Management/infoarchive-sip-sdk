/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http.apache;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
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

import com.emc.ia.sdk.support.http.BinaryPart;
import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.HttpException;
import com.emc.ia.sdk.support.http.Part;
import com.emc.ia.sdk.support.http.ResponseFactory;
import com.emc.ia.sdk.support.http.TextPart;
import com.emc.ia.sdk.support.http.UriBuilder;
import com.emc.ia.sdk.support.io.ByteArrayInputOutputStream;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApacheHttpClient implements HttpClient {

  private static final int STATUS_CODE_RANGE_MIN = 200;
  private static final int STATUS_CODE_RANGE_MAX = 300;
  private static final int MAX_HTTP_CONNECTIONS = 50;
  private static final int DEFAULT_CONNECTIONS_PER_ROUTE = 50;

  private final CloseableHttpClient client;
  private final ObjectMapper mapper;

  public ApacheHttpClient() {
    this(MAX_HTTP_CONNECTIONS, DEFAULT_CONNECTIONS_PER_ROUTE);
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

  @Override
  public <T> T get(String uri, Collection<Header> headers, Class<T> type) throws IOException {
    return execute(newGet(uri, headers), type);
  }

  @Override
  public <T> T get(String uri, Collection<Header> headers, ResponseFactory<T> factory) throws IOException {
    return execute(newGet(uri, headers), factory);
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

  protected <T> T execute(HttpRequestBase request, Class<T> type) throws IOException {
    Objects.requireNonNull(request, "Missing request");
    try {
      return client.execute(request, getResponseHandler(request.getMethod(), request.getURI()
        .toString(), type));
    } catch (HttpResponseException e) {
      throw new HttpException(e.getStatusCode(), e);
    } catch (HttpException e) { // NOPMD AvoidRethrowingException
      throw e;
    } catch (IOException e) {
      throw new HttpException(500, e);
    } finally {
      request.releaseConnection();
    }
  }

  protected <T> T execute(HttpRequestBase request, ResponseFactory<T> factory) throws IOException {
    CloseableHttpResponse httpResponse;
    try {
      httpResponse = client.execute(request);
    } catch (HttpResponseException e) {
      throw new HttpException(e.getStatusCode(), e);
    } catch (HttpException e) { // NOPMD AvoidRethrowingException
      throw e;
    } catch (IOException e) {
      throw new HttpException(500, e);
    }
    Runnable closeResponse = () -> {
      IOUtils.closeQuietly(httpResponse);
      request.releaseConnection();
    };
    boolean shouldCloseResponse = true;
    try {
      StatusLine statusLine = httpResponse.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      if (!isOk(statusCode)) {
        HttpEntity entity = httpResponse.getEntity();
        String body = entity == null ? "" : EntityUtils.toString(entity);
        String method = request.getMethod();
        URI uri = request.getURI();
        String reasonPhrase = statusLine.getReasonPhrase();
        throw new HttpException(statusCode, String.format("%n%s %s%n==> %d %s%n%s", method, uri, statusCode,
            reasonPhrase, body));
      }
      T result = factory.create(new ApacheResponse(httpResponse), closeResponse);
      shouldCloseResponse = false;
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (shouldCloseResponse) {
        closeResponse.run();
      }
    }
  }

  <T> ResponseHandler<T> getResponseHandler(String method, String uri, Class<T> type) {
    return response -> {
      StatusLine statusLine = response.getStatusLine();
      int status = statusLine.getStatusCode();
      HttpEntity entity = response.getEntity();
      boolean isBinary = InputStream.class.equals(type);
      String body = isBinary ? "<binary>" : entity == null ? "" : EntityUtils.toString(entity);
      if (!isOk(status)) {
        throw new HttpException(status,
            String.format("%n%s %s%n==> %d %s%n%s", method, uri, status, statusLine.getReasonPhrase(), body));
      }
      return isBinary ? binaryResponse(entity, type) : textResponse(body, type);
    };
  }

  private boolean isOk(int status) {
    return STATUS_CODE_RANGE_MIN <= status && status < STATUS_CODE_RANGE_MAX;
  }

  private <T> T binaryResponse(HttpEntity entity, Class<T> type) throws IOException {
    ByteArrayInputOutputStream result = new ByteArrayInputOutputStream();
    IOUtils.copy(entity.getContent(), result);
    return type.cast(result.getInputStream());
  }

  private <T> T textResponse(String body, Class<T> type) {
    if (type == null) {
      return null;
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
    Objects.requireNonNull(uri, "Missing URI");
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
    Objects.requireNonNull(uri, "Missing URI");
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
    throw new IllegalArgumentException("Unhandled part type: " + part.getClass()
      .getName());
  }

  @Override
  public <T> T delete(String uri, Collection<Header> headers, Class<T> type) throws IOException {
    HttpDelete request = new HttpDelete(uri);
    setHeaders(request, headers);
    return execute(request, type);
  }

  @Override
  public void close() {
    IOUtils.closeQuietly(client);
  }

  @Override
  public UriBuilder uri(String baseUri) {
    return new ApacheUriBuilder(baseUri);
  }

}
