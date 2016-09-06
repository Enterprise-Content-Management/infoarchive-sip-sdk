/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configurer;

/**
 * Interface hiding the details of how the application and holding configuration objects are created in the Archive.
 */
@FunctionalInterface
public interface InfoArchiveConfigurer {

  void configure();
}
