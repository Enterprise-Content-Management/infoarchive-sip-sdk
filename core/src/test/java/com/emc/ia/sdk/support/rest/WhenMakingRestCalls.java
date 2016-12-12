/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.MediaTypes;
import com.emc.ia.sdk.support.http.UriBuilder;
import com.emc.ia.sdk.support.http.apache.WhenMakingHttpCallsUsingApache.Foo;
import com.emc.ia.sdk.support.test.TestCase;

public class WhenMakingRestCalls extends TestCase {

  private final HttpClient httpClient = mock(HttpClient.class);
  private final String token = randomString();
  private final AuthenticationStrategy authentication = new NonExpiringTokenAuthentication(token);
  private final RestClient restClient = new RestClient(httpClient);

  @Before
  public void init() {
    restClient.init(authentication);
  }

  @Test
  public void shouldForwardToHttpClient() throws IOException {
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
  public void shouldFollowLinkRelations() throws IOException {
    String relation = randomString();
    String uri = randomString();
    LinkContainer state = new LinkContainer();
    Link link = new Link();
    link.setHref(uri);
    state.getLinks()
      .put(relation, link);
    Class<String> type = String.class;

    restClient.follow(state, relation, type);

    verify(httpClient).get(eq(uri), any(List.class), eq(type));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldRefreshState() throws IOException {
    String uri = randomString();
    LinkContainer state = new LinkContainer();
    Link link = new Link();
    link.setHref(uri);
    state.getLinks()
      .put(StandardLinkRelations.LINK_SELF, link);

    restClient.refresh(state);

    verify(httpClient).get(eq(uri), any(List.class), eq(LinkContainer.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldCreateItemInCollection() throws IOException {
    String relation = randomString();
    String uri = randomString();
    LinkContainer collection = new LinkContainer();
    Link link = new Link();
    link.setHref(uri);
    collection.getLinks()
      .put(relation, link);
    Foo expected = new Foo();
    expected.setBar(randomString());
    when(httpClient.post(eq(uri), any(List.class), eq(Foo.class), anyString())).thenReturn(expected);

    Foo actual = restClient.createCollectionItem(collection, new Foo(), relation);

    assertSame("Result", expected, actual);
  }

  @Test
  public void shouldCloseHttpClient() {
    restClient.close();

    verify(httpClient).close();
  }

  @Test
  public void shouldForwardUri() {
    UriBuilder expected = mock(UriBuilder.class);
    when(httpClient.uri(anyString())).thenReturn(expected);

    UriBuilder actual = restClient.uri(randomString());

    assertSame("URI Builder", expected, actual);
  }

}
