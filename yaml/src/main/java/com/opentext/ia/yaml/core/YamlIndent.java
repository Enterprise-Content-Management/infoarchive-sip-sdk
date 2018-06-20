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
    return text.length() > this.indent.length() ? text.substring(this.indent.length()) : text;
  }

  public YamlIndent inMap() {
    return new YamlIndent(this.indent + MAP_INDENT);
  }

  public YamlIndent inSequence() {
    return new YamlIndent(this.indent + SEQUENCE_INDENT);
  }

  public YamlIndent inText() {
    return inMap();
  }

  public int length() {
    return this.indent.length();
  }

  @Override
  public String toString() {
    String result = this.isFirst ? this.firstIndent : this.indent;
    this.isFirst = false;
    return result;
  }
}
