/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.test.TestCase;

public class WhenMakingJwtAuthentication extends TestCase {

  private final String username = randomString();
  private final String password = randomString();
  private final String clientId = randomString();
  private final String clientSecret = randomString();
  private final HttpClient httpClient = mock(HttpClient.class);
  private final GatewayInfo gatewayInfo = new GatewayInfo("http://authgateway.com/", clientId, clientSecret);
  private final AuthenticationStrategy authentication =
      new JwtAuthentication(username, password, gatewayInfo, 5, httpClient);
  private final String accessToken = randomString();
  private final String secondAccessToken = randomString();
  private final String refreshToken = randomString();
  private final String secondRefreshToken = randomString();
  private final AuthenticationSuccess authResult = new AuthenticationSuccess();
  private final AuthenticationSuccess authRefresh = new AuthenticationSuccess();
  private final Header authorizationHeader = new Header("Authorization", "Bearer " + accessToken);
  private final Header secondAuthorizationHeader = new Header("Authorization", "Bearer " + secondAccessToken);

  @Before
  public void init() throws IOException {
    authResult.setAccessToken(accessToken);
    authResult.setRefreshToken(refreshToken);
    authResult.setTokenType("Bearer");
    authResult.setExpiresIn(4);
    authRefresh.setAccessToken(secondAccessToken);
    authRefresh.setRefreshToken(secondRefreshToken);
    authRefresh.setTokenType("Bearer");
    authRefresh.setExpiresIn(4);
    when(httpClient.post(any(), any(), any(), eq(AuthenticationSuccess.class)))
        .thenReturn(authResult)
        .thenReturn(authRefresh);
  }

  @Test
  public void shouldCorrectlyFormatToken() {
    assertEquals("Should add prefix", authorizationHeader, authentication.issueAuthHeader());
  }

  @Test
  public void shouldCorrectlyFormUrl() throws IOException {
    authentication.issueAuthHeader();
    verify(httpClient).post(eq("http://authgateway.com/oauth/token"), any(), any(), eq(AuthenticationSuccess.class));
  }

  @Test
  public void shouldCorrectlyFormAuthorizationHeader() throws IOException {
    String authToken = Base64.getEncoder()
                           .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    Collection<Header> headers = Collections.singletonList(new Header("Authorization", "Basic " + authToken));
    authentication.issueAuthHeader();
    verify(httpClient).post(any(), eq(headers), any(), eq(AuthenticationSuccess.class));
  }

  @Test
  public void shouldCorrectlyFormPayload() throws IOException {
    String payload = "grant_type=password&username=" + username + "&password=" + password;
    authentication.issueAuthHeader();
    verify(httpClient).post(any(), any(), eq(payload), eq(AuthenticationSuccess.class));
  }

  @Test
  public void shouldChangeToken() throws IOException {
    authentication.issueAuthHeader();
    assertEquals("Should be refreshed", secondAuthorizationHeader, authentication.issueAuthHeader());
  }

  @Test
  public void shouldFormCorrectPayloadToRefreshToken() throws IOException {
    String payload = "grant_type=refresh_token&refresh_token=" + refreshToken;
    authentication.issueAuthHeader();
    authentication.issueAuthHeader();
    verify(httpClient).post(any(), any(), eq(payload), eq(AuthenticationSuccess.class));
  }

  @Test
  public void shouldNotRefreshTokenIfNotExpiring() throws IOException {
    authResult.setExpiresIn(20);
    authentication.issueAuthHeader();
    assertEquals("Should not be refreshed", authorizationHeader, authentication.issueAuthHeader());
  }

  @Test(expected = RuntimeIoException.class)
  public void shouldFailWithRuntimeIoException() throws IOException {
    when(httpClient.post(any(), any(), any(), eq(AuthenticationSuccess.class)))
        .thenThrow(new IOException());
    authentication.issueAuthHeader();
  }

}
