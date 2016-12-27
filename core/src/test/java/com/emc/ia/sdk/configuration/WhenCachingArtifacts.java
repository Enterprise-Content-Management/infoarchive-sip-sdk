/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.FileSystemFolder;
import com.emc.ia.sdk.sip.client.dto.Space;
import com.emc.ia.sdk.sip.client.dto.Tenant;


public class WhenCachingArtifacts {

  private IACache cache;
  private static final String TEST_NAME = "test";

  @Before
  public void creatCache() {
    cache = new IACache();
  }

  @Test
  public void shouldGetCachedTenantAsFirst() {
    Tenant tenant = new Tenant();
    tenant.setName(TEST_NAME);
    cache.cacheOne(tenant);

    assertEquals(TEST_NAME, cache.getFirst(Tenant.class).getName());
  }

  @Test
  public void shouldGetCachedTenantByName() {
    Tenant tenant = new Tenant();
    tenant.setName(TEST_NAME);
    cache.cacheOne(tenant);

    assertEquals(TEST_NAME, cache.getByClassWithName(Tenant.class, TEST_NAME).getName());
  }

  @Test
  public void shouldGetFileSystemFolderByName() {
    FileSystemFolder fsFolder = new FileSystemFolder();
    fsFolder.setName("myFolder");
    cache.cacheOne(fsFolder);

    assertEquals("myFolder", cache.getByClassWithName(FileSystemFolder.class, "myFolder").getName());
  }

  @Test
  public void shouldNotGetCachedTenantWithWrongName() {
    Tenant tenant = new Tenant();
    tenant.setName(TEST_NAME);
    cache.cacheOne(tenant);

    assertNull(cache.getByClassWithName(Tenant.class, "Wrong Name"));
  }

  @Test
  public void shouldNotGetAnyArtifact() {
    Tenant tenant = new Tenant();
    tenant.setName(TEST_NAME);
    cache.cacheOne(tenant);
    Application application = new Application();
    application.setName("PhoneCalls");
    cache.cacheOne(application);

    assertNull(cache.getByClassWithName(Federation.class, "mainFederation"));
  }

  @Test
  public void shouldNotGetArtifactWithWrongName() {
    createFolders(3).forEach(cache::cacheOne);

    assertNull(cache.getByClassWithName(FileSystemFolder.class, "folder3"));
  }

  @Test
  public void shouldChooseRightFileSystemFolderByName() {
    createFolders(3).forEach(cache::cacheOne);

    assertEquals("folder1", cache.getByClassWithName(FileSystemFolder.class, "folder1").getName());
  }

  @Test
  public void shouldGetCachedSpaceAsFirst() {
    Space space = new Space();
    space.setName(TEST_NAME);
    cache.cacheOne(space);

    assertEquals(TEST_NAME, cache.getFirst(Space.class).getName());
  }

  @Test
  public void shouldGetFirstCachedFolder() {
    createFolders(3).forEach(cache::cacheOne);

    assertEquals("folder0", cache.getFirst(FileSystemFolder.class).getName());
  }

  private List<FileSystemFolder> createFolders(int n) {
    List<FileSystemFolder> folders = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      FileSystemFolder folder = new FileSystemFolder();
      folder.setName("folder" + i);
      folders.add(folder);
    }
    return folders;
  }

}
