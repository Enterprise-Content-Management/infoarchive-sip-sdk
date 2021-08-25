/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */

package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.opentext.ia.sdk.support.http.rest.Link;


public class WhenExtractingApplicationFromApplicationsContainer {

  private final TestApplication app = new TestApplication();

  @Before
  public void init() {
    app.setName("TestApplication");
    app.setType("Test");
    app.setArchiveType("TestArchive");
  }

  @Test
  public void fetchApplicationName() {
    assertEquals("Application Name", "TestApplication", app.getName());
  }

  @Test
  public void fetchType() {
    assertEquals("Type is", "Test", app.getType());
  }

  @Test
  public void fetchArchiveType() {
    assertEquals("Archive Type", "TestArchive", app.getArchiveType());
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
