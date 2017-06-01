/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

import java.util.Objects;


/**
 * An HTTP <a href="https://tools.ietf.org/html/rfc7231#section-5">header</a>.
 */
public class Header implements Comparable<Header> {

  private final String name;
  private final String value;

  public Header(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  @Override
  public int compareTo(Header other) {
    return name.compareTo(other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Header other = (Header)obj;
    return Objects.equals(name, other.name) && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return String.format("%s=%s", name, value);
  }

}
