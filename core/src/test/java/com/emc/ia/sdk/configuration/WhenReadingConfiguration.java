package com.emc.ia.sdk.configuration;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class WhenReadingConfiguration {

  private final String configuration = "tenant: MyTenant\n" +
                             "application:\n" +
                             "  name: PhoneCalls\n" +
                             "  type: ACTIVE_ARCHIVING\n" +
                             "  archiveType: SIP\n" +
                             "space: PhoneCalls-space";

  @Test
  public void shouldExtract3Objects() {
    ConfigurationReader reader = new SnakeYamlConfigurationReader(new ByteArrayInputStream(configuration.getBytes()));
    ArtifactCollection artifacts = reader.readConfiguration();
    int size = 0;
    for (Iterator<Installable> artifactIter = artifacts.iterator(); artifactIter.hasNext(); artifactIter.next()) {
      size++;
    }
    assertEquals(3, size);
  }

}
