/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;


/**
 * Customize a {@linkplain ZipConfiguration}.
 * @since 10.1.0
 */
@FunctionalInterface
public interface ZipCustomization {

  /**
   * Additional entry to be added to the ZIP file.
   */
  public interface ExtraZipEntry {

    /**
     * Returns the name for the ZIP entry.
     * @return the name for the ZIP entry
     */
    String getName();

    /**
     * Returns the content for the ZIP entry.
     * @return the content for the ZIP entry
     */
    InputStream getContent();

  }


  /**
   * Customize the ZIP entry.
   * @param name the name of the ZIP entry
   * @param content the content of the ZIP entry
   * @return the customized content for the ZIP entry
   * @throws IOException when an I/O error occurs
   */
  InputStream customize(String name, InputStream content) throws IOException;

  /**
   * Returns extra entries to be added to the ZIP file.
   * @return extra entries to be added to the ZIP file
   */
  default Collection<ExtraZipEntry> extraEntries() {
    return Collections.emptyList();
  }


  /**
   * No customization. Leaves all entries as-is.
   * @return a customization that leaves all entries as-is
   */
  static ZipCustomization none() {
    return (name, input) -> input;
  }

}
