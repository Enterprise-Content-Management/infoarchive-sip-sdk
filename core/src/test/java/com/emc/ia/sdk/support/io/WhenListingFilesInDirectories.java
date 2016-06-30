/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;


public class WhenListingFilesInDirectories extends WhenListeningForFilesInDirectories {

  public WhenListingFilesInDirectories() {
    super(new ListingDirectoryListener());
  }

}
