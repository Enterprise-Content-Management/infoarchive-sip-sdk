/*
 * Copyright (c) 2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.multiaiu;

import com.opentext.ia.sdk.support.xml.XmlBuilder;


public class Animal implements Aiu {

  private final String name;

  public Animal(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public void addTo(XmlBuilder<Void> builder) {
    builder.element("animal")
        .element("name", getName())
    .end();
  }

}
