/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */

package com.emc.ia.sdk.sip.ingestion.dto;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.support.rest.Link;


public class WhenExtractingApplicationFromApplicationsContainer {

  private final TestApplication app = new TestApplication();
  private final Applications apps = new Applications();

  @Before
  public void init() {
    app.setName("TestApplication");
    app.setType("Test");
    app.setArchiveType("TestArchive");
  }

  @Test
  public void fetchApplicationName() {
    assertEquals("Application Name", app.getName(), "TestApplication");
  }

  @Test
  public void fetchType() {
    assertEquals("Type is", app.getType(), "Test");
  }

  @Test
  public void fetchArchiveType() {
    assertEquals("Archive Type", app.getArchiveType(), "TestArchive");
  }

  @Test
  public void shouldReturnValidApplicationObjectWithDefaultKeyAndPreSetName() {
    prepare("applications");
    assertNotNull(apps.byName("TestApp"));
  }

  @Test
  public void shouldReturnNullWhenQueriedForApplicationWithWrongName() {
    prepare("applications");
    assertNull(apps.byName("Not_A_Valid_Name"));
  }

  @Test
  public void shouldReturnNullWhenQueriedForApplicationWithWrongKeyButPreSetName() {
    prepare("Not_a_valid_key");
    assertNull(apps.byName("TestApp"));
  }

  @Test
  public void shouldReturnNullWhenQueriedForApplicationWithWrongKeyAndWrongName() {
    prepare("Not_a_valid_key");
    assertNull(apps.byName("Not_A_Valid_Name"));
  }

  private void prepare(String key) {
    Map<String, List<Application>> items = new HashMap<String, List<Application>>();
    List<Application> list = new ArrayList<Application>();
    app.setName("TestApp");
    list.add(app);
    items.put(key, list);
    apps.setItems(items);
  }


  public static class TestApplication extends Application {

    @Override
    public Map<String, Link> getLinks() {
      Map<String, Link> result = new HashMap<String, Link>();

      Link link = new Link();
      link.setHref("https://india.emc.com/content-management/infoarchive/infoarchive.htm");
      result.put("testApp", link);

      return result;
    }

  }

}
