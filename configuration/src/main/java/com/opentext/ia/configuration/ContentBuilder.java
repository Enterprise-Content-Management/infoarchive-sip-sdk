/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.io.IOUtils;


/**
 * Build a content object.
 * @author Ray Sinnema
 * @since 9.13.0
 */
public class ContentBuilder<P extends ContentObjectBuilder<?, P, ?>> {

  private final ContentObjectBuilder<?, P, ?> parent;
  private String text;
  private String format;

  protected ContentBuilder(ContentObjectBuilder<?, P, ?> parent, String defaultFormat) {
    this.format = defaultFormat;
    this.parent = Objects.requireNonNull(parent, "Missing parent");
  }

  public ContentBuilder<P> ofType(String type) {
    format = type;
    return this;
  }

  /**
   * Sets the content.
   * @param content the content to add
   * @return this builder
   */
  @SuppressWarnings("unchecked")
  public ContentBuilder<P> as(String content) {
    this.text = content;
    return this;
  }

  /**
   * Sets the content.
   * @param content the content to add
   * @return this builder
   */
  public ContentBuilder<P> as(InputStream content) {
    try {
      return as(IOUtils.toString(content, StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read content", e);
    }
  }

  /**
   * Sets the content from a named resource.
   * @param name the name of the content resource to add
   * @return this builder
   */
  public ContentBuilder<P> fromResource(String name) {
    try (InputStream content = getClass().getResourceAsStream(name)) {
      return as(content);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read resource: " + name, e);
    }
  }

  @SuppressWarnings("unchecked")
  public P end() {
    parent.addContent(format, text);
    return (P)parent;
  }

}
