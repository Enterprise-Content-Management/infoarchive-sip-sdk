/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;


class YamlIndent {

  private static final String MAP_INDENT = "  ";
  private static final String SEQUENCE_INDENT = "- ";

  private final String indent;
  private final String firstIndent;
  private boolean isFirst = true;

  YamlIndent() {
    this("");
  }

  private YamlIndent(String indent) {
    this.firstIndent = indent;
    this.indent = indent.replace('-', ' ');
  }

  public String removeFrom(String text) {
    return text.length() > indent.length() ? text.substring(indent.length()) : text;
  }

  public YamlIndent inMap() {
    return new YamlIndent(indent + MAP_INDENT);
  }

  public YamlIndent inSequence() {
    return new YamlIndent(indent + SEQUENCE_INDENT);
  }

  public YamlIndent inText() {
    return inMap();
  }

  public int length() {
    return indent.length();
  }

  @Override
  public String toString() {
    String result = isFirst ? firstIndent : indent;
    isFirst = false;
    return result;
  }

}
