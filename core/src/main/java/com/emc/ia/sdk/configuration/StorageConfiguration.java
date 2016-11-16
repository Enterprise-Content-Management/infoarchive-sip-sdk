package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.FileSystemFolder;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoot;
import com.emc.ia.sdk.sip.client.dto.Library;
import com.emc.ia.sdk.sip.client.dto.Space;
import com.emc.ia.sdk.sip.client.dto.SpaceRootFolder;
import com.emc.ia.sdk.sip.client.dto.SpaceRootLibrary;
import com.emc.ia.sdk.sip.client.dto.Store;

public interface StorageConfiguration {
  Federation getFederation();
  Database getDatabase();
  SpaceRootLibrary getSpaceRootLibrary();
  Library getLibrary();
  Space getSpace();
  SpaceRootFolder getSpaceRootFolder();
  FileSystemRoot getFileSystemRoot();
  FileSystemFolder getFileSystemFolder();
  Store getStore();
}
