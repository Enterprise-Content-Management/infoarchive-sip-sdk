/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepeatingConfigReader {

  private final String name;
  private final List<String> fields;

  public RepeatingConfigReader(String name, List<String> fields) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("RepeatingConfigReader.name cannot be null/empty.");
    }
    if (fields == null || fields.isEmpty()) {
      throw new IllegalArgumentException("RepeatingConfigReader.fields cannot be null/empty.");
    }

    this.name = name;
    this.fields = fields;
  }

  public List<Map<String, String>> read(Map<String, String> configuration) {
    List<String[]> values = new ArrayList<>();
    List<Integer> sizes = new ArrayList<>(values.size());
    int nullCount = 0;
    for (String field : fields) {
      String rawFieldValue = configuration.get(field);
      if (rawFieldValue == null) {
        rawFieldValue = "";
        nullCount += 1;
      }
      String[] fieldValues = rawFieldValue.split(",");
      values.add(fieldValues);
      sizes.add(fieldValues.length);
    }

    if (nullCount == fields.size()) {
      return Collections.emptyList();
    }

    long valueCount = sizes.stream()
      .distinct()
      .count();
    if (valueCount > 1) {
      throw new IllegalArgumentException(formatErrorMessage(values, sizes));
    }

    List<Map<String, String>> list = convertToListOfMaps(values);

    return list;
  }

  private List<Map<String, String>> convertToListOfMaps(List<String[]> values) {
    int numValues = values.get(0).length;
    List<Map<String, String>> list = new ArrayList<>();

    for (int valueIndex = 0; valueIndex < numValues; ++valueIndex) {
      Map<String, String> map = new HashMap<>();
      for (int i = 0; i < fields.size(); ++i) {
        String[] fieldValue = values.get(i);
        map.put(fields.get(i), fieldValue[valueIndex]);
      }
      list.add(map);
    }
    return list;
  }

  private String formatErrorMessage(List<String[]> values, List<Integer> sizes) {
    StringBuilder result = new StringBuilder(256);
    result.append("All configuration items in the ");
    result.append(name);
    result.append(" configuration group must have the same number of values. Number of values found for each item: ");
    for (int i = 0; i < fields.size(); ++i) {
      if (i > 0) {
        result.append(", ");
      }
      result.append(fields.get(i));
      result.append(": ");
      result.append(sizes.get(i));
      result.append(' ');
      result.append(Arrays.toString(values.get(i)));
    }
    result.append('.');
    return result.toString();
  }
}
