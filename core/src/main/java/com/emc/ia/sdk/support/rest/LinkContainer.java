/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
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

  public String getSelfUri() {
    return getUri(StandardLinkRelations.LINK_SELF);
  }

  public String getUri(String linkRelation) {
    Link selfLink = links.get(linkRelation);
    return selfLink == null ? null : selfLink.getHref();
  }

  @Override
  public String toString() {
    return links.toString();
  }

}
