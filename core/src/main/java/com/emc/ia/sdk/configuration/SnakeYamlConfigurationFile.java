package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
  public <T extends NamedLinkContainer> T getNamedObject(Class<T> typeToken) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T extends NamedLinkContainer> List<T> getNamedObjects(Class<T> typeToken) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Tenant getTenant() {
    return Extractors.getTenantExtractor().extract(configuration.get("tenant"));
  }

  @Override
  public Application getApplication() {
    return Extractors.getApplicationExtractor().extract(configuration.get("application"));
  }

  @Override
  public Database getDatabase() {
    return Extractors.getDatabaseExtractor().extract(configuration.get("xdbDatabase"));
  }

  @Override
  public Federation getFederation() {
    return Extractors.getFederationExtractor().extract(configuration.get("federation"));
  }
}
