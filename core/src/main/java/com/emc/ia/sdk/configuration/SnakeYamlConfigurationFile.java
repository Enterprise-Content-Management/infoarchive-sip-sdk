package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class SnakeYamlConfigurationFile implements YamlConfigurationFile {

  private Map configuration;

  public SnakeYamlConfigurationFile(File yamlConfiguration) {
    try(InputStream input = new BufferedInputStream(new FileInputStream(yamlConfiguration))) {
      Yaml yaml = new Yaml();
      configuration = (Map) yaml.load(input);
    } catch (IOException ex) {
      throw new RuntimeException("Supplied file is not found");
    }
  }

  @Override
  public <T extends NamedLinkContainer> T getNamedObject(Class<T> typeToken) {
    return null;
  }

  @Override
  public Tenant getTenant() {
    return Extractors.getTenantExtractor().extract("tenant", configuration);
  }

  @Override
  public Application getApplication() {
    return Extractors.getApplicationExtractor().extract("application", configuration);
  }

  @Override
  public Federation getFederation() {
    return Extractors.getFederationExtractor().extract("federation", configuration);
  }
}
