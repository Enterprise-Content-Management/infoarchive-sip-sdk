/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configurer;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.rest.AuthenticationStrategy;
import com.emc.ia.sdk.support.rest.BasicAuthentication;
import com.emc.ia.sdk.support.rest.JwtAuthentication;
import com.emc.ia.sdk.support.rest.NonExpiringTokenAuthentication;
import com.emc.ia.sdk.support.test.TestCase;

public class WhenConfiguringAuthenticationStrategy extends TestCase {

  private final Map<String, String> configuration = new HashMap<>();
  private final AuthenticationStrategyFactory authFactory = new AuthenticationStrategyFactory(configuration);

  @Test
  public void shouldGetTokenAuthenticationConfiguration() {
    configuration.put(InfoArchiveConfiguration.SERVER_AUTENTICATON_TOKEN, randomString());
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> null);
    assertTrue(authentication instanceof NonExpiringTokenAuthentication);
  }

  @Test
  public void shouldGetBasicAuthentication() {
    configuration.put(InfoArchiveConfiguration.SERVER_AUTHENTICATION_USER, randomString());
    configuration.put(InfoArchiveConfiguration.SERVER_AUTHENTICATION_PASSWORD, randomString());
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> null);
    assertTrue(authentication instanceof BasicAuthentication);
  }

  @Test
  public void shouldGetJwtAuthentication() {
    configuration.put(InfoArchiveConfiguration.SERVER_CLIENT_ID, randomString());
    configuration.put(InfoArchiveConfiguration.SERVER_CLIENT_SECRET, randomString());
    configuration.put(InfoArchiveConfiguration.SERVER_AUTHENTICATION_GATEWAY, randomString());
    configuration.put(InfoArchiveConfiguration.SERVER_AUTHENTICATION_USER, randomString());
    configuration.put(InfoArchiveConfiguration.SERVER_AUTHENTICATION_PASSWORD, randomString());
    HttpClient httpClient = mock(HttpClient.class);
    AuthenticationStrategy authentication = authFactory.getAuthenticationStrategy(() -> httpClient);
    assertTrue(authentication instanceof JwtAuthentication);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException() {
    configuration.put(InfoArchiveConfiguration.SERVER_CLIENT_ID, randomString());
    configuration.put(InfoArchiveConfiguration.SERVER_CLIENT_SECRET, randomString());
    configuration.put(InfoArchiveConfiguration.SERVER_AUTHENTICATION_GATEWAY, randomString());
    configuration.put(InfoArchiveConfiguration.SERVER_AUTHENTICATION_USER, randomString());
    HttpClient httpClient = mock(HttpClient.class);
    authFactory.getAuthenticationStrategy(() -> httpClient);
  }
}
