/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;


public class WhenWatchingFilesInDirectories extends WhenListeningForFilesInDirectories {

  public WhenWatchingFilesInDirectories() {
    super(new WatchingDirectoryListener());
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
