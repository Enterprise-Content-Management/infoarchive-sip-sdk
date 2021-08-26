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
import com.opentext.ia.sdk.support.http.rest.LinkContainer;


public class WhenExtractingLinksFromLinkContainer {

  private static final String LINK_HREF = "http://documentum.opentext.com/infoarchive/";
  private static final String KEY = "Test";

  private final LinkContainer container = new LinkContainer();
  private final Link link = new Link();
  private final Map<String, Link> links = new HashMap<>();

  @BeforeEach
  public void init() {
    link.setHref(LINK_HREF);
    links.put(KEY, link);
    container.setLinks(links);
  }

  @Test
  public void fetchLinkObjectFromContainer() {
    assertEquals(link, container.getLinks().get(KEY), "Link object information");
  }

  @Test
  public void fetchLinkStringFromContainer() {
    assertEquals(LINK_HREF, container.getLinks().get(KEY).getHref(), "Link string information");
  }

}
