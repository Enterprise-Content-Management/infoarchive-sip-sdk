/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;


class ListingDirectoryListener implements DirectoryListener {

  private final Collection<File> directories = new ArrayList<>();
  private final Collection<File> reportedFiles = new ArrayList<>();

  @Override
  public void listenIn(File dir) {
    directories.add(dir);
  }

  @Override
  public Iterator<File> addedFiles() {
    Collection<File> result = new ArrayList<>();
    for (File dir : directories) {
      File[] files = dir.listFiles();
      if (files != null) {
        result.addAll(Arrays.asList(files));
      }
    }
    result.removeAll(reportedFiles);
    reportedFiles.addAll(result);
    return result.iterator();
  }

  @Override
  public void stopListening() {
    // Nothing to do
  }

}
