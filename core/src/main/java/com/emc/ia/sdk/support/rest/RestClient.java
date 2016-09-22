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
  private final AuthenticationStrategy authentication;
  private final JsonFormatter formatter = new JsonFormatter();
  private final Collection<Header> headers = new ArrayList<Header>();
  private final Collection<Header> headersNoFormat = new ArrayList<Header>();

  public RestClient(HttpClient client, AuthenticationStrategy authentication) {
    this.httpClient = Objects.requireNonNull(client, "Missing HTTP client");
    this.authentication = Objects.requireNonNull(authentication, "Missing Authentication strategy");
  }

  public void init() {
    headers.add(new Header("Accept", MediaTypes.HAL));
  }

  public UriBuilder uri(String baseUri) {
    return httpClient.uri(baseUri);
  }

  public <T> T get(String uri, Class<T> type) throws IOException {
    return httpClient.get(uri, withAuthorization(headers), type);
  }

  public <T> T get(String uri, ResponseFactory<T> factory) throws IOException {
    return httpClient.get(uri, withAuthorization(headersNoFormat), factory);
  }

  public <T> T put(String uri, Class<T> type) throws IOException {
    return httpClient.put(uri, withAuthorization(headers), type);
  }

  public <S, T> T put(String uri, Class<T> type, S payload) throws IOException {
    return httpClient.put(uri, withAuthorization(withContentType(MediaTypes.HAL)), type, toJson(payload));
  }

  public <T> T post(String uri, Class<T> type, Part... parts) throws IOException {
    return httpClient.post(uri, withAuthorization(headers), type, parts);
  }

  public <T> T post(String uri, String data, Class<T> type) throws IOException {
    return httpClient.post(uri, withAuthorization(withContentType(MediaTypes.HAL)), toJson(data), type);
  }

  public <T> T follow(LinkContainer state, String relation, Class<T> type) throws IOException {
    Objects.requireNonNull(state, "Missing state");
    return get(linkIn(state, relation).getHref(), type);
  }

  private Link linkIn(LinkContainer state, String... relations) {
    Link result = null;
    for (String relation : relations) {
      result = state.getLinks()
        .get(relation);
      if (result != null) {
        break;
      }
    }
    Objects.requireNonNull(result, String.format("Missing link %s in %s", relations[relations.length - 1], state));
    return result;
  }

  @SuppressWarnings("unchecked")
  public <T extends LinkContainer> T refresh(T state) throws IOException {
    return (T)follow(state, LINK_SELF, state.getClass());
  }

  @SuppressWarnings("unchecked")
  public <T> T createCollectionItem(LinkContainer collection, T item, String... addLinkRelations) throws IOException {
    String uri = linkIn(collection, addLinkRelations).getHref();
    T result = (T)httpClient.post(uri, withContentType(MediaTypes.HAL), toJson(item), item.getClass());
    Objects.requireNonNull(result, String.format("Could not create item in %s%n%s", uri, item));
    return result;
  }

  public Collection<Header> withContentType(String contentType) {
    Collection<Header> result = new ArrayList<>(headers);
    result.add(new Header("Content-Type", contentType));
    return result;
  }

  private Collection<Header> withAuthorization(Collection<Header> givenHeaders) {
    Collection<Header> updated = new ArrayList<>(givenHeaders);
    updated.add(authentication.issueAuthHeader());
    return updated;
  }

  private String toJson(Object object) throws IOException {
    return formatter.format(object);
  }

  @Override
  public void close() {
    httpClient.close();
  }

}
