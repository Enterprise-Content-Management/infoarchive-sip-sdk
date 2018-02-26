/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import org.json.JSONArray;
import org.json.JSONObject;

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
    JSONObject content = new JSONObject();
    content.put("format", format);
    content.put("text", text);
    JSONArray contents;
    if (hasProperty(CONTENT_PROPERTY)) {
      contents = (JSONArray)getProperty(CONTENT_PROPERTY);
    } else {
      contents = new JSONArray();
      setProperty(CONTENT_PROPERTY, contents);
    }
    contents.put(content);
  }

}
