/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.emc.ia.sdk.support.datetime.Clock;
import com.emc.ia.sdk.support.datetime.Timer;
import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.io.RuntimeIoException;

public final class JwtAuthentication implements AuthenticationStrategy {

  private static final long RESERVE_TIME = 10000;
  private static final long REFRESHING_TIME_BORDER = 20000;

  private final GatewayInfo gatewayInfo;
  private final String username;
  private final String password;
  private final HttpClient httpClient;
  private volatile AuthenticationSuccess authResult;
  private final Clock clock;
  private Timer timer;

  public static Optional<AuthenticationStrategy> optional(String username, String password, GatewayInfo gatewayInfo,
                                                          HttpClient httpClient, Clock clock) {
    if ((username == null) || (password == null) || (gatewayInfo == null) || (httpClient == null)) {
      return Optional.empty();
    } else {
      return Optional.of(new JwtAuthentication(
          username, password, gatewayInfo, httpClient, clock));
    }
  }

  public JwtAuthentication(String username, String password, GatewayInfo gatewayInfo,
                           HttpClient httpClient, Clock clock) {
    if (username.isEmpty()) {
      throw new IllegalArgumentException("Username is empty");
    } else {
      this.username = username;
    }
    if (password.isEmpty()) {
      throw new IllegalArgumentException("Password is empty");
    } else {
      this.password = password;
    }
    this.clock = Objects.requireNonNull(clock, "Missing clock");
    this.gatewayInfo = Objects.requireNonNull(gatewayInfo, "Missing gateway information");
    this.httpClient = Objects.requireNonNull(httpClient, "Missing HttpClient");
    this.authResult = null;
  }

  @Override
  public Header issueAuthHeader() {
    authResult = issueAuthentication();
    return new Header("Authorization", authResult.getToken_type() + " " + authResult.getAccess_token());
  }

  private AuthenticationSuccess issueAuthentication() {
    if (authResult == null) {
      AuthenticationSuccess firstResult = fetchAuthentication();
      startRefreshingTimer(TimeUnit.MILLISECONDS.convert(firstResult.getExpires_in(), TimeUnit.SECONDS));
      return firstResult;
    } else {
      return authResult;
    }
  }

  private AuthenticationSuccess fetchAuthentication() {
    String payload = "grant_type=password&username=" + username + "&password=" + password;
    return postToGateway(payload);
  }

  private void startRefreshingTimer(long expiresInMilliseconds) {
    if (expiresInMilliseconds > REFRESHING_TIME_BORDER) {
      timer = new Timer(expiresInMilliseconds - RESERVE_TIME, this::refreshAuthentication, clock);
    } else {
      timer = new Timer(expiresInMilliseconds / 2, this::refreshAuthentication, clock);
    }
  }

  private void refreshAuthentication() {
    String payload = "grant_type=refresh_token&refresh_token=" + authResult.getRefresh_token();
    authResult = postToGateway(payload);
  }

  private AuthenticationSuccess postToGateway(String payload) {
    String gatewayUrl = gatewayInfo.getGatewayUrl();
    Collection<Header> headers =
        new ArrayList<>(Arrays.asList(gatewayInfo.getAuthorizationHeader(), gatewayInfo.getContentTypeHeader()));
    AuthenticationSuccess authSuccess;
    try {
      authSuccess = httpClient.post(gatewayUrl, headers, AuthenticationSuccess.class, payload);
    } catch (IOException ex) {
      if (timer != null) {
        timer.stop();
      }
      throw new RuntimeIoException(ex);
    }
    return authSuccess;
  }
}
