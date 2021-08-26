/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.test.TestCase;


public class WhenCreatingAuthenticationStrategy extends TestCase {

  @Test
  public void shouldConfigureNonExpiringAuthentication() {
    String token = randomString();
    assertTrue(NonExpiringTokenAuthentication.optional(token, null, null).isPresent(),
        "Should configure NonExpiringTokenAuthentication");
  }

  @Test
  public void shoultNotConfigureNonExpiringAuthentication() {
    String token = randomString();
    String username = randomString();
    String password = randomString();
    assertFalse(NonExpiringTokenAuthentication.optional(token, username, password).isPresent(),
        "Should not configure NonExpiringTokenAuthentication because of user and password");
  }

  @Test
  public void shouldConfigureBasicAuthentication() {
    String username = randomString();
    String password = randomString();
    assertTrue(BasicAuthentication.optional(username, password, null).isPresent(),
        "Should configure BasicAuthentication");
  }

  @Test
  public void shouldNotConfigureBasicAuthentication() {
    String username = randomString();
    String password = randomString();
    String gateway = randomString();
    assertFalse(BasicAuthentication.optional(username, password, gateway).isPresent(),
        "Should not configure BasicAuthentication because of gateway");
  }

  @Test
  public void shouldConfigureJwtAuthentication() {
    String username = randomString();
    String password = randomString();
    Optional<GatewayInfo> gatewayInfo = GatewayInfo.optional(randomString(), randomString(), randomString());
    HttpClient httpClient = mock(HttpClient.class);
    Clock clock = mock(Clock.class);
    assertTrue(JwtAuthentication
        .optional(username, password, gatewayInfo.orElse(null), httpClient, clock).isPresent(),
        "Should configure JwtAuthentication");
  }

  @Test
  public void shouldNotConfigureJwtAuthentication() {
    String username = randomString();
    String password = randomString();
    HttpClient httpClient = mock(HttpClient.class);
    Clock clock = mock(Clock.class);
    assertFalse(JwtAuthentication.optional(username, password, null, httpClient, clock).isPresent(),
        "Should not configure JwtAuthentication because of abscence of gatewayInfo");
  }
}
