/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.Test;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.test.TestCase;


public class WhenCreatingAuthenticationStrategy extends TestCase {

  @Test
  public void shouldConfigureNonExpiringAuthentication() {
    String token = randomString();
    assertTrue("Should configure NonExpiringTokenAuthentication",
        NonExpiringTokenAuthentication.optional(token, null, null).isPresent());
  }

  @Test
  public void shoultNotConfigureNonExpiringAuthentication() {
    String token = randomString();
    String username = randomString();
    String password = randomString();
    assertFalse("Should not configure NonExpiringTokenAuthentication because of user and password",
        NonExpiringTokenAuthentication.optional(token, username, password).isPresent());
  }

  @Test
  public void shouldConfigureBasicAuthentication() {
    String username = randomString();
    String password = randomString();
    assertTrue("Should configure BasicAuthentication",
        BasicAuthentication.optional(username, password, null).isPresent());
  }

  @Test
  public void shouldNotConfigureBasicAuthentication() {
    String username = randomString();
    String password = randomString();
    String gateway = randomString();
    assertFalse("Should not configure BasicAuthentication because of gateway",
        BasicAuthentication.optional(username, password, gateway).isPresent());
  }

  @Test
  public void shouldConfigureJwtAuthentication() {
    String username = randomString();
    String password = randomString();
    Optional<GatewayInfo> gatewayInfo = GatewayInfo.optional(randomString(), randomString(), randomString());
    HttpClient httpClient = mock(HttpClient.class);
    Clock clock = mock(Clock.class);
    assertTrue("Should configure JwtAuthentication",
        JwtAuthentication.optional(username, password, gatewayInfo.orElse(null), httpClient, clock).isPresent());
  }

  @Test
  public void shouldNotConfigureJwtAuthentication() {
    String username = randomString();
    String password = randomString();
    HttpClient httpClient = mock(HttpClient.class);
    Clock clock = mock(Clock.class);
    assertFalse("Should not configure JwtAuthentication because of abscence of gatewayInfo",
        JwtAuthentication.optional(username, password, null, httpClient, clock).isPresent());
  }
}
