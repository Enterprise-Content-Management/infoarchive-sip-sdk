/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.AuthenticationStrategyFactory;
import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.rest.AuthenticationStrategy;
import com.opentext.ia.sdk.support.http.rest.BasicAuthentication;
import com.opentext.ia.sdk.support.http.rest.JwtAuthentication;
import com.opentext.ia.sdk.support.http.rest.NonExpiringTokenAuthentication;
import com.opentext.ia.test.TestCase;


public class WhenConfiguringAuthenticationStrategy extends TestCase {

  private final ArchiveConnection connection = new ArchiveConnection();
  private final AuthenticationStrategyFactory authFactory = new AuthenticationStrategyFactory(connection);

  @Test
  public void shouldGetTokenAuthenticationConfiguration() {
    connection.setAuthenticationToken(randomString());
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> null, () -> null);
    assertTrue(authentication instanceof NonExpiringTokenAuthentication);
  }

  @Test
  public void shouldGetBasicAuthentication() {
    connection.setAuthenticationUser(randomString());
    connection.setAuthenticationPassword(randomString());
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> null, () -> null);
    assertTrue(authentication instanceof BasicAuthentication);
  }

  @Test
  public void shouldGetJwtAuthentication() {
    connection.setClientId(randomString());
    connection.setClientSecret(randomString());
    connection.setAuthenticationGateway(randomString());
    connection.setAuthenticationUser(randomString());
    connection.setAuthenticationPassword(randomString());
    HttpClient httpClient = mock(HttpClient.class);
    Clock clock = mock(Clock.class);
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> httpClient, () -> clock);
    assertTrue(authentication instanceof JwtAuthentication);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException() {
    connection.setClientId(randomString());
    connection.setClientSecret(randomString());
    connection.setAuthenticationGateway(randomString());
    connection.setAuthenticationUser(randomString());
    HttpClient httpClient = mock(HttpClient.class);
    Clock clock = mock(Clock.class);
    authFactory.getAuthenticationStrategy(() -> httpClient, () -> clock);
  }

}
