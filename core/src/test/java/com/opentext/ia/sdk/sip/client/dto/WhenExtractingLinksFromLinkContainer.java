/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.opentext.ia.sdk.support.rest.Link;
import com.opentext.ia.sdk.support.rest.LinkContainer;


public class WhenExtractingLinksFromLinkContainer {

  private static final String LINK_HREF = "http://documentum.opentext.com/infoarchive/";
  private static final String KEY = "Test";

  private final LinkContainer container = new LinkContainer();
  private final Link link = new Link();
  private final Map<String, Link> links = new HashMap<String, Link>();

  @Before
  public void init() {
    link.setHref(LINK_HREF);
    links.put(KEY, link);
    container.setLinks(links);
  }

  @Test
  public void fetchLinkObjectFromContainer() {
    assertEquals("Link bject information", container.getLinks()
      .get(KEY), link);
  }

  @Test
  public void fetchLinkStringFromContainer() {
    assertEquals("Link string information", container.getLinks()
      .get(KEY)
      .getHref(), LINK_HREF);
  }

}
