/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

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
  public void shouldGetCachedSpaceAsFirst() {
    Space space = new Space();
    space.setName(TEST_NAME);
    cache.cacheOne(space);

    assertEquals(TEST_NAME, cache.getFirst(Space.class).getName());
  }

  @Test
  public void shouldGetFirstCachedFolder() {
    FileSystemFolder folder0 = new FileSystemFolder();
    folder0.setName("first");
    cache.cacheOne(folder0);
    FileSystemFolder folder1 = new FileSystemFolder();
    folder1.setName("second");
    cache.cacheOne(folder1);
    FileSystemFolder folder2 = new FileSystemFolder();
    folder2.setName("third");
    cache.cacheOne(folder2);

    assertEquals("first", cache.getFirst(FileSystemFolder.class).getName());
  }
}
