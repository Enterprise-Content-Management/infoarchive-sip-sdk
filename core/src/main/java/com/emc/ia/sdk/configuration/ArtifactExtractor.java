package com.emc.ia.sdk.configuration;


public abstract class ArtifactExtractor implements Extractor {
  public abstract BaseIAArtifact extract(Object representation);
}
