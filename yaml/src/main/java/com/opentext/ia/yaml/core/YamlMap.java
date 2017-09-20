/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;


/**
 * Type-safe access to a <a href="http://www.yaml.org/spec/1.2/spec.html">YAML</a> map.
 */
public class YamlMap {

  private static final String NL = System.lineSeparator();

  private final Map<String, Object> data;

  /**
   * Parses the given YAML.
   * @param yaml The YAML string to parse
   * @return A <code>YamlMap</code> corresponding to the provided YAML string
   */
  public static YamlMap from(String yaml) {
    try (InputStream input = streamOf(yaml)) {
      return from(input);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to parse YAML string", e);
    }
  }

  private static InputStream streamOf(String text) {
    return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Parses the given YAML.
   * @param yaml The YAML stream to parse
   * @return A <code>YamlMap</code> corresponding to the provided YAML input stream
   */
  public static YamlMap from(InputStream yaml) {
    YamlMap result = new YamlMap();
    for (Object data : new Yaml().loadAll(yaml)) {
      result.putAll(new YamlMap(data));
    }
    return result;
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
    AtomicReference<String> prefix = new AtomicReference<>("");
    Arrays.stream(text.split("(\n|\r)+")).forEach(line -> {
      int end = line.length() - 1;
      while (end > 0 && Character.isWhitespace(line.charAt(end))) {
        --end;
      }
      result.append(prefix.get()).append(line.substring(0, end + 1));
      prefix.set(NL);
    });
    return result.toString();
  }

  private Map<String, Object> getRawData() {
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
    Map<String, Object> result = sortedCopyOf(data, comparator);
    if (recursive) {
      sortRecursively(result, comparator);
    }
    return new YamlMap(result);
  }

  private Map<String, Object> sortedCopyOf(Map<String, Object> source, Comparator<String> comparator) {
    Map<String, Object> result = new TreeMap<>(comparator);
    result.putAll(source);
    return result;
  }

  @SuppressWarnings("unchecked")
  private void sortRecursively(Map<String, Object> map, Comparator<String> comparator) {
    map.keySet().forEach(key -> {
      Object value = map.get(key);
      if (value instanceof Map) {
        sortMap(map, comparator, key, (Map<String, Object>)value);
      } else if (value instanceof List) {
        sortList(map, comparator, key, (List<?>)value);
      }
    });
  }

  private void sortMap(Map<String, Object> map, Comparator<String> comparator, String key, Map<String, Object> value) {
    Map<String, Object> sortedMap = sortedCopyOf(value, comparator);
    sortRecursively(sortedMap, comparator);
    map.put(key, sortedMap);
  }

  private void sortList(Map<String, Object> map, Comparator<String> comparator, String key, List<?> list) {
    List<Object> sortedList = new ArrayList<>(list);
    ListContent content = new ListContent();
    for (int i = 0; i < sortedList.size(); i++) {
      sortListItem(sortedList, i, comparator, content);
    }
    if (content.isAllMapsWithNames()) {
      Collections.sort(sortedList, new DefaultYamlSequenceComparator());
    }
    map.put(key, sortedList);
  }

  @SuppressWarnings("unchecked")
  private void sortListItem(List<Object> sortedList, int index, Comparator<String> comparator, ListContent content) {
    Object value = sortedList.get(index);
    if (value instanceof Map) {
      Map<String, Object> sortedMap = sortedCopyOf((Map<String, Object>)value, comparator);
      sortRecursively(sortedMap, comparator);
      sortedList.set(index, sortedMap);
      content.setAllScalars(false);
      if (!sortedMap.containsKey("name")) {
        content.setAllMapsWithNames(false);
      }
    } else {
      content.setAllMapsWithNames(false);
    }
    if (content.isAllScalars()) {
      Collections.sort(sortedList, (a, b) -> String.valueOf(a).compareTo(String.valueOf(b)));
    }
  }

  @Override
  public String toString() {
    return separateTopLevelSections(yamlToString());
  }

  private String yamlToString() {
    return new Yaml(new NullSkippingRepresenter(), prettyFlowBlockOptions()).dump(data);
  }

  private DumperOptions prettyFlowBlockOptions() {
    DumperOptions result = new DumperOptions();
    result.setDefaultFlowStyle(FlowStyle.BLOCK);
    result.setPrettyFlow(true);
    return result;
  }

  private String separateTopLevelSections(String yaml) {
    StringBuilder result = new StringBuilder();
    StringTokenizer tokenizer = new StringTokenizer(yaml, "\n\r");
    while (tokenizer.hasMoreTokens()) {
      String line = tokenizer.nextToken();
      if (isTopLevel(line)) {
        result.append(NL);
      }
      result.append(line).append(NL);
    }
    return result.toString().trim() + NL;
  }

  private boolean isTopLevel(String line) {
    if (line.isEmpty()) {
      return true;
    }
    char firstChar = line.charAt(0);
    return !Character.isWhitespace(firstChar) && firstChar != '-';
  }


  private static class ListContent {

    private boolean allScalars = true;
    private boolean allMapsWithNames = true;

    boolean isAllScalars() {
      return allScalars;
    }

    void setAllScalars(boolean allScalars) {
      this.allScalars = allScalars;
    }

    boolean isAllMapsWithNames() {
      return allMapsWithNames;
    }

    void setAllMapsWithNames(boolean allMapsWithNames) {
      this.allMapsWithNames = allMapsWithNames;
    }

  }


  private static class NullSkippingRepresenter extends Representer {

    NullSkippingRepresenter() {
      this.multiRepresenters.put(Map.class, data ->
          representMapping(getTag(data.getClass(), Tag.MAP), filterNullValues(data), null));
    }

    private Map<?, ?> filterNullValues(Object data) {
      Map<?, ?> result = (Map<?, ?>)data;
      if (result.containsValue(null)) {
        result = removePropertiesWithoutValue(result);
      }
      return result;
    }

    private Map<?, ?> removePropertiesWithoutValue(Map<?, ?> source) {
      Map<?, ?> result = new LinkedHashMap<>(source);
      Collection<?> propertiesWithoutValue = result.keySet().stream()
          .filter(key -> result.get(key) == null)
          .collect(Collectors.toList());
      propertiesWithoutValue.forEach(result::remove);
      return result;
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

}
