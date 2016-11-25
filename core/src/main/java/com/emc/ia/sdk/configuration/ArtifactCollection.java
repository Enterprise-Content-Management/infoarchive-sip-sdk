package com.emc.ia.sdk.configuration;


import java.util.Iterator;
import java.util.List;

public final class ArtifactCollection implements Iterable<BaseIAArtifact> {

  private List<BaseIAArtifact> artifactInstallationOrder;

  public ArtifactCollection(List<BaseIAArtifact> source) {
    this.artifactInstallationOrder = source;
  }

  @Override
  public Iterator<BaseIAArtifact> iterator() {
    return artifactInstallationOrder.iterator();
  }
}
