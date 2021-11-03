/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */

package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.http.rest.Link;


class WhenExtractingApplicationFromApplicationsContainer {

  private final TestApplication app = new TestApplication();

  @BeforeEach
  public void init() {
    app.setName("TestApplication");
    app.setType("Test");
    app.setArchiveType("TestArchive");
    app.setMigration("REQUIRED");
  }

  @Test
  void fetchApplicationName() {
    assertEquals("TestApplication", app.getName(), "Application Name");
  }

  @Test
  void fetchType() {
    assertEquals("Test", app.getType(), "Type is");
  }

  @Test
  void fetchArchiveType() {
    assertEquals("TestArchive", app.getArchiveType(), "Archive Type");
  }

  @Test
  void fetchMigration() {
    assertEquals("REQUIRED", app.getMigration(), "Migration State");
  }

  public static class TestApplication extends Application {

    @Override
    public Map<String, Link> getLinks() {
      Map<String, Link> result = new HashMap<>();

      Link link = new Link();
      link.setHref("http://documentum.opentext.com/infoarchive/");
      result.put("testApp", link);

      return result;
    }

  }

}
