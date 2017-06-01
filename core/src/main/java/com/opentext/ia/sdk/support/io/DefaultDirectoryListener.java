/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.File;
import java.util.*;


/**
 * Default implementation of {@linkplain DirectoryListener}.
 */
public class DefaultDirectoryListener implements DirectoryListener {

  private final int delta;
  private final Collection<File> directories = new ArrayList<>();
  private final Map<File, Long> reportedFiles = new HashMap<>();

  public DefaultDirectoryListener() {
    this(100);
  }

  public DefaultDirectoryListener(int delta) {
    this.delta = delta;
  }

  @Override
  public void listenIn(File dir) {
    directories.add(dir);
  }

  @Override
  public Iterator<File> addedFiles() {
    Map<File, Long> result = new TreeMap<>();
    for (File dir : directories) {
      File[] files = dir.listFiles();
      if (files != null) {
        Arrays.stream(files)
          // Give producer time to finish writing file
          .filter(file -> new Date().getTime() - file.lastModified() > delta)
          // Skip files that we've seen before, unless they were changed
          .filter(file -> !reportedFiles.containsKey(file) || reportedFiles.get(file) < file.lastModified())
          .forEach(file -> result.put(file, file.lastModified()));
      }
    }
    reportedFiles.putAll(result);
    return result.keySet()
      .iterator();
  }

  @Override
  public void stopListening() {
    // Nothing to do
  }

}
