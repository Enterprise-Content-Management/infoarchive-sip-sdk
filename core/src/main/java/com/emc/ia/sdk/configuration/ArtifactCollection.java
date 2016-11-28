package com.emc.ia.sdk.configuration;


import java.util.Iterator;
import java.util.List;

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
