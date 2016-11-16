package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.sip.client.dto.Application;
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
  public Tenant getTenant() { // TODO: Delegate?
    String tenantName = (String) configuration.get("tenant");
    Tenant targetTenant = new Tenant();
    targetTenant.setName(tenantName);
    return targetTenant;
  }

  @Override
  public Application getApplication() { // TODO: Delegate?
    Map appRepresentation = (Map) configuration.get("application");
    Application application = new Application();
    application.setName((String) appRepresentation.get("name"));
    application.setArchiveType((String) appRepresentation.get("archiveType"));
    application.setType((String) appRepresentation.get("type"));
    return application;
  }
}
