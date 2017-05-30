/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.rest;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.opentext.ia.sdk.support.http.*;


public class RestClient implements Closeable, StandardLinkRelations {

  private final JsonFormatter formatter = new JsonFormatter();
  private final Collection<Header> headers = new ArrayList<>();
  private final Collection<Header> headersNoFormat = new ArrayList<>();
  private final HttpClient httpClient;
  private AuthenticationStrategy authentication;

  public RestClient(HttpClient client) {
    this.httpClient = Objects.requireNonNull(client, "Missing HTTP client");
  }

  public void init(AuthenticationStrategy auth) {
    headers.add(new Header("Accept", MediaTypes.HAL));
    this.authentication = Objects.requireNonNull(auth, "Missing Authentication strategy");
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

  public <T> T put(String uri, Class<T> type, String payload) throws IOException {
    return put(uri, type, payload, MediaTypes.HAL);
  }

  public <T> T put(String uri, Class<T> type, String payload, String contentType) throws IOException {
    return httpClient.put(uri, withAuthorization(withContentType(contentType)), type, payload);
  }

  public <S, T> T put(String uri, Class<T> type, S payload) throws IOException {
    return put(uri, type, toJson(payload));
  }

  public <S, T> T post(String uri, Class<T> type, S payload) throws IOException {
    if (payload instanceof Part) {
      return httpClient.post(uri, withAuthorization(headers), type, (Part)payload);
    } else {
      return post(uri, type, toJson(payload));
    }
  }

  public <T> T post(String uri, Class<T> type, String data) throws IOException {
    return post(uri, type, data, MediaTypes.HAL);
  }

  public <T> T post(String uri, Class<T> type, Part... parts) throws IOException {
    return httpClient.post(uri, withAuthorization(headers), type, parts);
  }

  public <T> T post(String uri, Class<T> type, String data, String contentType) throws IOException {
    return httpClient.post(uri, withAuthorization(withContentType(contentType)), type, data);
  }

  public void delete(String uri) throws IOException {
    httpClient.delete(uri, withAuthorization(Collections.emptyList()));
  }

  public <T> T follow(LinkContainer state, String relation, Class<T> type) throws IOException {
    Objects.requireNonNull(state, "Missing state");
    return get(linkIn(state, relation).getHref(), type);
  }

  private Link linkIn(LinkContainer state, String... relations) {
    Link result = null;
    for (String relation : relations) {
      result = state.getLinks().get(relation);
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
    T result = (T)httpClient.post(uri, withAuthorization(withContentType(MediaTypes.HAL)), item.getClass(),
        toJson(item));
    Objects.requireNonNull(result, String.format("Could not create item in %s%n%s", uri, item));
    return result;
  }

  public Collection<Header> withContentType(String contentType) {
    Collection<Header> result = new ArrayList<>(headers);
    result.add(new Header("Content-Type", contentType));
    return result;
  }

  private Collection<Header> withAuthorization(Collection<Header> givenHeaders) {
    Collection<Header> result = new ArrayList<>(givenHeaders);
    if (authentication != null) {
      result.add(authentication.issueAuthHeader());
    }
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
