/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;


class FileResolver implements ResourceResolver {

  private final File dir;

  FileResolver(File base) {
    dir = base.getParentFile();
  }

  @Override
  public String apply(String name) {
    try (InputStream input = new FileInputStream(new File(dir, name))) {
      return IOUtils.toString(input, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UnknownResourceException(name, e);
    }
  }

}
