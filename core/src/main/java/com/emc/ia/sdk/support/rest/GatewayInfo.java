/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import com.emc.ia.sdk.support.http.Header;
import org.apache.http.entity.ContentType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public final class GatewayInfo {
  private final String gatewayUrl;
  private final String clientId;
  private final String clientSecret;

  public static Optional<GatewayInfo> optional(String gatewayUri, String clientId, String clientSecret) {
    if ((gatewayUri == null) || (clientId == null) || (clientSecret == null)) {
      return Optional.empty();
    } else {
      return Optional.of(new GatewayInfo(gatewayUri, clientId, clientSecret));
    }
  }

  public GatewayInfo(final String gatewayUrl, final String clientId, final String clientSecret) {
    if (gatewayUrl.endsWith("/")) {
      this.gatewayUrl = gatewayUrl + "oauth/token";
    } else {
      this.gatewayUrl = gatewayUrl + "/oauth/token";
    }
    if (clientId.isEmpty()) {
      throw new IllegalArgumentException("Client Id is empty");
    } else {
      this.clientId = clientId;
    }
    if (clientSecret.isEmpty()) {
      throw new IllegalArgumentException("Client Secret is empty");
    } else {
      this.clientSecret = clientSecret;
    }
  }

  public Header getAuthorizationHeader() {
    String basicAuthToken = "Basic "
        + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    return new Header("Authorization", basicAuthToken);
  }

  public Header getContentTypeHeader() {
    return new Header("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
  }

  public String getGatewayUrl() {
    return gatewayUrl;
  }
}
