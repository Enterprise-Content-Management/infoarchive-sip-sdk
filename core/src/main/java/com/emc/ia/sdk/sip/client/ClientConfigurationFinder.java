/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public final class ClientConfigurationFinder {

  private static final String IASDK_PROPERTIES = "iasdk.client.properties";

  private ClientConfigurationFinder() {

  }

  /**
   * Finds the configuration file by convention. First checks the the system property iasdk.client.properties, second
   * checks the current working directory for the file iasdk.client.properties, finally checks the classpath for
   * iasdk.client.properties.
   * @return An InputStream with the contents of the configuration file.
   */
  public static InputStream find() {
    InputStream stream = readFromPropertyVariable();
    if (stream == null) {
      stream = readFromFileSystem(IASDK_PROPERTIES);
    }

    if (stream == null) {
      stream = readFromClassPath();
    }

    if (stream == null) {
      throw new IllegalArgumentException("Couldn't find configuration file.");
    }

    return stream;
  }

  private static InputStream readFromPropertyVariable() {
    String path = System.getProperty(IASDK_PROPERTIES);
    if (path != null && !path.isEmpty()) {
      return readFromFileSystem(path);
    }
    return null;
  }

  private static InputStream readFromClassPath() {
    return ClientConfigurationFinder.class.getResourceAsStream(IASDK_PROPERTIES);
  }

  private static InputStream readFromFileSystem(String path) {
    try {
      File file = new File(path);
      if (file.exists() && file.isFile() && file.canRead()) {
        return new FileInputStream(file);
      }
    } catch (FileNotFoundException e) {
      // Ignore
    }
    return null;
  }
}
