/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;


class WhenWorkingWithFileSystemRoots extends TestCase {

  @Test
  void shouldReturnFirst() {
    FileSystemRoots collection = new FileSystemRoots();
    Map<String, List<FileSystemRoot>> embedded = new HashMap<>();
    FileSystemRoot item1 = new FileSystemRoot();
    item1.setName(randomString());
    FileSystemRoot item2 = new FileSystemRoot();
    item2.setName(randomString());
    embedded.put("fileSystemRoots", Arrays.asList(item1, item2));

    collection.setEmbedded(embedded);

    assertSame(item1, collection.first());
  }

}
