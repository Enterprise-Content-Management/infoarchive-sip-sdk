package com.emc.ia.sdk.configuration;

/**
 * Possible replacement for RestCache For future use. Maybe some tree walker with only links. Maybe with cached content
 */

public interface IARemoteSnapshot {
  String getTennantName();
  ApplicationRemoteSnapshot getApplicationConfiguration();
  StorageConfiguration getStorageConfiguration();
  SearchConfiguration getSearchConfiguration();
}
