/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.opentext.ia.yaml.configuration.YamlConfiguration;


/**
 * Convert a ZIP containing configuration files into a {@linkplain YamlConfiguration}.
 * @since 9.14.0
 */
public class ZipToYamlConfiguration implements Function<InputStream, YamlConfiguration> {

  @Override
  public YamlConfiguration apply(InputStream zip) {
    try {
      return extract(new RandomAccessZipFile(zip));
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read ZIP", e);
    }
  }

  private YamlConfiguration extract(Map<String, InputStream> streamsByName) {
    return new YamlConfiguration(getConfigurationFileFrom(streamsByName),
        new StreamsByNameResourceResolver(streamsByName));
  }

  private InputStream getConfigurationFileFrom(Map<String, InputStream> streamsByFileName) {
    String fileName;
    if (streamsByFileName.containsKey(ZipConfiguration.MAIN_YAML_FILE_NAME)) {
      fileName = ZipConfiguration.MAIN_YAML_FILE_NAME;
    } else {
      fileName = findKeyBySuffix(streamsByFileName, ".yml")
          .orElseGet(() -> findKeyBySuffix(streamsByFileName, ".yaml")
              .orElseThrow(() -> new IllegalArgumentException("Missing .yml or .yaml file")));
    }
    return streamsByFileName.get(fileName);
  }

  private Optional<String> findKeyBySuffix(Map<String, InputStream> items, String suffix) {
    return items.keySet().stream()
        .filter(name -> name.endsWith(suffix))
        .findFirst();
  }

}
