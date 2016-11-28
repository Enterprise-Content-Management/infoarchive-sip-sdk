package com.emc.ia.sdk.configuration.artifacts;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.Extractor;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoot;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoots;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;
import java.util.Map;

public final class FileSystemRootIaHandler extends BaseIAArtifact {

  public static Extractor extractor() {
    return new FileSystemRootExtractor();
  }

  private final FileSystemRoot fsRoot;

  public FileSystemRootIaHandler(FileSystemRoot source) {
    this.fsRoot = source;
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    FileSystemRoots fsRoots = client.follow(cache.getFirst(Services.class), LINK_FILE_SYSTEM_ROOTS, FileSystemRoots.class);
    FileSystemRoot existingFsRoot = fsRoots.byName(fsRoot.getName());
    if (existingFsRoot == null) {
      // TODO: Fail or get the first fsRoot???
      throw new IllegalStateException("No such FileSystemRoot on running IA instance");
    }
    cache.cacheOne(existingFsRoot);
  }

  private static final class FileSystemRootExtractor extends ArtifactExtractor {
    @Override
    public BaseIAArtifact extract(Object representation) {
      if (representation instanceof String) {
        String fsRootName = asString(representation);
        FileSystemRoot fsRoot = new FileSystemRoot();
        fsRoot.setName(fsRootName);
        return new FileSystemRootIaHandler(fsRoot);
      } else {
        Map fsRootRepresentation = asMap(representation);
        FileSystemRoot fsRoot = new FileSystemRoot();
        fsRoot.setName(extractString(fsRootRepresentation, "name"));
        fsRoot.setPath(extractString(fsRootRepresentation, "path"));
        return new FileSystemRootIaHandler(fsRoot);
      }
    }

    @Override
    public String getFieldName() {
      return "fileSystemRoot";
    }
  }
}
