/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the collection of all InfoArchive artifacts that have to be installed on running instance.
 * It should guarantee the correct order of installation of the artifacts.
 */
public final class ArtifactCollection implements Iterable<Installable> {

  private final List<Installable> artifactInstallationOrder;

  public ArtifactCollection(List<Installable> source) {
    source.sort(Comparator.comparingInt(Installable::getInstallationOrderKey));
    artifactInstallationOrder = new ArrayList<>(source);
  }

  @Override
  public Iterator<Installable> iterator() {
    return artifactInstallationOrder.iterator();
  }
}
