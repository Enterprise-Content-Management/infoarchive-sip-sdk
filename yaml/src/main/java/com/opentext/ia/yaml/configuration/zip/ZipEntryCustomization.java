/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.IOException;
import java.io.InputStream;


/**
 * Customize a ZIP entry.
 * @since 10.1.0
 */
public interface ZipEntryCustomization {

  /**
   * Returns <code> true</code> if this customization can customize a ZIP entry with a given name.
   * @param name the name of the ZIP entry
   * @return whether this customization can customize an entry with the given name
   */
  boolean matches(String name);

  /**
   * Customize the ZIP entry.
   * @param input the content of the ZIP entry
   * @return the customized content for the ZIP entry
   * @throws IOException when an I/O error occurs
   */
  InputStream customize(InputStream input) throws IOException;

}
