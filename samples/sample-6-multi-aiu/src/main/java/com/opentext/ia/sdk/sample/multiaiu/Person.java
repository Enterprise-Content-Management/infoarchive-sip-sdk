/*
 * Copyright (c) 2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.multiaiu;

import com.opentext.ia.sdk.support.xml.XmlBuilder;


public class Person implements Aiu {

  private final String firstName;
  private final String lastName;

  public Person(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public void addTo(XmlBuilder<Void> builder) {
    builder.element("person")
        .element("first-name", getFirstName())
        .element("last-name", getLastName())
    .end();
  }

}
