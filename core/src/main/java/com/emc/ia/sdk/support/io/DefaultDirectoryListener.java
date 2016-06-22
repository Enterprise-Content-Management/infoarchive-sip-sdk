/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.File;
import java.util.Iterator;
import java.util.Locale;


/**
 * Default implementation of {@linkplain DirectoryListener}.
 */
public class DefaultDirectoryListener implements DirectoryListener {

  private final DirectoryListener listener;

  public DefaultDirectoryListener() {
    this.listener = isMacOsx() ? new ListingDirectoryListener() : new WatchingDirectoryListener();
  }

  private boolean isMacOsx() {
    return System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("mac os x");
  }

  @Override
  public void listenIn(File dir) {
    listener.listenIn(dir);
  }

  @Override
  public Iterator<File> addedFiles() {
    return listener.addedFiles();
  }

  @Override
  public void stopListening() {
    listener.stopListening();
  }

}
