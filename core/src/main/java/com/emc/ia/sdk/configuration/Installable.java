/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.support.rest.RestClient;

/**
 * Represents the installable artifacts that can be installed to the running instance of InfoArchive.
 * The installable artifact could be either BaseIAArtifact, that represents single InfoArchive Artifact, or
 * ArtifactGroup, that represents the collection of artifacts with the same alias type.
 */
public interface Installable {
  /**
   * Installs artifact to the running InfoArchive instance using RestClient to make requests and IACache to retrieve
   * some values needed during installation or to cache installed artifact for future use.
   * @param client creates requests to the running InfoArchive
   * @param cache used to retrieve some needed data (e.g. URI's of already installed artifacts) and cache installed
   *              artifact
   */
  void install(RestClient client, IACache cache);

  /**
   * This method is required to manage the installation order of artifacts.
   * Lesser returned key means higher priority.
   * @return the key of the installation order.
   */
  int getInstallationOrderKey();
}
