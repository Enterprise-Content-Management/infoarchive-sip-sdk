/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.JsonFormatter;
import com.emc.ia.sdk.support.http.MediaTypes;
import com.emc.ia.sdk.support.http.Part;
import com.emc.ia.sdk.support.http.ResponseFactory;
import com.emc.ia.sdk.support.http.UriBuilder;

public class RestClient implements Closeable, StandardLinkRelations {

  private final HttpClient httpClient;
  private final JsonFormatter formatter = new JsonFormatter();
  private final Collection<Header> headers = new ArrayList<Header>();

  public RestClient(HttpClient client) {
    this.httpClient = Objects.requireNonNull(client, "Missing HTTP client");
  }

  public void init(String bearerToken) {
    headers.add(new Header("Authorization", "Bearer " + bearerToken));
    headers.add(new Header("Accept", MediaTypes.HAL));
  }

  public UriBuilder uri(String baseUri) {
    return httpClient.uri(baseUri);
  }

  public <T> T get(String uri, Class<T> type) throws IOException {
    return httpClient.get(uri, headers, type);
  }

  public <T> T get(String uri, ResponseFactory<T> factory) throws IOException {
    return httpClient.get(uri, headers, factory);
  }

  public <T> T put(String uri, Class<T> type) throws IOException {
    return httpClient.put(uri, headers, type);
  }

  public <T> T post(String uri, Class<T> type, Part... parts) throws IOException {
    return httpClient.post(uri, headers, type, parts);
  }

  public <T> T follow(LinkContainer state, String relation, Class<T> type) throws IOException {
    Objects.requireNonNull(state, "Missing state");
    return get(linkIn(state, relation).getHref(), type);
  }

  private Link linkIn(LinkContainer state, String relation) {
    Link result = state.getLinks()
      .get(relation);
    Objects.requireNonNull(result, String.format("Missing link %s in %s", relation, state));
    return result;
  }

  @SuppressWarnings("unchecked")
  public <T extends LinkContainer> T refresh(T state) throws IOException {
    return (T)follow(state, LINK_SELF, state.getClass());
  }

  @SuppressWarnings("unchecked")
  public <T> T createCollectionItem(LinkContainer collection, String addLinkRelation, T item) throws IOException {
    String uri = linkIn(collection, addLinkRelation).getHref();
    T result = (T)httpClient.post(uri, withContentType(MediaTypes.HAL), toJson(item), item.getClass());
    Objects.requireNonNull(result, String.format("Could not create item in %s%n%s", uri, item));
    return result;
  }

  public Collection<Header> withContentType(String contentType) {
    Collection<Header> result = new ArrayList<>(headers);
    result.add(new Header("Content-Type", contentType));
    return result;
  }

  private String toJson(Object object) throws IOException {
    return formatter.format(object);
  }

  @Override
  public void close() {
    httpClient.close();
  }

}
