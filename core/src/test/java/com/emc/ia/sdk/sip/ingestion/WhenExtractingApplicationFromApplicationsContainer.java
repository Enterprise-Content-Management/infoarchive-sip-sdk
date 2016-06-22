/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */

package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class WhenExtractingApplicationFromApplicationsContainer {

  private final TestApplication app = new TestApplication();
  private final Applications apps = new Applications();

  @Before
  public void init() {
    app.setName("TestApplication");
  }

  @Test
  public void fetchApplicationName() {
    assertEquals("Application Name", app.getName(), "TestApplication");
  }

  @Test
  public void validateLinkToStringValue() {
    assertEquals("Application ToString check", app.toString(),
        "Application [name=" + "TestApplication" + ", getLinks()=" + app.getLinks() + " ]");
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
    Map<String, List<Application>> embedded = new HashMap<String, List<Application>>();
    List<Application> list = new ArrayList<Application>();
    app.setName("TestApp");
    list.add(app);
    embedded.put(key, list);
    apps.setApplications(embedded);
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
