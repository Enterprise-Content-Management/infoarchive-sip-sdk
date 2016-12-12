/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.configurer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.emc.ia.sdk.configuration.artifacts.FileSystemRootIaHandler;
import com.emc.ia.sdk.configuration.artifacts.SpaceIaHandler;
import org.yaml.snakeyaml.Yaml;

import com.emc.ia.sdk.configuration.ArtifactCollection;
import com.emc.ia.sdk.configuration.ConfigurationReader;
import com.emc.ia.sdk.configuration.Extractor;
import com.emc.ia.sdk.configuration.Installable;
import com.emc.ia.sdk.configuration.artifacts.ApplicationIaHandler;
import com.emc.ia.sdk.configuration.artifacts.FederationIaHandler;
import com.emc.ia.sdk.configuration.artifacts.TenantIaHandler;
import com.emc.ia.sdk.configuration.artifacts.XdbDatabaseIaHandler;

public final class SnakeYamlConfigurationReader implements ConfigurationReader {

  private final Map configuration;

  public static SnakeYamlConfigurationReader fromFile(File yamlConfiguration) {
    try (InputStream input = new BufferedInputStream(new FileInputStream(yamlConfiguration))) {
      return new SnakeYamlConfigurationReader(input);
    } catch (IOException ex) {
      throw new RuntimeException("Supplied file is not found", ex);
    }
  }

  public SnakeYamlConfigurationReader(InputStream input) {
    Yaml yaml = new Yaml();
    configuration = (Map)yaml.load(input);
  }

  @Override
  public ArtifactCollection readConfiguration() {
    List<Installable> artifacts = Stream.of(
        TenantIaHandler.extractor(),
        ApplicationIaHandler.extractor(),
        FederationIaHandler.extractor(),
        XdbDatabaseIaHandler.extractor(),
        SpaceIaHandler.extractor(),
        FileSystemRootIaHandler.extractor()
    ).flatMap(this::extractWith).collect(Collectors.toList());
    return new ArtifactCollection(artifacts);
  }

  private Stream<Installable> extractWith(Extractor extractor) {
    Object representation = configuration.get(extractor.getFieldName());
    if (representation == null) {
      return Stream.empty();
    } else {
      return Stream.of(extractor.extract(representation));
    }
  }
}
