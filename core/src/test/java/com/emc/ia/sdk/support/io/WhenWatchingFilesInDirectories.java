/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;


public class WhenWatchingFilesInDirectories extends WhenListeningForFilesInDirectories {

  public WhenWatchingFilesInDirectories() {
    super(new WatchingDirectoryListener());
  }

  @Before
  public void before() {
    boolean isMacOsX = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("mac os x");
    org.junit.Assume.assumeTrue(!isMacOsX);
  }

  @Test
  public void shouldStopThreadWhenNoLongerListening() throws IOException {
    WatchingDirectoryListener listener = (WatchingDirectoryListener)getListener();
    startListening();
    assertTrue("Sanity check", listener.watchThread().isAlive());

    listener.stopListening();
    assertFalse("Listening thread is still running", listener.watchThread().isAlive());
  }

}
