/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


import org.apache.commons.lang3.StringUtils;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.datetime.DefaultClock;
import com.opentext.ia.sdk.support.datetime.Timer;
import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.io.RuntimeIoException;


/**
 * Authenticate using a <a href="https://tools.ietf.org/html/rfc7519">JSON Web Token</a> (JWT).
 */
public final class JwtAuthentication implements AuthenticationStrategy {

  private static final long RESERVE_TIME = 10_000;
  private static final long REFRESHING_TIME_BORDER = 20_000;

  private final GatewayInfo gatewayInfo;
  private final String userName;
  private final String password;
  private final String scopes;
  private final HttpClient httpClient;
  private final Clock clock;
  private AuthenticationSuccess authenticationResult;
  private Timer timer;

  public static Optional<AuthenticationStrategy> optional(String username, String password, String scopes, GatewayInfo gatewayInfo,
      HttpClient httpClient, Clock clock) {
    if (username == null || password == null || gatewayInfo == null || httpClient == null) {
      return Optional.empty();
    } else {
      return Optional.of(new JwtAuthentication(username, password, scopes, gatewayInfo, httpClient, clock));
    }
  }

  public static Optional<AuthenticationStrategy> optional(String username, String password, GatewayInfo gatewayInfo,
      HttpClient httpClient, Clock clock) {
        return optional(username, password, null, gatewayInfo, httpClient, clock);
  }

  public JwtAuthentication(String userName, String password, String scopes, GatewayInfo gatewayInfo, HttpClient httpClient,
      Clock clock) {
    this.userName = requireNonEmpty(userName, "Missing user name");
    this.password = requireNonEmpty(password, "Missing password");
    this.gatewayInfo = Objects.requireNonNull(gatewayInfo, "Missing gateway information");
    this.httpClient = Objects.requireNonNull(httpClient, "Missing HttpClient");
    this.clock = Objects.requireNonNull(clock, "Missing clock");
    this.scopes = scopes;
    this.authenticationResult = null;
  }

  public JwtAuthentication(String userName, String password, GatewayInfo gatewayInfo, HttpClient httpClient,
      Clock clock) {
    this(userName, password, null, gatewayInfo, httpClient, clock);
  }

  public JwtAuthentication(String userName, String password, String scopes, GatewayInfo gatewayInfo, HttpClient httpClient) {
    this(userName, password, scopes, gatewayInfo, httpClient, new DefaultClock());
  }

  public JwtAuthentication(String userName, String password, GatewayInfo gatewayInfo, HttpClient httpClient) {
    this(userName, password, null, gatewayInfo, httpClient, new DefaultClock());
  }


  private String requireNonEmpty(String value, String message) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  @Override
  public Header issueAuthHeader() {
    try {
      authenticationResult = issueAuthentication();
    } catch (UnsupportedEncodingException ue) {
      throw new RuntimeIoException(ue);
    }

    return new Header("Authorization", authenticationResult.getTokenType() + " "
        + authenticationResult.getAccessToken());
  }

  private AuthenticationSuccess issueAuthentication() throws UnsupportedEncodingException {
    if (authenticationResult == null) {
      AuthenticationSuccess firstResult = fetchAuthentication();
      startRefreshingTimer(TimeUnit.MILLISECONDS.convert(firstResult.getExpiresIn(), TimeUnit.SECONDS));
      return firstResult;
    }
    return authenticationResult;
  }

  private AuthenticationSuccess fetchAuthentication() throws UnsupportedEncodingException {
    String charsetEncoding = StandardCharsets.UTF_8.toString();

    return postToGateway("grant_type=password&username=" + URLEncoder.encode(userName, charsetEncoding)
     + "&password=" + URLEncoder.encode(password, charsetEncoding) + appendScopesParamIfNeeded());
  }

  private void startRefreshingTimer(long expiresInMilliseconds) {
    long time = expiresInMilliseconds > REFRESHING_TIME_BORDER ? expiresInMilliseconds - RESERVE_TIME
        : expiresInMilliseconds / 2;

    timer = new Timer(time, this::refreshAuthentication, clock);
  }

  private void refreshAuthentication() {
    try {
    authenticationResult = postToGateway("grant_type=refresh_token&refresh_token="
        + authenticationResult.getRefreshToken() + appendScopesParamIfNeeded());
    } catch (UnsupportedEncodingException ue) {
      throw new RuntimeIoException(ue);
    }
  }

  private String appendScopesParamIfNeeded() throws UnsupportedEncodingException  {
    if (StringUtils.isBlank(this.scopes)) {
      return "";
    } else {
      return "&scope=" + URLEncoder.encode(this.scopes, StandardCharsets.UTF_8.toString());
    }
  }

  private AuthenticationSuccess postToGateway(String payload) {
    AuthenticationSuccess result;
    String gatewayUrl = gatewayInfo.getGatewayUrl();
    Collection<Header> headers = new ArrayList<>(Arrays.asList(gatewayInfo.getAuthorizationHeader(),
        gatewayInfo.getContentTypeHeader()));
    try {
      result = httpClient.post(gatewayUrl, headers, AuthenticationSuccess.class, payload);
    } catch (IOException ex) {
      if (timer != null) {
        timer.stop();
      }
      throw new RuntimeIoException(ex);
    }
    return result;
  }

}
