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


public class WhenFetchingHomeResource {

  private final TestHomeResource homeResource = new TestHomeResource();

  @Before
  public void init() {
    homeResource.setName("TestApplication");
  }

  @Test
  public void fetchResourceName() {
    assertEquals("Resource Name", "TestApplication", homeResource.getName());
  }


  public static class TestHomeResource extends Services {

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
