/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.emc.ia.sdk.support.http.Header;

public final class GatewayInfo {
  private final String gatewayUrl;
  private final String clientId;
  private final String clientSecret;

  public static GatewayInfo of(String gatewayUri, String clientId, String clientSecret) {
    if ((gatewayUri == null) || (clientId == null) || (clientSecret == null)) {
      return null;
    } else {
      return new GatewayInfo(gatewayUri, clientId, clientSecret);
    }
  }

  public GatewayInfo(final String gatewayUrl, final String clientId, final String clientSecret) {
    if (gatewayUrl.endsWith("/")) {
      this.gatewayUrl = gatewayUrl + "oauth/token";
    } else {
      this.gatewayUrl = gatewayUrl + "/oauth/token";
    }
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  public Header getAuthorizationHeader() {
    String basicAuthToken = "Basic "
        + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    return new Header("Authorization", basicAuthToken);
  }

  public String getGatewayUrl() {
    return gatewayUrl;
  }
}
