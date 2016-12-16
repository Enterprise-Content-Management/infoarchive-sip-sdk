/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration;

import java.io.IOException;

import com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.RestClient;

/**
 * Implements Installable artifact. Represents single artifact that can be installed.
 * The class handles exception rethrowing and provides additional utility methods for installation
 */
public abstract class BaseIAArtifact implements Installable, InfoArchiveLinkRelations {

  /** Handles exception rethrowing and delegates the method logic to the abstract method.
   * @param client creates requests to the running InfoArchive
   * @param cache used to retrieve some needed data (e.g. URI's of already installed artifacts) and cache installed
   */
  @Override
  public final void install(RestClient client, IACache cache) {
    try {
      installArtifact(client, cache);
    } catch (IOException ex) {
      throw new RuntimeIoException(ex);
    }
  }

  protected abstract void installArtifact(RestClient client, IACache cache) throws IOException;
}
