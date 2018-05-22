/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import com.opentext.ia.sdk.support.http.BinaryPart;
import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.JsonFormatter;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.sdk.support.http.Part;
import com.opentext.ia.sdk.support.http.ResponseFactory;
import com.opentext.ia.sdk.support.http.UriBuilder;


/**
 * An HTTP client that understands hyperlinks.
 */
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
    headers.add(new Header(Header.ACCEPT, MediaTypes.HAL));
    this.authentication = Objects.requireNonNull(auth, "Missing Authentication strategy");
  }

  public UriBuilder uri(String baseUri) {
    return httpClient.uri(baseUri);
  }

  public <T> T get(String uri, Class<T> type) throws IOException {
    return httpClient.get(uri, withAuthorization(headers), type);
  }

  public <T> T get(String uri, String mediaType, Class<T> type) throws IOException {
    return httpClient.get(uri, withAuthorization(withAccept(mediaType)), type);
  }

  private Collection<Header> withAccept(String mediaType) {
    Collection<Header> result = headers.stream()
        .filter(header -> !Header.ACCEPT.equals(header.getName()))
        .collect(Collectors.toList());
    result.add(new Header(Header.ACCEPT, mediaType));
    return result;
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

  public <T> T put(String uri, Class<T> type, Part... parts) throws IOException {
    return httpClient.put(uri, withAuthorization(headers), type, parts);
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
    result.add(new Header(Header.CONTENT_TYPE, contentType));
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

  public <T> T upload(LinkContainer state, String linkRelation, File file, Class<T> type) throws IOException {
    return upload(state, linkRelation, file, type, "file");
  }

  public <T> T upload(LinkContainer state, String linkRelation, File file, Class<T> type, String partName)
      throws IOException {
    try (InputStream data = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
      return upload(state, linkRelation, data, type, partName);
    }
  }

  public <T> T upload(LinkContainer state, String linkRelation, InputStream data, Class<T> type, String partName)
      throws IOException {
    Link link = linkIn(state, linkRelation);
    return post(link.getHref(), type, new BinaryPart(partName, data, ""));
  }

  @Override
  public void close() {
    httpClient.close();
  }

}
