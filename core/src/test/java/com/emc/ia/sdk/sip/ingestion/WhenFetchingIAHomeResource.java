/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class WhenFetchingIAHomeResource {

  private final TestHomeResource homeResource = new TestHomeResource();

  @Before
  public void init() {
    homeResource.setName("TestApplication");
  }

  @Test
  public void fetchResourceName() {
    assertEquals("Resource Name", homeResource.getName(), "TestApplication");
  }

  @Test
  public void validateLinkToStringValue() {
    assertEquals("Resource ToString check", homeResource.toString(),
        "IAHomeResource [name=" + "TestApplication" + ", links=" + homeResource.getLinks() + " ]");
  }


  public static class TestHomeResource extends IAHomeResource {

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
