/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


class DefaultDirectoryListener implements DirectoryListener {

  private static final int DELTA = 100;

  private final Collection<File> directories = new ArrayList<>();
  private final Map<File, Long> reportedFiles = new HashMap<>();

  @Override
  public void listenIn(File dir) {
    directories.add(dir);
  }

  @Override
  public Iterator<File> addedFiles() {
    Map<File, Long> result = new HashMap<>();
    for (File dir : directories) {
      File[] files = dir.listFiles();
      if (files != null) {
        Arrays.stream(files)
            // Give producer time to finish writing file
            .filter(file -> new Date().getTime() - file.lastModified() > DELTA)
            // Skip files that we've seen before, unless they were changed
            .filter(file -> !reportedFiles.containsKey(file) || reportedFiles.get(file) < file.lastModified())
            .forEach(file -> result.put(file, file.lastModified()));
      }
    }
    reportedFiles.putAll(result);
    return result.keySet().iterator();
  }

  @Override
  public void stopListening() {
    // Nothing to do
  }

}
