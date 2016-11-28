package com.emc.ia.sdk.configuration;


/**
 * Abstracts from the actual representation of the file as well as from parser implementation
 */
public interface ConfigurationReader {
  ArtifactCollection readConfiguration();
}
