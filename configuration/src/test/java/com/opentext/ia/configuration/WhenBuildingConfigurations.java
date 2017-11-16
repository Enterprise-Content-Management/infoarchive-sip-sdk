/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;


public class WhenBuildingConfigurations {

  private static final String NAME = "name";
  private static final String TENANT_NAME = "myTenant";
  private static final String APPLICATION_NAME = "myApplication";

  private final ConfigurationProducer<ConfigurationObject> producer = new JsonConfigurationProducer();
  private final ConfigurationBuilder<ConfigurationObject> builder = new ConfigurationBuilder<>(producer);
  private Configuration<ConfigurationObject> configuration;

  @Test
  public void shouldUseDefaultPropertiesForTenant() {
    configuration = builder.withTenant().build();

    assertEquals(NAME, "INFOARCHIVE", configuration.getTenant().getProperties().getString(NAME));
  }

  @Test
  public void shouldSetPropertiesOfTenant() {
    configuration = builder.withTenant()
        .named(TENANT_NAME)
    .build();

    assertEquals(NAME, TENANT_NAME, nameOf(configuration.getTenant()));
  }

  private String nameOf(ConfigurationObject object) {
    return object.getProperties().getString(NAME);
  }

  @Test
  public void shouldUseDefaultpropertiesForApplication() {
    configuration = builder.withApplication().build();

    assertUuid(NAME, nameOf(configuration.getApplication()));
  }

  private void assertUuid(String message, String actual) {
    try {
      UUID.fromString(actual);
    } catch (IllegalArgumentException e) {
      fail(message + " is not a UUID");
    }
  }

  @Test
  public void shouldSetPropertiesOfApplication() {
    configuration = builder.withApplication()
        .named(APPLICATION_NAME)
    .build();

    JSONObject application = configuration.getApplication().getProperties();
    assertEquals(NAME, APPLICATION_NAME, application.getString(NAME));
  }

  @Test
  @Ignore("TODO: Make this work")
  public void shouldSetTenantForApplication() {
    configuration = builder.withApplication().build();

    assertEquals("Tenant", nameOf(configuration.getTenant()), nameOf(configuration.getApplication()));
  }

  @Test
  public void shouldUseDefaultPropertiesForSearch() {
    configuration = builder.withSearch().build();

    assertUuid(NAME, nameOf(configuration.getSearch()));
  }

}
