/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.sdk.support.http.UriBuilder;
import com.opentext.ia.test.TestCase;

class WhenMakingRestCalls extends TestCase {

  private final HttpClient httpClient = mock(HttpClient.class);
  private final String token = randomString();
  private final AuthenticationStrategy authentication = new NonExpiringTokenAuthentication(token);
  private final RestClient restClient = new RestClient(httpClient);

  @BeforeEach
  public void init() {
    restClient.init(authentication);
  }

  @Test
  void shouldForwardToHttpClient() throws IOException {
    String uri = randomString();
    Class<?> type = String.class;
    Collection<Header> authorizationHeader = new ArrayList<>();
    authorizationHeader.add(new Header("Authorization", "Bearer " + token));

    Collection<Header> contentAndAuthorizationHeaders = new ArrayList<>();
    contentAndAuthorizationHeaders.add(new Header("Accept", MediaTypes.HAL));
    contentAndAuthorizationHeaders.addAll(authorizationHeader);

    restClient.get(uri, type);
    verify(httpClient).get(eq(uri), eq(contentAndAuthorizationHeaders), eq(type));

    restClient.put(uri, type);
    verify(httpClient).put(eq(uri), eq(contentAndAuthorizationHeaders), eq(type));

    restClient.post(uri, type);
    verify(httpClient).post(eq(uri), eq(contentAndAuthorizationHeaders), eq(type));

    restClient.delete(uri);
    verify(httpClient).delete(eq(uri), eq(authorizationHeader));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldFollowLinkRelations() throws IOException {
    String relation = randomString();
    String uri = randomString();
    LinkContainer state = new LinkContainer();
    Link link = new Link();
    link.setHref(uri);
    state.getLinks().put(relation, link);
    Class<String> type = String.class;

    restClient.follow(state, relation, type);

    verify(httpClient).get(eq(uri), any(List.class), eq(type));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldRefreshState() throws IOException {
    String uri = randomString();
    LinkContainer state = new LinkContainer();
    Link link = new Link();
    link.setHref(uri);
    state.getLinks().put(StandardLinkRelations.LINK_SELF, link);

    restClient.refresh(state);

    verify(httpClient).get(eq(uri), any(List.class), eq(LinkContainer.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldCreateItemInCollection() throws IOException {
    String relation = randomString();
    String uri = randomString();
    LinkContainer collection = new LinkContainer();
    Link link = new Link();
    link.setHref(uri);
    collection.getLinks().put(relation, link);
    Foo expected = new Foo();
    expected.setBar(randomString());
    when(httpClient.post(eq(uri), any(List.class), eq(Foo.class), anyString()))
        .thenReturn(expected);

    Foo actual = restClient.createCollectionItem(collection, new Foo(), relation);

    assertSame(expected, actual, "Result");
  }

  @Test
  void shouldCloseHttpClient() {
    restClient.close();

    verify(httpClient).close();
  }

  @Test
  void shouldForwardUri() {
    UriBuilder expected = mock(UriBuilder.class);
    when(httpClient.uri(anyString())).thenReturn(expected);

    UriBuilder actual = restClient.uri(randomString());

    assertSame(expected, actual, "URI Builder");
  }

  @Test
  void shouldForwardWithNonDefaultAcceptHeader() throws IOException {
    String uri = randomString(32);
    Class<?> type = String.class;
    String mediaType = randomMediaType();

    Collection<Header> contentAndAuthorizationHeaders = new ArrayList<>();
    contentAndAuthorizationHeaders.add(new Header("Accept", mediaType));
    contentAndAuthorizationHeaders.add(new Header("Authorization", "Bearer " + token));

    restClient.get(uri, mediaType, type);
    verify(httpClient).get(eq(uri), eq(contentAndAuthorizationHeaders), eq(type));
  }

  private String randomMediaType() {
    return randomString(5) + '/' + randomString(8);
  }

  public static class Foo {

    private String bar;

    public String getBar() {
      return bar;
    }

    public void setBar(String bar) {
      this.bar = bar;
    }

  }
}
