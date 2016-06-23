/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class WhenFetchingIngestionResponse {

  private final TestIngestionResponse ingestionResponse = new TestIngestionResponse();

  @Before
  public void init() {
    ingestionResponse.setName("TestApplication");
    ingestionResponse.setAipId("TestID");
  }

  @Test
  public void fetchName() {
    assertEquals("Resource Name", ingestionResponse.getName(), "TestApplication");
  }

  @Test
  public void fetchAipID() {
    assertEquals("AipId", ingestionResponse.getAipId(), "TestID");
  }


  public static class TestIngestionResponse extends IngestionResponse {

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
