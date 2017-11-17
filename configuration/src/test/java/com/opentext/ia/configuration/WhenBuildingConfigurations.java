/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class WhenBuildingConfigurations {

  private static final String NAME = "name";
  private static final String TENANT_NAME = "myTenant";
  private static final String APPLICATION_NAME = "myApplication";
  private static final Pattern NAME_PATTERN = Pattern.compile("[a-z]{1,3}(?<uuid>.*)");

  @Rule
  public ExpectedException thrown = ExpectedException.none();
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

    assertRandomName(nameOf(configuration.getApplication()));
  }

  private void assertRandomName(String actual) {
    Matcher matcher = NAME_PATTERN.matcher(actual);
    if (matcher.matches()) {
      assertUuid(NAME, matcher.group("uuid"));
    } else {
      fail("Not a random name");
    }
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
  public void shouldSetTenantForApplication() {
    configuration = builder.withApplication().build();

    assertEquals("Tenant", nameOf(configuration.getTenant()),
        configuration.getApplication().getProperties().getString("tenant"));
  }

  @Test
  public void shouldUseDefaultPropertiesForSearch() {
    configuration = builder.withSearch().build();

    assertRandomName(nameOf(configuration.getSearch()));
  }

  @Test
  public void shouldThrowExceptionWhenAskedForMissingItem() {
    thrown.expect(IllegalArgumentException.class);

    builder.withTenant().build().getApplication();
  }

}
