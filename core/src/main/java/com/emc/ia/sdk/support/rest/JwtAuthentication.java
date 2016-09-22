/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.io.RuntimeIoException;

public final class JwtAuthentication implements AuthenticationStrategy {

  private final GatewayInfo gatewayInfo;
  private final String username;
  private final String password;
  private final HttpClient httpClient;
  private final int expirationThreshold;
  private AuthenticationSuccess authResult;
  private long lastIssuingTime;

  public static Optional<AuthenticationStrategy> of(String username, String password, GatewayInfo gatewayInfo,
                                                    int expirationThreshold, HttpClient httpClient) {
    if ((username == null) || (password == null) || (gatewayInfo == null) || (httpClient == null)) {
      return Optional.empty();
    } else {
      return Optional.of(new JwtAuthentication(
          username, password, gatewayInfo, expirationThreshold, httpClient));
    }
  }

  public JwtAuthentication(String username, String password, GatewayInfo gatewayInfo, int expirationThreshold, HttpClient httpClient) {
    this.username = Objects.requireNonNull(username, "Missing username");
    this.password = Objects.requireNonNull(password, "Missing password");
    this.gatewayInfo = Objects.requireNonNull(gatewayInfo, "Missing gateway information");
    this.httpClient = Objects.requireNonNull(httpClient, "Missing HttpClient");
    this.expirationThreshold = expirationThreshold;
    this.authResult = null;
    this.lastIssuingTime = 0;
  }

  @Override
  public Header issueAuthHeader() {
    authResult = issueAuthentication();
    return new Header("Authorization", authResult.getTokenType() + " " + authResult.getAccessToken());
  }

  private AuthenticationSuccess issueAuthentication() {
    if (authResult == null) {
      return fetchAuthentication();
    } else {
      return tryRefreshAuthentication();
    }
  }

  private AuthenticationSuccess fetchAuthentication() {
    String payload = "grant_type=password&username=" + username + "&password=" + password;
    return postToGateway(payload);
  }

  private AuthenticationSuccess tryRefreshAuthentication() {
    if (isTokenExpiring()) {
      String payload = "grant_type=refresh_token&refresh_token=" + authResult.getRefreshToken();
      return postToGateway(payload);
    } else {
      return authResult;
    }
  }

  private boolean isTokenExpiring() {
    return authResult.getExpiresIn() - (System.currentTimeMillis() / 1000000 - lastIssuingTime) < expirationThreshold;
  }

  private AuthenticationSuccess postToGateway(String payload) {
    String gatewayUrl = gatewayInfo.getGatewayUrl();
    Collection<Header> headers = Collections.singletonList(gatewayInfo.getAuthorizationHeader());
    AuthenticationSuccess authSuccess;
    try {
      authSuccess = httpClient.post(gatewayUrl, headers, payload, AuthenticationSuccess.class);
      lastIssuingTime = System.currentTimeMillis() / 1000000;
    } catch (IOException ex) {
      throw new RuntimeIoException(ex);
    }
    return authSuccess;
  }
}
