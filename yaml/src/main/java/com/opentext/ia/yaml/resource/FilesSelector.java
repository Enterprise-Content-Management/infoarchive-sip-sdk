/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.resource;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Select files in a directory based on a wildcard pattern.
 * @since 9.14.0
 */
public class FilesSelector implements Function<String, List<File>> {

  private final File root;

  public FilesSelector(File root) {
    this.root = root.getAbsoluteFile();
  }

  @Override
  public List<File> apply(String pattern) {
    return resolve(root, new MatchesWildcardPattern<>(relativePathOf(new File(pattern)), this::relativePathOf));
  }

  private String relativePathOf(File file) {
    String path = file.getAbsolutePath();
    String rootPath = root.getPath();
    if (path.startsWith(rootPath)) {
      path = path.substring(rootPath.length() + 1);
    } else {
      path = file.getPath();
    }
    return path.replace(File.separatorChar, '/');
  }

  private List<File> resolve(File dir, Predicate<File> filter) {
    List<File> files = Optional.ofNullable(dir.listFiles())
        .map(Arrays::asList)
        .orElseGet(Collections::emptyList);
    List<File> result = files.stream()
        .filter(File::isFile)
        .filter(filter)
        .collect(Collectors.toList());
    files.stream()
        .filter(File::isDirectory)
        .map(file -> resolve(file, filter))
        .forEach(result::addAll);
    return result;
  }

}
