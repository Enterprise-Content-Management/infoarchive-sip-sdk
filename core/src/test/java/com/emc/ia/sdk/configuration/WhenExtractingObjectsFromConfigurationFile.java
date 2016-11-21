package com.emc.ia.sdk.configuration;

import static org.junit.Assert.*;

import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class WhenExtractingObjectsFromConfigurationFile {
  private InputStream configuration;

  @Test
  public void shouldGetTenantWithTheSameName() {
    String tenantConfiguration = "tenant: MyTenant";
    configuration = new ByteArrayInputStream(tenantConfiguration.getBytes());
    YamlConfigurationFile yamlConfig = new SnakeYamlConfigurationFile(configuration);
    Tenant targetTenant = yamlConfig.getTenant();
    assertEquals("Should be equal", "MyTenant", targetTenant.getName());
  }

  @Test
  public void shouldGetApplicationWithEqualFields() {
    String appConfiguration = "application:\n" +
                                  "  name: PhoneCalls\n" +
                                  "  type: ACTIVE_ARCHIVING\n" +
                                  "  archiveType: SIP\n";
    configuration = new ByteArrayInputStream(appConfiguration.getBytes());
    YamlConfigurationFile yamlConfig = new SnakeYamlConfigurationFile(configuration);
    Application targetApplication = yamlConfig.getApplication();
    assertEquals("Should be equal", "PhoneCalls", targetApplication.getName());
    assertEquals("Should be equal", "ACTIVE_ARCHIVING", targetApplication.getType());
    assertEquals("should be equal", "SIP", targetApplication.getArchiveType());
  }
}
