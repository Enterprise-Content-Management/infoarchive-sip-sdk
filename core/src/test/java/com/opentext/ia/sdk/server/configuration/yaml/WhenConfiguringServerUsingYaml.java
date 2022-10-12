/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.dto.Services;
import com.opentext.ia.sdk.server.configuration.ApplicationConfigurer;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.rest.Link;
import com.opentext.ia.sdk.support.http.rest.RestClient;
import com.opentext.ia.test.TestCase;
//import com.opentext.ia.yaml.configuration.YamlConfiguration;


@Disabled
class WhenConfiguringServerUsingYaml extends TestCase implements InfoArchiveLinkRelations {

  private final HttpClient httpClient = mock(HttpClient.class);
  private final ArchiveConnection connection = new ArchiveConnection();
  private final ApplicationConfigurer clientSideConfigurer = mock(ApplicationConfigurer.class);
//  private final YamlBasedApplicationConfigurer configurer = new YamlBasedApplicationConfigurer(
//      new YamlConfiguration("version: 1.0.0"), (yaml, conn) -> clientSideConfigurer);

  @BeforeEach
  public void init() throws IOException {
    connection.setRestClient(new RestClient(httpClient));
  }

  @Test
  void shouldDeferToServerWhenItSupportsYamlConfiguration() throws IOException {
    String configurationUri = randomUri();
    Services services = new Services();
    services.getLinks().put(LINK_CONFIGURATION, new Link(configurationUri));
    when(httpClient.get(any(), any(), eq(Services.class))).thenReturn(services);

//    configurer.configure(connection);

    verify(httpClient).put(eq(configurationUri), any(), eq(String.class), anyString());
    verify(clientSideConfigurer, never()).configure(any());
  }

  @Test
  void shouldConfigureFromClientWhenServerDoesntSupportsYamlConfiguration() throws IOException {
    Services services = new Services();
    when(httpClient.get(any(), any(), eq(Services.class))).thenReturn(services);

//    configurer.configure(connection);

    verify(clientSideConfigurer).configure(any());
    verify(httpClient, never()).put(anyString(), any(), any(), anyString());
  }

//  @Test
//  void shouldConvertYamlToProperties() throws IOException {
//    Map<String, String> expected = loadProperties();
//    Map<String, String> actual = yamlToProperties();
//    assertEqual(normalizeWhitespace(expected), normalizeWhitespace(actual));
//  }

//  private Map<String, String> normalizeWhitespace(Map<String, String> map) {
//    for (Entry<String, String> entry : map.entrySet()) {
//      map.replace(entry.getKey(), entry.getValue().replaceAll("\\s+", " ").trim());
//    }
//    return map;
//  }

//  private Map<String, String> loadProperties() throws IOException {
//    Properties properties = new Properties();
//    try (InputStream input = WhenConfiguringServerUsingYaml.class.getResourceAsStream("/iaif/iaif.properties")) {
//      properties.load(input);
//    }
//    Set<String> stringPropertyNames = properties.stringPropertyNames();
//    Map<String, String> result = new HashMap<>(stringPropertyNames.size());
//    for (String name : stringPropertyNames) {
//      result.put(name, properties.getProperty(name));
//    }
//    return result;
//  }

//  private Map<String, String> yamlToProperties() throws IOException {
//    try (InputStream input = WhenConfiguringServerUsingYaml.class.getResourceAsStream("/iaif/iaif.yaml")) {
//      YamlConfiguration configuration = new YamlConfiguration(input, ResourceResolver.fromClasspath("/iaif"));
//      return new YamlPropertiesMap(configuration.getMap().sort());
//    }
//  }
//
//  private void assertEqual(Map<String, String> expected, Map<String, String> actual) {
//    expected.entrySet().stream()
//        .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
//        .forEachOrdered(e -> assertEqual(e, actual));
//  }

//  private void assertEqual(Entry<String, String> expected, Map<String, String> actual) {
//    if (!actual.containsKey(expected.getKey())) {
//      fail(String.format("Missing key: %s%nGot:%n%s", expected.getKey(),
//          actual.keySet().stream().collect(Collectors.joining(System.lineSeparator()))));
//    }
//    TestUtil.assertEquals(normalize(expected.getValue()), normalize(actual.get(expected.getKey())),
//        expected.getKey());
//  }

//  private List<String> normalize(String value) {
//    return Arrays.asList(value
//        .replaceAll("(\\r|\\n)+\\s*", " ")
//        .replaceAll("\\s+<", "<")
//        .replaceAll(">\\s+", ">")
//        .split(","));
//  }

}
