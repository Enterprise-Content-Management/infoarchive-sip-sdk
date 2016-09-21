package com.emc.ia.sdk.support.rest;

import com.emc.ia.sdk.support.test.TestCase;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTENTICATON_TOKEN;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_GATEWAY;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_PASSWORD;
import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.SERVER_AUTHENTICATION_USER;
import static org.junit.Assert.assertTrue;

public class WhenConfiguringAuthenticationStrategy extends TestCase{

  private Map<String, String> configuration = new HashMap<>();

  @Test
  public void shouldConfigureNonExpiringAuthentication() {
    configuration.put(SERVER_AUTENTICATON_TOKEN, randomString());
    assertTrue("Should configure NonExpiringTokenAuthentication",
        NonExpiringTokenAuthentication.fromConfiguration(configuration).isPresent());
  }

  @Test
  public void shouldConfigureBasicAuthentication() {
    configuration.put(SERVER_AUTHENTICATION_USER, randomString());
    configuration.put(SERVER_AUTHENTICATION_PASSWORD, randomString());
    assertTrue("Should configure BasicAuthentication",
        BasicAuthentication.fromConfiguration(configuration).isPresent());
  }

  @Test
  public void shouldConfigureBasicAuthenticationDespiteToken() {
    configuration.put(SERVER_AUTHENTICATION_USER, randomString());
    configuration.put(SERVER_AUTHENTICATION_PASSWORD, randomString());
    configuration.put(SERVER_AUTENTICATON_TOKEN, randomString());
    assertTrue("Should configure BasicAuthentication despite configuration contains token",
        BasicAuthentication.fromConfiguration(configuration).isPresent());
  }

  @Test
  public void shouldConfigureJwtAuthentication() {
    configuration.put(SERVER_AUTHENTICATION_USER, randomString());
    configuration.put(SERVER_AUTHENTICATION_PASSWORD, randomString());
    configuration.put(SERVER_AUTHENTICATION_GATEWAY, randomString());
    assertTrue("Should configure JwtAuthentication",
        JwtAuthentication.fromConfiguration(configuration).isPresent());
  }
}
