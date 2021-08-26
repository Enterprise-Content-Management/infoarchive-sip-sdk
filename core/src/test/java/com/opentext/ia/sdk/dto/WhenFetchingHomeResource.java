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


class WhenFetchingHomeResource {

  private final TestHomeResource homeResource = new TestHomeResource();

  @BeforeEach
  public void init() {
    homeResource.setName("TestApplication");
  }

  @Test
  void fetchResourceName() {
    assertEquals("TestApplication", homeResource.getName(), "Resource Name");
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
