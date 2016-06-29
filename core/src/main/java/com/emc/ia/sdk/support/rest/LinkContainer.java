/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class LinkContainer {

  @JsonProperty("_links")
  private Map<String, Link> links = new HashMap<String, Link>();

  public Map<String, Link> getLinks() {
    return links;
  }

  public void setLinks(Map<String, Link> links) {
    this.links = links;
  }

  public String getUri() {
    Link selfLink = links.get(StandardLinkRelations.LINK_SELF);
    return selfLink == null ? null : selfLink.getHref();
  }

  @Override
  public String toString() {
    return links.toString();
  }

}
