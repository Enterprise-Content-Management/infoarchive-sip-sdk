package com.emc.ia.sdk.configuration;


import java.util.Iterator;
import java.util.List;

/**
 * Represents the collection of all InfoArchive artifacts that have to be installed on running instance.
 * It should guarantee the correct order of installation of the artifacts.
 */
public final class ArtifactCollection implements Iterable<Installable> {

  private List<Installable> artifactInstallationOrder;

  public ArtifactCollection(List<Installable> source) {
    this.artifactInstallationOrder = source;
  }

  @Override
  public Iterator<Installable> iterator() {
    return artifactInstallationOrder.iterator();
  }
}
