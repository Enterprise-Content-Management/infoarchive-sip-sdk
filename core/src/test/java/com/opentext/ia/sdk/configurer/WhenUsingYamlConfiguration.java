/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.configurer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;


public class WhenUsingYamlConfiguration implements InfoArchiveConfiguration {

  private static final String EXPECTED_TENANT_NAME = "myTenant";
  private static final String EXPECTED_APPLICATION_NAME = "myApplication";
  private static final String EXPECTED_QUERY_NAME = "myQuery";

  private String yaml;

  @Before
  public void init() {
    Map<String, Object> root = new HashMap<>();
    root.put("tenant", EXPECTED_TENANT_NAME);
    Map<String, String> application = new HashMap<>();
    application.put(NAME, EXPECTED_APPLICATION_NAME);
    root.put("application", application);
    List<Map<String, String>> queries = new ArrayList<>();
    Map<String, String> query = new HashMap<>();
    query.put(NAME, EXPECTED_QUERY_NAME);
    queries.add(query);
    root.put("query", queries);
    yaml = new Yaml().dumpAsMap(root);
  }

  @Test
  public void shouldParseYamlSuccessfully() throws IOException {
    Map<String, String> config = new YamlConfiguration(yaml).toMap();

    assertEquals("Tenant name", EXPECTED_TENANT_NAME, config.get(TENANT_NAME));
    assertEquals("Application name", EXPECTED_APPLICATION_NAME, config.get(APPLICATION_NAME));
    assertEquals("Query name", EXPECTED_QUERY_NAME, config.get(QUERY_NAME));
  }

}
