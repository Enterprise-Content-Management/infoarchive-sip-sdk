package com.emc.ia.sdk.configuration;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class SnakeYamlConfigurationFile implements YamlConfigurationFile {

  private Map configuration;

  public static SnakeYamlConfigurationFile fromFile(File yamlConfiguration) {
    try(InputStream input = new BufferedInputStream(new FileInputStream(yamlConfiguration))) {
      return new SnakeYamlConfigurationFile(input);
    } catch (IOException ex) {
      throw new RuntimeException("Supplied file is not found");
    }
  }

  public SnakeYamlConfigurationFile(InputStream input) {
    Yaml yaml = new Yaml();
    configuration = (Map) yaml.load(input);
  }

  @Override
  public BaseIAArtifact extractWith(Extractor extractor) {
    return extractor.extract(configuration.get(extractor.getFieldName()));
  }
}
