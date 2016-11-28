package com.emc.ia.sdk.configuration;


/**
 * Abstracts from the actual representation of the file as well as from parser implementation.
 * Reads the content of the configuration into the ArtifactCollection.
 */
public interface ConfigurationReader {
  ArtifactCollection readConfiguration();
}
