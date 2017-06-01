/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.rest.AuthenticationStrategy;
import com.opentext.ia.sdk.support.rest.BasicAuthentication;
import com.opentext.ia.sdk.support.rest.JwtAuthentication;
import com.opentext.ia.sdk.support.rest.NonExpiringTokenAuthentication;
import com.opentext.ia.sdk.support.test.TestCase;


public class WhenConfiguringAuthenticationStrategy extends TestCase {

  private final ServerConfiguration configuration = new ServerConfiguration();
  private final AuthenticationStrategyFactory authFactory = new AuthenticationStrategyFactory(configuration);

  @Test
  public void shouldGetTokenAuthenticationConfiguration() {
    configuration.setAuthenticationToken(randomString());
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> null, () -> null);
    assertTrue(authentication instanceof NonExpiringTokenAuthentication);
  }

  @Test
  public void shouldGetBasicAuthentication() {
    configuration.setAuthenticationUser(randomString());
    configuration.setAuthenticationPassword(randomString());
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> null, () -> null);
    assertTrue(authentication instanceof BasicAuthentication);
  }

  @Test
  public void shouldGetJwtAuthentication() {
    configuration.setClientId(randomString());
    configuration.setClientSecret(randomString());
    configuration.setAuthenticationGateway(randomString());
    configuration.setAuthenticationUser(randomString());
    configuration.setAuthenticationPassword(randomString());
    HttpClient httpClient = mock(HttpClient.class);
    Clock clock = mock(Clock.class);
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> httpClient, () -> clock);
    assertTrue(authentication instanceof JwtAuthentication);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException() {
    configuration.setClientId(randomString());
    configuration.setClientSecret(randomString());
    configuration.setAuthenticationGateway(randomString());
    configuration.setAuthenticationUser(randomString());
    HttpClient httpClient = mock(HttpClient.class);
    Clock clock = mock(Clock.class);
    authFactory.getAuthenticationStrategy(() -> httpClient, () -> clock);
  }

}
