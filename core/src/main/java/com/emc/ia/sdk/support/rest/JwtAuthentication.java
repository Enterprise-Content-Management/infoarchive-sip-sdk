/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.HTTP_CLIENT_CLASSNAME;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_GATEWAY;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_PASSWORD;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_USER;

import com.emc.ia.sdk.sip.client.dto.result.AuthenticationSuccess;
import com.emc.ia.sdk.support.NewInstance;
import com.emc.ia.sdk.support.http.Header;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.io.RuntimeIoException;

public final class JwtAuthentication implements AuthenticationStrategy {

  private final String gatewayUri;
  private final String username;
  private final String password;
  private final HttpClient httpClient;

  public static Optional<AuthenticationStrategy> fromConfiguration(Map<String, String> configuration) {
    if (configuration.containsKey(SERVER_AUTHENTICATION_USER)
               && configuration.containsKey(SERVER_AUTHENTICATION_PASSWORD)
               && configuration.containsKey(SERVER_AUTHENTICATION_GATEWAY)) {
      return Optional.of(new JwtAuthentication(
          configuration.get(SERVER_AUTHENTICATION_GATEWAY),
          configuration.get(SERVER_AUTHENTICATION_USER),
          configuration.get(SERVER_AUTHENTICATION_PASSWORD),
          NewInstance.fromConfiguration(configuration, HTTP_CLIENT_CLASSNAME, ApacheHttpClient.class.getName())
              .as(HttpClient.class)
      ));
    } else {
      return Optional.empty();
    }
  }

  public JwtAuthentication(String gatewayUri, String username, String password, HttpClient httpClient) {
    if (gatewayUri.endsWith("/")) {
      this.gatewayUri = gatewayUri.substring(0, gatewayUri.length() - 1) + "/oauth/token";
    } else {
      this.gatewayUri = gatewayUri + "/oauth/token";
    }
    this.username = username;
    this.password = password;
    this.httpClient = httpClient;
  }

  @Override
  public String issueToken() {
    String authToken = Base64.getEncoder().encodeToString("infoarchive.sipsdk:secret".getBytes(StandardCharsets.UTF_8));
    Collection<Header> headers = Collections.singletonList(new Header("Authorization", authToken));
    String payload = "grant_type=password&username=" + username + "&password=" + password;
    AuthenticationSuccess authSuccess;
    try {
      authSuccess = httpClient.post(gatewayUri, headers, payload, AuthenticationSuccess.class);
    } catch (IOException ex) {
      throw new RuntimeIoException(ex);
    }
    return "Bearer " + authSuccess.getAccessToken();
  }
}
