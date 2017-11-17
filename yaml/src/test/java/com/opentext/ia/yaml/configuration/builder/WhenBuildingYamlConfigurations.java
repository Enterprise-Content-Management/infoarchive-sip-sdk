/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.opentext.ia.configuration.ConfigurationBuilder;
import com.opentext.ia.configuration.ConfigurationProducer;
import com.opentext.ia.yaml.core.YamlMap;


public class WhenBuildingYamlConfigurations {

  private static final String NAME = "name";
  private static final String TENANT_NAME = "myTenant";
  private static final String APPLICATION_NAME = "myApplication";

  private final ConfigurationProducer<YamlMap> producer = new YamlMapConfigurationProducer();
  private final ConfigurationBuilder<YamlMap> builder = new ConfigurationBuilder<>(producer);

  @Test
  public void shouldBuildYampMap() {
    YamlMapConfiguration configuration = (YamlMapConfiguration)builder.withApplication().build();
    YamlMap yaml = configuration.getYaml();
    assertEquals("Version", "1.0.0", yaml.get("version").toString());
  }

  @Test
  public void shouldExtractTenant() {
    YamlMap tenant = builder.withTenant().named(TENANT_NAME).build().getTenant();

    assertEquals("Tenant name", TENANT_NAME, tenant.get(NAME).toString());
  }

  @Test
  public void shouldExtractApplication() {
    YamlMap application = builder.withTenant()
        .named(TENANT_NAME)
        .withApplication()
            .named(APPLICATION_NAME)
        .end()
    .build().getApplication();

    assertEquals("Application name", APPLICATION_NAME, application.get(NAME).toString());
    assertEquals("Tenant name", TENANT_NAME, application.get("tenant").toString());
    assertTrue("Missing description", application.containsKey("description"));
    assertTrue("Default description", application.get("description").isEmpty());
  }

}
