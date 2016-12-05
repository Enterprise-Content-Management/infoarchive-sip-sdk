/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import org.junit.Test;

public class WhenReadingConfiguration {

  private static final String CONFIGURATION = "tenant: MyTenant\n"
                            + "application:\n"
                            + "  name: PhoneCalls\n"
                            + "  type: ACTIVE_ARCHIVING\n"
                            + "  archiveType: SIP\n"
                            + "space: PhoneCalls-space";

  @Test
  public void shouldExtract3Objects() {
    ConfigurationReader reader = new SnakeYamlConfigurationReader(new ByteArrayInputStream(CONFIGURATION.getBytes(UTF_8)));
    ArtifactCollection artifacts = reader.readConfiguration();
    int size = 0;
    for (Iterator<Installable> artifactIter = artifacts.iterator(); artifactIter.hasNext(); artifactIter.next()) {
      size++;
    }
    assertEquals(3, size);
  }

}
