/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.opentext.ia.configuration.ConfigurationBuilder;
import com.opentext.ia.yaml.core.YamlMap;

class WhenBuildingYamlConfigurations {

  private static final String NAME = "name";
  private static final String TENANT_NAME = "myTenant";
  private static final String APPLICATION_NAME = "myApplication";

  private final ConfigurationBuilder<YamlMapConfiguration> builder =
      new ConfigurationBuilder<>(new YamlMapConfigurationProducer());

  @Test
  void shouldBuildYampMap() {
    YamlMap yaml = builder.withApplication().build().getYaml();

    assertEquals("1.0.0", yaml.get("version").toString(), "Version");
  }

  @Test
  void shouldExtractTenant() {
    YamlMap tenant = builder.withTenant().named(TENANT_NAME).build().getTenant();

    assertEquals(TENANT_NAME, tenant.get(NAME).toString(), "Tenant name");
  }

  @Test
  void shouldExtractApplication() {
    YamlMap application = builder.withTenant().named(TENANT_NAME).withApplication().named(APPLICATION_NAME).end()
        .build().getApplication();

    assertEquals(APPLICATION_NAME, application.get(NAME).toString(), "Application name");
    assertEquals(TENANT_NAME, application.get("tenant").toString(), "Tenant name");
    assertTrue(application.containsKey("description"), "Missing description");
    assertTrue(application.get("description").isEmpty(), "Default description");
  }

  @Test
  void shouldExtractPdiWithMultipleContentObjects() {
    YamlMapConfiguration configuration =
        builder.withApplication().withPdi().withContent().as("foo").end().withContent().as("bar").end().build();

    YamlMap application = configuration.getApplication();
    YamlMap pdi = configuration.getPdi(application);

    assertEquals(2, pdi.get("content").toList().size(), "# content objects");
  }

}
