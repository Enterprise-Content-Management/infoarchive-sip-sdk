/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import java.io.IOException;
import java.util.Map;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.configuration.ListExtractor;
import com.emc.ia.sdk.sip.client.dto.FileSystemFolder;
import com.emc.ia.sdk.sip.client.dto.FileSystemFolders;
import com.emc.ia.sdk.sip.client.dto.SpaceRootFolder;
import com.emc.ia.sdk.support.rest.RestClient;

public final class FileSystemFolderIaHandler extends BaseIAArtifact {

  public static FileSystemFolderExtractor extractor() {
    return new FileSystemFolderExtractor();
  }

  private final FileSystemFolder fsFolder;
  private final String parentFileSystemFolderName;

  public FileSystemFolderIaHandler(FileSystemFolder source, String parentFsFolderName) {
    this.fsFolder = source;
    this.parentFileSystemFolderName = parentFsFolderName;
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    if (parentFileSystemFolderName == null) {
      FileSystemFolders fsFolders = client.follow(
          cache.getFirst(SpaceRootFolder.class),
          LINK_FILE_SYSTEM_FOLDERS,
          FileSystemFolders.class
      );
      FileSystemFolder createdFsFolder = fsFolders.byName(fsFolder.getName());
      if (createdFsFolder == null) {
        createdFsFolder = client.createCollectionItem(fsFolders, fsFolder, LINK_ADD, LINK_SELF);
      }
      cache.cacheOne(createdFsFolder);
    }
  }

  public static class FileSystemFolderExtractor extends ArtifactExtractor {
    @Override
    public FileSystemFolderIaHandler extract(Object representation) {
      Map fsFolderRepresentation = asMap(representation);
      FileSystemFolder fsFolder = new FileSystemFolder();
      fsFolder.setName(extractString(fsFolderRepresentation, "name"));
      fsFolder.setSubPath(extractString(fsFolderRepresentation, "subPath"));
      String parentFsFolderName = extractString(fsFolderRepresentation, "fileSystemFolder");
      return new FileSystemFolderIaHandler(fsFolder, parentFsFolderName);
    }

    @Override
    public String getFieldName() {
      return "fileSystemFolder";
    }

    public ListExtractor fromList() {
      return new ListExtractor(this, "fileSystemFolders");
    }
  }

}
