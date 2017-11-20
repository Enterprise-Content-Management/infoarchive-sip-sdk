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

import com.opentext.ia.configuration.JsonConfigurationProducer.JsonConfiguration;


public class WhenBuildingConfigurations {

  private static final String STATE = "state";
  private static final Pattern NAME_PATTERN = Pattern.compile("[a-z]{1,3}(?<uuid>.*)");
  private static final String NAME = "name";
  private static final String TENANT_NAME = "myTenant";
  private static final String APPLICATION_NAME = "myApplication";
  private static final String SEARCH_NAME = "mySearch";
  private static final String DESCRIPTION = "myDescription";
  private static final String CATEGORY = "myCategory";

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  private final ConfigurationProducer<JsonConfiguration> producer = new JsonConfigurationProducer();
  private final ConfigurationBuilder<JsonConfiguration> builder = new ConfigurationBuilder<>(producer);
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
  public void shouldUseDefaultPropertiesForApplication() {
    configuration = builder.withApplication().build();
    ConfigurationObject application = configuration.getApplication();

    assertRandomName(application);
    assertProperties(application,
        "type", "ACTIVE_ARCHIVING",
        "archiveType", "SIP",
        STATE, "IN_TEST");
  }

  private void assertRandomName(ConfigurationObject actual) {
    Matcher matcher = NAME_PATTERN.matcher(nameOf(actual));
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

  private void assertProperties(ConfigurationObject actual, String... expectedPropertyValues) {
    JSONObject properties = actual.getProperties();
    for (int i = 0; i < expectedPropertyValues.length; i += 2) {
      String property = expectedPropertyValues[i];
      assertEquals(property, expectedPropertyValues[i + 1], properties.optString(property));
    }
  }

  @Test
  public void shouldSetPropertiesOfApplication() {
    configuration = builder.withApplication()
        .named(APPLICATION_NAME)
        .forAppDecom()
        .forTables()
        .activated()
        .withDescription(DESCRIPTION)
        .withCategory(CATEGORY)
    .build();

    ConfigurationObject application = configuration.getApplication();
    assertProperties(application,
        NAME, APPLICATION_NAME,
        "type", "APP_DECOMM",
        "archiveType", "TABLE",
        STATE, "ACTIVE",
        "description", DESCRIPTION,
        "category", CATEGORY);
  }

  @Test
  public void shouldSetTenantForApplication() {
    configuration = builder.withApplication().build();

    assertProperties(configuration.getApplication(),
        "tenant", nameOf(configuration.getTenant()));
  }

  @Test
  public void shouldThrowExceptionWhenAskedForMissingItem() {
    thrown.expect(IllegalArgumentException.class);

    builder.withTenant().build().getApplication();
  }

  @Test
  public void shouldUseDefaultPropertiesForSearch() {
    configuration = builder.withSearch().build();
    ConfigurationObject search = configuration.getSearch();

    assertRandomName(search);
    assertProperties(search,
        STATE, "DRAFT");
  }

  @Test
  public void shouldSetPropertiesForSearch() {
    configuration = builder.withSearch()
        .named(SEARCH_NAME)
        .withDescription(DESCRIPTION)
        .published()
    .build();
    ConfigurationObject search = configuration.getSearch();

    assertProperties(search,
        NAME, SEARCH_NAME,
        STATE, "PUBLISHED",
        "description", DESCRIPTION);
  }

}
