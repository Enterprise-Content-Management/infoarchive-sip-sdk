/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
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
    try (InputStream input = new FileInputStream(file)) {
      return IOUtils.toString(input, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UnknownResourceException(file.getName(), e);
    }
  }

  @Override
  public List<String> resolve(String pattern) {
    Pattern regex = Pattern.compile(toRegex(pattern));
    Predicate<File> filter = (file) -> regex.matcher(relativePathOf(file)).matches();
    return resolve(dir, filter);
  }

  private List<String> resolve(File root, Predicate<File> filter) {
    List<File> files = Optional.ofNullable(root.listFiles())
        .map(Arrays::asList)
        .orElse(Collections.emptyList());
    List<String> result = files.stream()
        .filter(File::isFile)
        .filter(filter)
        .map(this::resolve)
        .collect(Collectors.toList());
    files.stream()
        .filter(File::isDirectory)
        .map(file -> resolve(file, filter))
        .forEach(result::addAll);
    return result;
  }

  private String toRegex(String pattern) {
    return pattern
        .replace(".", "\\.")
        .replace("?", ".")
        .replace("**/", "([^/]+/)+")
        .replace("*", "[^/]*");
  }

  private String relativePathOf(File file) {
    return file.getAbsolutePath()
        .substring(dir.getAbsolutePath().length() + 1)
        .replace(File.separatorChar, '/');
  }

}
