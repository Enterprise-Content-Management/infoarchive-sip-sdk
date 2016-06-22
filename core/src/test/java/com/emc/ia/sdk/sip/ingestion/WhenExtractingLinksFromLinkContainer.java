/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */

package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class WhenExtractingLinksFromLinkContainer {
  
  private final LinkContainer container = new LinkContainer();
  private final static String LINKSTR = "https://india.emc.com/content-management/infoarchive/infoarchive.htm";
  private final Link link = new Link(); 
  private final Map<String, Link> links = new HashMap<String, Link>();
  private final static String KEY = "Test";
  
  @Before
  public void init()
  {
    link.setHref(LINKSTR);
    links.put(KEY, link);
    container.setLinks(links);
  }
  
  @Test
  public void fetchLinkObjectFromContainer() {
    assertEquals("Link bject information", container.getLinks().get(KEY), link);
  }
  
  @Test
  public void fetchLinkStringFromContainer() {
    assertEquals("Link string information", container.getLinks().get(KEY).getHref(), LINKSTR);
  }
  
  @Test
  public void validateLinkToStringValue() {
    assertEquals("Link ToString check", container.getLinks().get(KEY).toString(), "Link [href=" + LINKSTR + " ]");
  }
  
}
