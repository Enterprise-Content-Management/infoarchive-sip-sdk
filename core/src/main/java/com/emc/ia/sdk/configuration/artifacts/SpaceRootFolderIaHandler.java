package com.emc.ia.sdk.configuration.artifacts;


import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.Extractor;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoot;
import com.emc.ia.sdk.sip.client.dto.Space;
import com.emc.ia.sdk.sip.client.dto.SpaceRootFolder;
import com.emc.ia.sdk.sip.client.dto.SpaceRootFolders;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;
import java.util.Map;

public final class SpaceRootFolderIaHandler extends BaseIAArtifact {

  private final SpaceRootFolder srFolder;
  private final String parentSpaceName;
  private final String fsRootName;

  public SpaceRootFolderIaHandler(SpaceRootFolder source, String parentSpaceName, String fsRootName) {
    this.srFolder = source;
    this.parentSpaceName = parentSpaceName;
    this.fsRootName = fsRootName;
  }

  @Override
  public void install(RestClient client, IACache cache) throws IOException {
    SpaceRootFolders srFolders = client.follow(cache.getByClassWithName(Space.class, parentSpaceName),
        LINK_SPACE_ROOT_FOLDERS, SpaceRootFolders.class);
    SpaceRootFolder createdFolder = srFolders.byName(srFolder.getName());
    if (createdFolder == null) {
      String fsRootUri = cache.getByClassWithName(FileSystemRoot.class, fsRootName).getSelfUri();
      srFolder.setFileSystemRoot(fsRootUri);
      createdFolder = client.createCollectionItem(srFolders, srFolder, LINK_ADD, LINK_SELF);
    }
    //TODO: Update existing fsRootFolder if needed
    cache.cacheOne(createdFolder);
  }

  private static final class SpaceRootFolderExtractor implements Extractor {
    @Override
    public BaseIAArtifact extract(Object representation) {
      Map srFolderRepresentation = (Map) representation;
      SpaceRootFolder srFolder = new SpaceRootFolder();
      srFolder.setName((String) srFolderRepresentation.get("name"));
      String parentSpaceName = (String) srFolderRepresentation.get("space");
      String fsRootName = (String) srFolderRepresentation.get("fileSystemRoot");
      return new SpaceRootFolderIaHandler(srFolder, parentSpaceName, fsRootName);
    }

    @Override
    public String getFieldName() {
      return "spaceRootFolder";
    }
  }
}
