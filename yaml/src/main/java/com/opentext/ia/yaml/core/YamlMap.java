/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;


/**
 * Type-safe access to a <a href="http://www.yaml.org/spec/1.2/spec.html">YAML</a> map.
 */
public class YamlMap {

  private static final int MAX_LINE_LENGTH = 80;

  private Map<String, Object> data;

  public static YamlMap from(YamlMap source) {
    return from(source.toString());
  }

  /**
   * Parses the given YAML.
   * @param yaml The YAML string to parse
   * @return A <code>YamlMap</code> corresponding to the provided YAML string
   */
  public static YamlMap from(String yaml) {
    if (yaml == null) {
      throw new IllegalArgumentException("Missing YAML");
    }
    try (InputStream input = streamOf(yaml)) {
      return from(input);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to parse YAML string", e);
    }
  }

  private static InputStream streamOf(String text) {
    return IOUtils.toInputStream(text, StandardCharsets.UTF_8);
  }

  /**
   * Parses the given YAML.
   * @param yaml The YAML stream to parse
   * @return A <code>YamlMap</code> corresponding to the provided YAML input stream
   */
  public static YamlMap from(InputStream yaml) {
    YamlMap result = new YamlMap();
    for (Object data : newLoader().loadAll(yaml)) {
      result.putAll(new YamlMap(data));
    }
    return result;
  }

  private static Yaml newLoader() {
    return new Yaml(new Constructor(), new Representer(), new DumperOptions(), new YamlTypeResolver());
  }

  /**
   * Parses the given YAML.
   * @param yaml The YAML file to parse
   * @return A <code>YamlMap</code> corresponding to the provided YAML file, or an empty <code>YamlMap</code> if the
   * file does not exist
   * @throws IOException When an I/O error occurs
   */
  public static YamlMap from(File yaml) throws IOException {
    if (!yaml.isFile()) {
      return new YamlMap();
    }
    try (InputStream input = new FileInputStream(yaml)) {
      return from(input);
    }
  }

  public YamlMap() {
    this(null);
  }

  @SuppressWarnings("unchecked")
  public YamlMap(Object data) {
    this.data = data instanceof Map ? (Map<String, Object>)data : new LinkedHashMap<>();
  }

  public boolean isEmpty() {
    return data.isEmpty();
  }

  public int size() {
    return data.size();
  }

  public YamlMap put(String key, Object value) {
    data.put(key, unpack(value));
    return this;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object unpack(Object value) {
    Object result = value;
    if (result instanceof YamlMap) {
      result = ((YamlMap)result).getRawData();
    }
    if (result instanceof Iterable) {
      Collection<Object> values = new ArrayList<>();
      ((Iterable)result).forEach(v -> values.add(unpack(v)));
      return values;
    }
    if (result instanceof Value) {
      result = ((Value)result).getRawData();
    }
    if (result instanceof String) {
      result = normalizeWhitespace((String)result);
    }
    return result;
  }

  private String normalizeWhitespace(String text) {
    StringBuilder result = new StringBuilder();
    String prefix = "";
    for (String line : text.split("(\n|\r)+")) {
      int end = line.length() - 1;
      while (end >= 0 && Character.isWhitespace(line.charAt(end))) {
        --end;
      }
      result.append(prefix);
      if (end >= 0) {
        result.append(line.substring(0, end + 1));
      }
      prefix = System.lineSeparator();
    }
    return result.toString();
  }

  Map<String, Object> getRawData() {
    return data;
  }

  public void putAll(YamlMap other) {
    data.putAll(other.data);
  }

  public boolean containsKey(String key) {
    return data.containsKey(key);
  }

  public YamlMap remove(String key) {
    data.remove(key);
    return this;
  }

  public Value get(Object... keys) {
    YamlMap map = this;
    int i = 0;
    while (i < keys.length - 1) {
      Value value = map.get(keys[i++]);
      if (value.isList()) {
        int index = (int)keys[i++];
        value = value.toList().get(index);
        if (i < keys.length) {
          map = value.toMap();
        } else {
          return value;
        }
      } else {
        map = value.toMap();
      }
    }
    return new Value(map.data.get(keys[keys.length - 1]));
  }

  public Stream<Value> values() {
    return entries().map(Entry::getValue);
  }

  public Stream<Entry> entries() {
    return data.entrySet().stream()
        .map(entry -> new Entry(this, entry.getKey(), new Value(entry.getValue())))
        .sorted((a, b) -> new DefaultYamlComparator().compare(a.getKey(), b.getKey()));
  }

  public void visit(Visitor visitor) {
    visit(visitor, new Visit(this));
  }

  private void visit(Visitor visitor, Visit visit) {
    boolean shouldVisit = visitor.test(visit);
    if (shouldVisit) {
      visitor.accept(visit);
    }
    if (visitor.maxNesting() > visit.getLevel()) {
      visit.getMap().entries().forEach(entry -> {
        String key = entry.getKey();
        Value value = entry.getValue();
        if (value.isMap()) {
          visit(visitor, visit.descend(key));
        } else if (isListOfMaps(value)) {
          IntStream.range(0, value.toList().size())
              .forEach(index -> visit(visitor, visit.descend(key, index)));
        }
      });
    }
    if (shouldVisit) {
      visitor.afterVisit(visit);
    }
  }

  private boolean isListOfMaps(Value value) {
    List<Value> values = value.toList();
    if (values.isEmpty()) {
      return false;
    }
    return values.size() == values.stream()
        .filter(Value::isMap)
        .count();
  }

  public InputStream toStream() {
    return streamOf(toString());
  }

  public YamlMap sort() {
    return sort(new DefaultYamlComparator());
  }

  public YamlMap sort(Comparator<String> comparator) {
    return sort(comparator, true);
  }

  public YamlMap sort(boolean recursive) {
    return sort(new DefaultYamlComparator(), recursive);
  }

  public YamlMap sort(Comparator<String> comparator, boolean recursive) {
    return sort(comparator, ignored -> true, recursive);
  }

  public YamlMap sort(Predicate<String> entriesFilter) {
    return sort(new DefaultYamlComparator(), entriesFilter, true);
  }

  public YamlMap sort(Comparator<String> comparator, Predicate<String> entriesFilter) {
    return sort(comparator, entriesFilter, true);
  }

  public YamlMap sort(Comparator<String> comparator, Predicate<String> entriesFilter, boolean recursive) {
    data = sortMap(data, comparator);
    if (recursive) {
      sortRecursively(data, comparator, entriesFilter);
    }
    return this;
  }

  private Map<String, Object> sortMap(Map<String, Object> source, Comparator<String> comparator) {
    Map<String, Object> result = new TreeMap<>(comparator);
    source.entrySet().forEach(entry -> result.put(entry.getKey(), entry.getValue()));
    if (source instanceof LinkedHashMap) {
      source.clear();
      source.putAll(result);
      return source;
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private void sortRecursively(Map<String, Object> map, Comparator<String> comparator,
      Predicate<String> entriesFilter) {
    map.keySet().stream()
        .filter(entriesFilter)
        .forEach(key -> {
          Object value = map.get(key);
          if (value instanceof Map) {
            sortMap(map, comparator, entriesFilter, key, (Map<String, Object>)value);
          } else if (value instanceof List) {
            sortList(map, comparator, entriesFilter, key, (List<?>)value);
          }
        });
  }

  private void sortMap(Map<String, Object> map, Comparator<String> comparator, Predicate<String> entriesFilter,
      String key, Map<String, Object> value) {
    Map<String, Object> sortedMap = sortMap(value, comparator);
    sortRecursively(sortedMap, comparator, entriesFilter);
    map.put(key, sortedMap);
  }

  private void sortList(Map<String, Object> map, Comparator<String> comparator, Predicate<String> entriesFilter,
      String key, List<?> list) {
    List<Object> sortedList = new ArrayList<>(list);
    ListContent content = new ListContent();
    for (int i = 0; i < sortedList.size(); i++) {
      sortListItem(sortedList, i, comparator, entriesFilter, content);
    }
    if (content.isAllMaps()) {
      Collections.sort(sortedList, new DefaultYamlSequenceComparator());
    }
    map.put(key, sortedList);
  }

  @SuppressWarnings("unchecked")
  private void sortListItem(List<Object> sortedList, int index, Comparator<String> comparator,
      Predicate<String> entriesFilter, ListContent content) {
    Object value = sortedList.get(index);
    if (value instanceof Map) {
      Map<String, Object> sortedMap = sortMap((Map<String, Object>)value, comparator);
      sortRecursively(sortedMap, comparator, entriesFilter);
      sortedList.set(index, sortedMap);
      content.setAllScalars(false);
    } else {
      content.setAllMaps(false);
    }
    if (content.isAllScalars()) {
      Collections.sort(sortedList, (a, b) -> String.valueOf(a).compareTo(String.valueOf(b)));
    }
  }

  public YamlMap replace(String key, Object newValue) {
    return put(key, newValue);
  }

  public YamlMap replace(String oldKey, String newKey, Object newValue) {
    Map<String, Object> newEntries = new LinkedHashMap<>();
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (oldKey.equals(entry.getKey())) {
        newEntries.put(newKey, unpack(newValue));
      } else {
        newEntries.put(entry.getKey(), entry.getValue());
      }
    }
    data.clear();
    data.putAll(newEntries);
    return this;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    appendMap(new YamlIndent(), hasMultipleNestedEntries(), data, result, new AtomicBoolean());
    return result.toString();
  }

  private boolean hasMultipleNestedEntries() {
    return size() > 1 && entries()
        .map(Entry::getValue)
        .anyMatch(value -> value.isMap() || value.isList());
  }

  private void appendMap(YamlIndent indent, boolean separateEntries, Map<String, Object> map, StringBuilder builder,
      AtomicBoolean differenceFound) {
    boolean hasOutput = false;
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (hasOutput && separateEntries) {
        builder.append(System.lineSeparator());
      }
      appendEntry(indent, entry, builder, differenceFound);
      hasOutput = true;
    }
    if (!hasOutput) {
      builder.append(indent).append("{ }").append(System.lineSeparator());
    }
  }

  @SuppressWarnings("unchecked")
  private void appendEntry(YamlIndent indent, Map.Entry<String, Object> entry, StringBuilder builder,
      AtomicBoolean differenceFound) {
    int len = builder.length();
    builder.append(indent);
    appendText(indent, builder.length() - len, entry.getKey(), builder);
    builder.append(':');
    Object value = entry.getValue();
    if (value instanceof Map) {
      builder.append(System.lineSeparator());
      appendMap(indent.inMap(), false, (Map<String, Object>)value, builder, differenceFound);
    } else if (value instanceof Collection) {
      appendCollection(indent, builder.length() - len, (Collection<?>)value, builder, differenceFound);
    } else {
      builder.append(' ');
      appendValue(indent, builder.length() - len, value, builder);
    }
  }

  private void appendText(YamlIndent indent, int currentLineLength, String text, StringBuilder builder) {
    if (text.isEmpty() || text.matches("([\"%@].*)|(.*#.*)")) {
      builder.append('\'').append(text.replace("'", "''")).append('\'');
    } else if (text.startsWith("'")) {
      builder.append('"').append(text.replace("\"", "\\\"")).append('"');
    } else if (MAX_LINE_LENGTH < currentLineLength + text.length()) {
      appendWrappedText(indent.inText(), currentLineLength, text, builder);
    } else {
      builder.append(text);
    }
  }

  private void appendWrappedText(YamlIndent indent, int currentLineLength, String text, StringBuilder builder) {
    int start = 0;
    int used = currentLineLength;
    while (start < text.length()) {
      int end = wordBreakAfter(text, start + MAX_LINE_LENGTH - used + 1);
      builder.append(text.substring(start, end));
      start = end + 1;
      while (start < text.length() && Character.isWhitespace(text.charAt(start))) {
        start++;
      }
      if (start < text.length()) {
        builder.append(System.lineSeparator()).append(indent);
        used = indent.length();
      }
    }
  }

  private int wordBreakAfter(String text, int start) {
    int result = text.indexOf(' ', start);
    if (result < 0) {
      result = text.length();
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private void appendCollection(YamlIndent indent, int currentLineLength, Collection<?> collection,
      StringBuilder builder, AtomicBoolean differenceFound) {
    int len = builder.length();
    if (collection.isEmpty()) {
      builder.append(" [ ]").append(System.lineSeparator());
    } else {
      builder.append(System.lineSeparator());
      for (Object item : collection) {
        if (item instanceof Map) {
          appendMap(indent.inSequence(), false, (Map<String, Object>)item, builder, differenceFound);
        } else {
          builder.append(indent).append("- ");
          appendValue(indent, currentLineLength + builder.length() - len, item, builder);
        }
      }
    }
  }

  private void appendValue(YamlIndent indent, int currentLineLength, Object value, StringBuilder builder) {
    if (value instanceof String) {
      String text = ((String)value).trim().replace("\t", "  ");
      String[] lines = text.split("\\s*(\n|\r)+");
      if (lines.length > 1) {
        builder.append('|');
        for (String line : lines) {
          builder.append(System.lineSeparator()).append(indent).append("  ").append(line);
        }
      } else {
        appendText(indent, currentLineLength, text, builder);
      }
    } else {
      builder.append(value);
    }
    builder.append(System.lineSeparator());
  }


  private static class ListContent {

    private boolean allScalars = true;
    private boolean allMaps = true;

    boolean isAllScalars() {
      return allScalars;
    }

    void setAllScalars(boolean allScalars) {
      this.allScalars = allScalars;
    }

    boolean isAllMaps() {
      return allMaps;
    }

    void setAllMaps(boolean allMaps) {
      this.allMaps = allMaps;
    }

  }

}
