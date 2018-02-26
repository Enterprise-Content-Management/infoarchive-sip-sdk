/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Builder for configuration objects with content.
 * @author Ray Sinnema
 * @since 9.13.0
 *
 * @param <P> The type of the parent builder
 * @param <C> The type of configuration to build
 */
public class ContentObjectBuilder<P extends BaseBuilder<?, C>, S extends ContentObjectBuilder<P, S, C>,
    C extends Configuration<?>> extends NamedObjectBuilder<P, S, C> {

  private static final String CONTENT_PROPERTY = "content";

  protected ContentObjectBuilder(P parent, String type) {
    super(parent, type);
  }

  protected ContentBuilder<S> withContent(String defaultFormat) {
    return new ContentBuilder<>(this, defaultFormat);
  }

  public void addContent(String format, String text) {
    if (hasProperty(CONTENT_PROPERTY)) {
      Object current = getProperty(CONTENT_PROPERTY);
      if (current instanceof String) {
        Collection<String> contents = new ArrayList<>();
        contents.add((String)current);
        contents.add(text);
        setProperty(CONTENT_PROPERTY, contents);
      } else {
        @SuppressWarnings("unchecked")
        Collection<String> contents = (Collection<String>)current;
        contents.add(text);
      }
    } else {
      setProperty(CONTENT_PROPERTY, text);
    }
  }

}
