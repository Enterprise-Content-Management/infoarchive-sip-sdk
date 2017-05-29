/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.File;
import java.util.Iterator;

/**
 * Listen for files being added to directories.
 */
public interface DirectoryListener {

  /**
   * Add a directory to listen to.
   * @param dir The directory to listen to
   */
  void listenIn(File dir);

  /**
   * Return the files that were added since the last call to this method.
   * @return The added files
   */
  Iterator<File> addedFiles();

  /**
   * Stop listening to the added directories.
   */
  void stopListening();

}
