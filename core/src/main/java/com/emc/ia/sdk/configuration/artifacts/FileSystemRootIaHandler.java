/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoot;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoots;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.support.rest.RestClient;

public final class FileSystemRootIaHandler extends BaseIAArtifact {

  public static FileSystemRootExtractor extractor() {
    return new FileSystemRootExtractor();
  }

  private final String name;
  private final String path;

  public FileSystemRootIaHandler() {
    this("defaultFileSystemRoot", null);
  }

  public FileSystemRootIaHandler(String name, String path) {
    this.name = Objects.requireNonNull(name);
    this.path = path;
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    FileSystemRoots fsRoots = client.follow(cache.getFirst(Services.class),
        LINK_FILE_SYSTEM_ROOTS, FileSystemRoots.class);
    FileSystemRoot existingFsRoot = fsRoots.byName(name);
    if (existingFsRoot == null) {
      if (path == null) {
        throw new IllegalStateException("No such FileSystemRoot on running IA instance.");
      } else {
        existingFsRoot = client.createCollectionItem(fsRoots, buildFsRoot(), LINK_ADD, LINK_SELF);
      }
    } else {
      ensureFileSystemRoot(existingFsRoot);
    }
    cache.cacheOne(existingFsRoot);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof FileSystemRootIaHandler)) {
      return false;
    }
    FileSystemRootIaHandler handler = (FileSystemRootIaHandler)other;
    return name.equals(handler.name) && Objects.equals(path, handler.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, path);
  }

  private void ensureFileSystemRoot(FileSystemRoot other) {
    if ((path != null) && !path.equals(other.getPath())) {
      throw new IllegalStateException("Existing FileSystemRoot with the same name has different path.");
    }
  }

  private FileSystemRoot buildFsRoot() {
    FileSystemRoot fsRoot = new FileSystemRoot();
    fsRoot.setName(name);
    fsRoot.setPath(path);
    return fsRoot;
  }

  private static final class FileSystemRootExtractor extends ArtifactExtractor {
    @Override
    public FileSystemRootIaHandler extract(Object representation) {
      Map<String, Object> fsRootRepresentation = asMap(representation);
      String name = extractString(fsRootRepresentation, "name");
      String path = extractString(fsRootRepresentation, "path");
      return new FileSystemRootIaHandler(name, path);
    }

    @Override
    public String getFieldName() {
      return "fileSystemRoot";
    }
  }
}
