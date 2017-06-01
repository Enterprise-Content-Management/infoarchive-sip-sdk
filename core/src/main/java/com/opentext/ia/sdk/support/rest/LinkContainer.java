/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.rest;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * A hypermedia document, aka a document with {@linkplain Link hyperlinks}. The links are stored in the
 * <code>_links</code> property, as in <a href="http://stateless.co/hal_specification.html">Hypermedia Application
 * Language</a> (HAL).
 */
public class LinkContainer {

  @JsonProperty("_links")
  private Map<String, Link> links = new HashMap<>();

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
