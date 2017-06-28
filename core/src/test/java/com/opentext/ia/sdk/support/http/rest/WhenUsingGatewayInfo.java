/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.Test;

import com.opentext.ia.sdk.support.http.Header;
import com.opentext.ia.test.TestCase;


public class WhenUsingGatewayInfo extends TestCase {

  private static final String GATEWAY_URL = "http://authgateway.com/";
  private final String clientId = randomString();
  private final String clientSecret = randomString();
  private final GatewayInfo gatewayInfo = new GatewayInfo(GATEWAY_URL, clientId, clientSecret);

  @Test
  public void shouldCorrectlyFormUrl() {
    assertEquals("Endpoint suffix should be added", GATEWAY_URL + "oauth/token", gatewayInfo.getGatewayUrl());
  }

  @Test
  public void shouldCorrecltyFormAuthorizationHeader() {
    String correctToken =
        "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    Header correctHeader = new Header("Authorization", correctToken);
    assertEquals("Header should be formed correctly", correctHeader, gatewayInfo.getAuthorizationHeader());
  }

}
