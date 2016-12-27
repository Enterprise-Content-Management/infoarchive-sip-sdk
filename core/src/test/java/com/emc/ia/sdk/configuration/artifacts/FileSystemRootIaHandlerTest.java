/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations.LINK_ADD;
import static com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations.LINK_FILE_SYSTEM_ROOTS;
import static com.emc.ia.sdk.support.rest.StandardLinkRelations.LINK_SELF;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoot;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoots;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.support.rest.RestClient;

public class FileSystemRootIaHandlerTest {
  private static final String NAME = "defaultFileSystemRoot";
  private static final String PATH = "storage";
  private IACache cache;
  private RestClient client;

  @Before
  public void setUp() {
    cache = new IACache();
    cache.cacheOne(new Services());
    client = mock(RestClient.class);
  }

  @Test
  public void whenComparingNonEquals() {
    assertNotEquals(getSampleFsRootArtifact(), null);
    assertNotEquals(getSampleFsRootArtifact(),
        new FileSystemRootIaHandler("WrongName", PATH));
    assertNotEquals(getSampleFsRootArtifact(),
        new FileSystemRootIaHandler(NAME, "WrongPath"));
  }

  @Test
  public void whenExtractingFsRoot() {
    ArtifactExtractor fsRootExtractor = FileSystemRootIaHandler.extractor();
    BaseIAArtifact fsRootArtifact = fsRootExtractor.extract(getFsRootRepresentation());
    BaseIAArtifact expectedArtifact = new FileSystemRootIaHandler(NAME, PATH);
    assertEquals(expectedArtifact, fsRootArtifact);
  }

  @Test
  public void whenInstallingFsRoot() throws IOException {
    FileSystemRoot resultFsRoot = getFsRoot();
    FileSystemRoots fsRoots = mock(FileSystemRoots.class);
    when(client.follow(any(), eq(LINK_FILE_SYSTEM_ROOTS), eq(FileSystemRoots.class))).thenReturn(fsRoots);
    when(fsRoots.byName(NAME)).thenReturn(null);
    when(client.createCollectionItem(eq(fsRoots), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultFsRoot);

    new FileSystemRootIaHandler(NAME, PATH).install(client, cache);

    assertEquals(resultFsRoot, cache.getFirst(FileSystemRoot.class));
  }

  @Test
  public void whenInstallingExistingFsRoot() throws IOException {
    FileSystemRoot existingFsRoot = getFsRoot();
    FileSystemRoots fsRoots = mock(FileSystemRoots.class);
    when(client.follow(any(), eq(LINK_FILE_SYSTEM_ROOTS), eq(FileSystemRoots.class))).thenReturn(fsRoots);
    when(fsRoots.byName(NAME)).thenReturn(existingFsRoot);

    new FileSystemRootIaHandler(NAME, PATH).install(client, cache);

    assertEquals(existingFsRoot, cache.getFirst(FileSystemRoot.class));
  }

  @Test(expected = IllegalStateException.class)
  public void whenReferencingNonExistingFsRoot() throws IOException {
    FileSystemRoots fsRoots = mock(FileSystemRoots.class);
    when(client.follow(any(), eq(LINK_FILE_SYSTEM_ROOTS), eq(FileSystemRoots.class))).thenReturn(fsRoots);
    when(fsRoots.byName(NAME)).thenReturn(null);

    new FileSystemRootIaHandler(NAME, null).install(client, cache);
  }

  @Test(expected = IllegalStateException.class)
  public void whenInstallingFsRootWithWrongPath() throws IOException {
    FileSystemRoot existingFsRoot = getFsRoot();
    FileSystemRoots fsRoots = mock(FileSystemRoots.class);
    when(client.follow(any(), eq(LINK_FILE_SYSTEM_ROOTS), eq(FileSystemRoots.class))).thenReturn(fsRoots);
    when(fsRoots.byName(NAME)).thenReturn(existingFsRoot);

    new FileSystemRootIaHandler(NAME, "WrongPath").install(client, cache);
  }

  private Map<String, String> getFsRootRepresentation() {
    Map<String, String> fsRootRepresentation = new HashMap<>();
    fsRootRepresentation.put("name", NAME);
    fsRootRepresentation.put("path", PATH);
    return fsRootRepresentation;
  }

  private FileSystemRoot getFsRoot() {
    FileSystemRoot fsRoot = new FileSystemRoot();
    fsRoot.setName(NAME);
    fsRoot.setPath(PATH);
    return fsRoot;
  }

  private FileSystemRootIaHandler getSampleFsRootArtifact() {
    return new FileSystemRootIaHandler(NAME, PATH);
  }
}
