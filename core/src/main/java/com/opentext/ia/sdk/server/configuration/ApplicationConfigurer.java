/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import java.io.IOException;

import com.opentext.ia.sdk.client.api.ArchiveConnection;


/**
 * Interface hiding the details of how an application in the Archive is configured.
 */
public interface ApplicationConfigurer {

  /**
   * Returns the name of the application to configure.
   * @return the name of the application to configure
   */
  String getApplicationName();

  /**
   * Configure the Archive for the application.
   * @param connection How to communicate with the InfoArchive server
   * @throws IOException When an I/O error occurs
   */
  void configure(ArchiveConnection connection) throws IOException;

}
