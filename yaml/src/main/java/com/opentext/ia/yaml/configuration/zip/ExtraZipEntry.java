/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

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


  static ExtraZipEntry of(String name, InputStream content) {
    return new ExtraZipEntry() {

      @Override
      public String getName() {
        return name;
      }

      @Override
      public InputStream getContent() {
        return content;
      }
    };
  }


  static ExtraZipEntry of(String name, String content) {
    return of(name, IOUtils.toInputStream(content, StandardCharsets.UTF_8));
  }

}
