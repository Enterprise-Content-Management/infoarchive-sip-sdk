/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import com.opentext.ia.sdk.client.api.ServerConfiguration;


/**
 * Interface hiding the details of how the application and holding configuration objects are created in the Archive.
 */
public interface InfoArchiveConfigurer {

  ServerConfiguration getServerConfiguration();

  void configure();

}
