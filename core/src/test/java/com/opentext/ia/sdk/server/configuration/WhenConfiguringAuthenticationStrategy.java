/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

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
    assertThat(authentication, instanceOf(NonExpiringTokenAuthentication.class));
  }

  @Test
  public void shouldGetBasicAuthentication() {
    connection.setAuthenticationUser(randomString());
    connection.setAuthenticationPassword(randomString());
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> null, () -> null);
    assertThat(authentication, instanceOf(BasicAuthentication.class));
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
    assertThat(authentication, instanceOf(JwtAuthentication.class));
  }

  @Test
  public void shouldThrowException() {
    connection.setClientId(randomString());
    connection.setClientSecret(randomString());
    connection.setAuthenticationGateway(randomString());
    connection.setAuthenticationUser(randomString());
    HttpClient httpClient = mock(HttpClient.class);
    Clock clock = mock(Clock.class);
    assertThrows(IllegalArgumentException.class,
        () -> authFactory.getAuthenticationStrategy(() -> httpClient, () -> clock));
  }

}
