/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.io.RuntimeIoException;
import com.opentext.ia.sdk.support.test.TestCase;


public class WhenMakingJwtAuthentication extends TestCase {

  private static final String GATEWAY_URL = "http://authgateway.com/";

  private final String username = randomString();
  private final String password = randomString();
  private final String clientId = randomString();
  private final String clientSecret = randomString();
  private final HttpClient httpClient = mock(HttpClient.class);
  private final Clock clock = mock(Clock.class);
  private final GatewayInfo gatewayInfo = new GatewayInfo(GATEWAY_URL, clientId, clientSecret);
  private final AuthenticationStrategy authentication =
      new JwtAuthentication(username, password, gatewayInfo, httpClient, clock);
  private final String accessToken = randomString();
  private final String secondAccessToken = randomString();
  private final String refreshToken = randomString();
  private final AuthenticationSuccess authResult = new AuthenticationSuccess();
  private final AuthenticationSuccess authRefresh = new AuthenticationSuccess();
  private final Header authorizationHeader = new Header("Authorization", "Bearer " + accessToken);
  private final Header secondAuthorizationHeader = new Header("Authorization", "Bearer " + secondAccessToken);

  @Before
  public void init() throws IOException {
    authResult.setAccessToken(accessToken);
    authResult.setRefreshToken(refreshToken);
    authResult.setTokenType("Bearer");
    authResult.setExpiresIn(25);
    authRefresh.setAccessToken(secondAccessToken);
    authRefresh.setTokenType("Bearer");
    authRefresh.setExpiresIn(25);
    when(httpClient.post(anyString(), any(), eq(AuthenticationSuccess.class), anyString()))
        .thenReturn(authResult)
        .thenReturn(authRefresh);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailBecauseOfClientId() {
    String illegalClientId = "";
    new GatewayInfo(GATEWAY_URL, illegalClientId, clientSecret);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailBecauseOfClientSecret() {
    String illegalSecret = "";
    new GatewayInfo(GATEWAY_URL, clientId, illegalSecret);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailBecauseOfUsername() {
    String illegalUsername = "";
    new JwtAuthentication(illegalUsername, password, gatewayInfo, httpClient, clock);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailBecauseOfPassword() {
    String illegalPassword = "";
    new JwtAuthentication(username, illegalPassword, gatewayInfo, httpClient, clock);
  }

  @Test
  public void shouldCorrectlyFormatToken() {
    assertEquals("Should add prefix", authorizationHeader, authentication.issueAuthHeader());
  }

  @Test
  public void shouldCorrectlyFormUrl() throws IOException {
    authentication.issueAuthHeader();
    verify(httpClient).post(eq("http://authgateway.com/oauth/token"), any(), eq(AuthenticationSuccess.class),
        anyString());
  }

  @Test
  public void shouldCorrectlyFormAuthorizationHeader() throws IOException {
    String authToken = Base64.getEncoder()
                           .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    Header authHeader = new Header("Authorization", "Basic " + authToken);
    Header contentTypeHeader = new Header("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
    Collection<Header> headers = new ArrayList<>(Arrays.asList(authHeader, contentTypeHeader));
    authentication.issueAuthHeader();
    verify(httpClient).post(any(), eq(headers), eq(AuthenticationSuccess.class), anyString());
  }

  @Test
  public void shouldCorrectlyFormPayload() throws IOException {
    String payload = "grant_type=password&username=" + username + "&password=" + password;
    authentication.issueAuthHeader();
    verify(httpClient).post(any(), any(), eq(AuthenticationSuccess.class), eq(payload));
  }

  @Test
  public void shouldCorrectlySetRefreshingTime() throws IOException {
    authentication.issueAuthHeader();
    verify(clock)
        .schedule(any(), eq(TimeUnit.MILLISECONDS.convert(15, TimeUnit.SECONDS)), eq(TimeUnit.MILLISECONDS), any());
  }

  @Test
  public void shouldCorrectlySetLittleRefreshingTime() throws IOException {
    authResult.setExpiresIn(18);
    authentication.issueAuthHeader();
    verify(clock)
        .schedule(any(), eq(TimeUnit.MILLISECONDS.convert(9, TimeUnit.SECONDS)), eq(TimeUnit.MILLISECONDS), any());
  }

  @Test
  public void shouldChangeToken() throws IOException {
    final ArgumentCaptor<Runnable> taskArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
    authentication.issueAuthHeader();
    verify(clock).schedule(any(), anyLong(), any(), taskArgumentCaptor.capture());
    taskArgumentCaptor.getValue().run();
    assertEquals("Should be refreshed", secondAuthorizationHeader, authentication.issueAuthHeader());
  }

  @Test
  public void shouldFormCorrectPayloadToRefreshToken() throws IOException {
    final ArgumentCaptor<Runnable> taskArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
    String payload = "grant_type=refresh_token&refresh_token=" + refreshToken;
    authentication.issueAuthHeader();
    verify(clock).schedule(any(), anyLong(), any(), taskArgumentCaptor.capture());
    taskArgumentCaptor.getValue().run();
    verify(httpClient).post(any(), any(), eq(AuthenticationSuccess.class), eq(payload));
  }

  @Test
  public void shouldNotRefreshTokenIfNotExpiring() throws IOException {
    authResult.setExpiresIn(20);
    authentication.issueAuthHeader();
    assertEquals("Should not be refreshed", authorizationHeader, authentication.issueAuthHeader());
  }

  @Test(expected = RuntimeIoException.class)
  public void shouldFailWithRuntimeIoException() throws IOException {
    when(httpClient.post(any(), any(), eq(AuthenticationSuccess.class), anyString()))
        .thenThrow(new IOException());
    authentication.issueAuthHeader();
  }

}
