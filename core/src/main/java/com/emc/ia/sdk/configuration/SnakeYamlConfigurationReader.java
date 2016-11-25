package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.configuration.artifacts.ApplicationIaHandler;
import com.emc.ia.sdk.configuration.artifacts.FederationIaHandler;
import com.emc.ia.sdk.configuration.artifacts.FileSystemRootIaHandler;
import com.emc.ia.sdk.configuration.artifacts.TenantIaHandler;
import com.emc.ia.sdk.configuration.artifacts.XdbDatabaseIaHandler;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SnakeYamlConfigurationReader implements ConfigurationReader {

  private final Map configuration;

  public static SnakeYamlConfigurationReader fromFile(File yamlConfiguration) {
    try(InputStream input = new BufferedInputStream(new FileInputStream(yamlConfiguration))) {
      return new SnakeYamlConfigurationReader(input);
    } catch (IOException ex) {
      throw new RuntimeException("Supplied file is not found");
    }
  }

  public SnakeYamlConfigurationReader(InputStream input) {
    Yaml yaml = new Yaml();
    configuration = (Map) yaml.load(input);
  }

  @Override
  public ArtifactCollection readConfiguration() {
    List<BaseIAArtifact> artifacts = Stream.of(
        TenantIaHandler.extractor(),
        ApplicationIaHandler.extractor(),
        FederationIaHandler.extractor(),
        XdbDatabaseIaHandler.extractor(),
        FileSystemRootIaHandler.extractor()
    ).map(this::extractWith).collect(Collectors.toList());
    return new ArtifactCollection(artifacts);
//    for (Artifact artifact : Artifact.values()) {
//
//    }
  }

  private BaseIAArtifact extractWith(Extractor extractor) {
    return extractor.extract(configuration.get(extractor.getFieldName()));
  }
}
