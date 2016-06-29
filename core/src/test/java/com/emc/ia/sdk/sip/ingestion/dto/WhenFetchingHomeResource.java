/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.support.rest.Link;


public class WhenFetchingHomeResource {

  private final TestHomeResource homeResource = new TestHomeResource();

  @Before
  public void init() {
    homeResource.setName("TestApplication");
  }

  @Test
  public void fetchResourceName() {
    assertEquals("Resource Name", homeResource.getName(), "TestApplication");
  }


  public static class TestHomeResource extends HomeResource {

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
