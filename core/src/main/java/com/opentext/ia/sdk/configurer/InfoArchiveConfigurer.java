/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.configurer;

/**
 * Interface hiding the details of how the application and holding configuration objects are created in the Archive.
 */
@FunctionalInterface
public interface InfoArchiveConfigurer {

  void configure();
}
