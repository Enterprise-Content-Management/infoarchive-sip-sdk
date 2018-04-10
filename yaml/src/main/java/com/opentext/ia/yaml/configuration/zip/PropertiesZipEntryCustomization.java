/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.BiConsumer;


/**
 * Customize ZIP entries that are .properties files.
 * @since 10.1.0
 */
public class PropertiesZipEntryCustomization implements ZipEntryCustomization {

  private final BiConsumer<String, Properties> propertiesCustomizer;
  private String name;

  public PropertiesZipEntryCustomization(BiConsumer<String, Properties> propertiesCustomizer) {
    this.propertiesCustomizer = propertiesCustomizer;
  }

  @Override
  public boolean matches(String path) {
    this.name = path;
    return path.endsWith(".properties");
  }

  @Override
  public InputStream customize(InputStream input) throws IOException {
    Properties properties = new Properties();
    properties.load(input);
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      propertiesCustomizer.accept(name, properties);
      properties.store(output, null);
      return new ByteArrayInputStream(output.toByteArray());
    }
  }

}
