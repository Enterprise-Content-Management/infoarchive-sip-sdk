/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;


public class RestClient implements Closeable {

  private final HttpClient httpClient;

  public RestClient(HttpClient client) {
    this.httpClient = Objects.requireNonNull(client, "Missing HTTP client");
  }

  public <T> T get(String uri, List<Header> headers, final Class<T> type) throws IOException {
    return httpClient.execute(httpClient.httpGetRequest(uri, headers), type);
  }

  @Override
  public void close() {
    httpClient.close();
  }

  public <T> T post(String uri, List<Header> headers, String body, InputStream attachment, Class<T> type)
      throws IOException {
    HttpPost postRequest = httpClient.httpPostRequest(uri, headers);

    // TODO - what should be the file name here ? IASIP.zip is Ok ?
    InputStreamBody file = new InputStreamBody(attachment, ContentType.APPLICATION_OCTET_STREAM, "IASIP.zip");
    HttpEntity entity = MultipartEntityBuilder.create()
        .addTextBody("format", "sip_zip")
        .addPart("sip", file)
        .build();
    postRequest.setEntity(entity);
    return httpClient.execute(postRequest, type);
  }

  public <T> T put(String uri, List<Header> headers, Class<T> type) throws IOException {
    return httpClient.execute(httpClient.httpPutRequest(uri, headers), type);
  }

}
