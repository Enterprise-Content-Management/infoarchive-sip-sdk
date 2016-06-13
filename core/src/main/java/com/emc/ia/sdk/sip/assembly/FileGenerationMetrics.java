/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.assembly;

import java.io.File;


/**
 * Metrics about file generation.
*/
public class FileGenerationMetrics {

  private final File file;
  private final Metrics metrics;

  FileGenerationMetrics(File file, Metrics metrics) {
    this.file = file;
    this.metrics = metrics;
  }

  /**
   * Return the generated file.
   * @return The generated file
   */
  public File getFile() {
    return file;
  }

  /**
   * Return metrics about the file generation process.
   * @return Metrics about the file generation process
   */
  public Metrics getMetrics() {
    return metrics;
  }

}
