/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;


class FileResolver implements ResourceResolver, ResourcesResolver {

  private final File dir;

  FileResolver(File base) {
    dir = base.getParentFile();
  }

  @Override
  public String apply(String name) {
    return resolve(new File(dir, name));
  }

  private String resolve(File file) {
    try (InputStream input = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
      return IOUtils.toString(input, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UnknownResourceException(file.getName(), e);
    }
  }

  @Override
  public List<String> resolve(String pattern) {
    return new FilesSelector(dir).apply(pattern).stream()
        .map(this::resolve)
        .collect(Collectors.toList());
  }

}
