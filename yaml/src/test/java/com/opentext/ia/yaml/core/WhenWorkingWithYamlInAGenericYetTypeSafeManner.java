/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.opentext.ia.test.TestCase;
import com.opentext.ia.test.TestUtil;


public class WhenWorkingWithYamlInAGenericYetTypeSafeManner extends TestCase {

  private static final String TYPE = "type";
  private static final String EMPTY = "Empty";
  private static final String NAME = "name";
  private static final String SAMPLE_YAML_STRING = String.format(
      "root:%n- property: value%n  sequence:%n  - one%n  - two%n  nested:%n    foo: bar%n");

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private final YamlMap yaml = new YamlMap();
  private final String key = 'k' + someValue();
  private final String value = 'v' + someValue();

  private String someValue() {
    return randomString(10);
  }

  @Test
  public void shouldStartEmpty() {
    assertTrue("Not empty", yaml.isEmpty());
    assertEquals("Size", 0, yaml.size());
    assertTrue("Value found", yaml.get(someValue()).isEmpty());
  }

  @Test
  public void shouldBeAbleToAddItems() {
    yaml.put(key, value);

    assertFalse(EMPTY, yaml.isEmpty());
    assertEquals("Size", 1, yaml.size());
    assertValue();
  }

  private void assertValue() {
    assertValue(value);
  }

  private void assertValue(Object expected) {
    assertValue(expected, yaml);
  }

  private void assertValue(Object expected, YamlMap map) {
    assertValue(expected, map.get(key));
  }

  private void assertValue(Object expected, Value actual) {
    if (!actual.equals(expected)) {
      assertEquals("Value", expected, actual);
    }
  }

  @Test
  public void shouldLoadFromOtherMap() {
    YamlMap map = new YamlMap(Collections.singletonMap(key, value));
    assertValue(value, map);

    yaml.putAll(map);
    assertValue();
  }

  @Test
  public void shouldLoadValueFromOtherMap() {
    YamlMap map = new YamlMap().put(key, value);
    Value v = map.get(key);

    yaml.put(key, v);

    assertValue();
    assertValue(v);
  }

  @Test
  public void shouldRemoveValue() {
    assertFalse("Contains non-added value", yaml.containsKey(key));

    yaml.put(key, value);
    assertTrue("Doesn't contain added value", yaml.containsKey(key));

    yaml.remove(key);
    assertFalse("Still contains removed value", yaml.containsKey(key));
  }

  @Test
  public void shouldRetrieveNestedValues() {
    String outerKey = someValue();
    YamlMap map = new YamlMap(Collections.singletonMap(outerKey, Collections.singletonMap(key, value)));

    assertValue(value, map.get(outerKey, key));
  }

  @Test
  public void shouldIterateOverEntriesAndValues() {
    String key1 = 'z' + someValue();
    String value1 = someValue();
    String key2 = 'a' + someValue();
    String value2 = someValue();
    yaml.put(key1, value1);
    yaml.put(key2, value2);

    assertEquals("Keys", key2 + ',' + key1, yaml.entries()
        .sorted()
        .map(Entry::getKey)
        .collect(Collectors.joining(",")));
    assertEquals("Values", value2 + ',' + value1, yaml.entries()
        .sorted()
        .map(Entry::getValue)
        .map(Value::toString)
        .collect(Collectors.joining(",")));
  }

  @Test
  public void shouldExtractNamedNestedObject() {
    String name = someValue();
    yaml.put(name, new YamlMap().put(key, value));

    YamlMap nestedObject = yaml.entries()
        .map(Entry::toMap)
        .findFirst()
        .get();

    assertEquals("Name", name, nestedObject.get(NAME).toString());
    assertEquals("Property", value, nestedObject.get(key).toString());
  }

  @Test
  public void shouldCheckWhetherValueExists() {
    yaml.put(key, null);
    assertTrue("Null value", yaml.get(key).isEmpty());

    yaml.put(key, value);
    assertFalse("Still null value", yaml.get(key).isEmpty());
  }

  @Test
  public void shouldConvertValueToMap() {
    yaml.put(key, value);
    assertFalse("String is a map", yaml.get(key).isMap());

    yaml.put(key, new YamlMap().put(key, value));
    Value v = yaml.get(key);
    assertTrue("Map is not a map", v.isMap());
    YamlMap map = v.toMap();
    assertValue(value, map);

    String value2 = someValue();
    map.put(key, value2);
    assertValue(value2, yaml.get(key, key));
  }

  @Test
  public void shouldConvertValueToList() {
    assertTrue("Empty by default", yaml.get(key).toList().isEmpty());
    yaml.put(key, value);
    assertFalse("String is a list", yaml.get(key).isList());

    String value2 = someValue();
    yaml.put(key, Arrays.asList(value, value2));
    Value v = yaml.get(key);
    assertTrue("List is not a list", v.isList());
    List<Value> values = v.toList();
    assertValue(value, values.get(0));
    assertValue(value2, values.get(1));
  }

  @Test
  public void shouldConvertValueToBoolean() {
    assertFalse(EMPTY, yaml.get(key).toBoolean());

    yaml.put(key, true);
    assertTrue("Boolean", yaml.get(key).toBoolean());

    yaml.put(key, Boolean.toString(true));
    assertTrue("Boolean string", yaml.get(key).toBoolean());
  }

  @Test
  public void shouldConvertValueToInt() {
    assertEquals(EMPTY, 0, yaml.get(key).toInt());

    yaml.put(key, 42);
    assertEquals("Int", 42, yaml.get(key).toInt());

    yaml.put(key, Integer.toString(313));
    assertEquals("Integer string", 313, yaml.get(key).toInt());
  }

  @Test
  public void shouldConvertValueToDouble() {
    assertEquals(EMPTY, 0.0, yaml.get(key).toDouble(), 1e-6);

    yaml.put(key, Math.PI);
    assertEquals("Double", Math.PI, yaml.get(key).toDouble(), 1e-6);

    yaml.put(key, Double.toString(Math.E));
    assertEquals("Double string", Math.E, yaml.get(key).toDouble(), 1e-6);
  }

  @Test
  public void shouldTestForScalarValue() {
    assertFalse(EMPTY, new Value(null).isScalar());
    assertFalse("List", new Value(Collections.singletonList(randomString())).isScalar());
    assertFalse("Map", new Value(Collections.singletonMap(randomString(), randomString())).isScalar());

    assertTrue("Boolean", new Value(true).isScalar());
    assertTrue("String", new Value(randomString()).isScalar());
    assertTrue("Int", new Value(313).isScalar());
    assertTrue("Double", new Value(Math.PI).isScalar());
  }

  @Test
  public void shouldSerializeToAndDeserializeFromString() {
    assertEquals("String representation", SAMPLE_YAML_STRING, YamlMap.from(SAMPLE_YAML_STRING).toString());
  }

  @Test
  public void shouldSkipNullValuesWhenSerializing() {
    assertEquals("String representation", String.format("foo:%n  ape: bear%n"),
        new YamlMap().put("foo", new YamlMap().put("ape", "bear").put("cheetah", null)).toString());
  }

  @Test
  public void shouldLoadYamlFromFile() throws IOException {
    File yamlFile = temporaryFolder.newFile();
    try (PrintWriter writer = new PrintWriter(yamlFile, StandardCharsets.UTF_8.name())) {
      writer.print(SAMPLE_YAML_STRING);
    }

    assertEquals("YAML from file", SAMPLE_YAML_STRING, YamlMap.from(yamlFile).toString());
  }

  @Test
  public void shouldReturnEmptyMapWhenLoadingFromNonExistingFile() throws IOException {
    File nonExistingFile = temporaryFolder.newFile();
    nonExistingFile.delete();

    assertTrue("YAML loaded from non-existing file is not empty", YamlMap.from(nonExistingFile).isEmpty());
  }

  @Test
  public void shouldSerializeToStream() throws IOException {
    assertEquals("YAML from stream", SAMPLE_YAML_STRING,
        IOUtils.toString(YamlMap.from(SAMPLE_YAML_STRING).toStream(), StandardCharsets.UTF_8));
  }

  @Test
  public void shouldDeleteListItem() {
    yaml.put(key, Arrays.asList(value));
    List<Value> values = yaml.get(key).toList();

    values.remove(0);

    assertTrue("Sequence should be empty after removing only item", yaml.get(key).toList().isEmpty());
  }

  @Test
  public void shouldIterateList() {
    String value2 = '2' + someValue();
    yaml.put(key, Arrays.asList(value, value2));

    ListIterator<Value> iterator = yaml.get(key).toList().listIterator();
    assertEquals("Item #1", value, iterator.next().toString());

    iterator.remove();
    assertEquals("Item after remove", value2, iterator.next().toString());

    assertEquals("Previous item", value2, iterator.previous().toString());

    iterator.remove();
    assertTrue("List should be empty after removing all values", yaml.get(key).toList().isEmpty());
  }

  @Test
  public void shouldSortWithDefaultComparator() throws Exception {
    yaml.put("cheetah", "dingo");
    yaml.put("ape", "bear");

    assertSorted("ape: bear%ncheetah: dingo%n");
  }

  private void assertSorted(String expected) {
    assertYaml(expected, yaml.sort());
  }

  private void assertYaml(String expected, YamlMap actual) {
    assertEquals("YAML", String.format(expected), actual.toString());
  }

  @Test
  public void shouldSortWithProvidedComparator() throws Exception {
    yaml.put("elephant", "fox");
    yaml.put("giraffe", "hyena");

    assertYaml("giraffe: hyena%nelephant: fox%n", yaml.sort((a, b) -> b.toString().compareTo(a.toString())));
  }

  @Test
  public void shouldSortRecursively() throws Exception {
    yaml.put("iguana", new YamlMap()
        .put("leopard", Arrays.asList(new YamlMap()
            .put("opossum", "parrot")
            .put("mule", "nightingale")))
        .put("jaguar", "koala")
        .put("rhino", Arrays.asList("tiger", "snake")));

    assertSorted("iguana:%n  jaguar: koala%n  leopard:%n  - mule: nightingale"
        + "%n    opossum: parrot%n  rhino:%n  - snake%n  - tiger%n");
  }

  @Test
  public void shouldSortSequencesByName() {
    yaml.put("unicorn", Arrays.asList(
        new YamlMap().put(NAME, "whale").put(TYPE, "a"),
        new YamlMap().put(NAME, "velociraptor").put(TYPE, "e"),
        new YamlMap().put(NAME, "velociraptor").put(TYPE, "b").put("s", "c"),
        new YamlMap().put(NAME, "velociraptor").put(TYPE, "d")));

    assertSorted("unicorn:%n"
        + "- name: velociraptor%n  type: d%n"
        + "- name: velociraptor%n  type: e%n"
        + "- name: velociraptor%n  s: c%n  type: b%n"
        + "- name: whale%n  type: a%n");
  }

  @Test
  public void shouldSortButKeepTopLevelAsIs() {
    yaml.put("bear", new YamlMap()
            .put("elephant", "fox")
            .put("cheetah", "dingo"))
        .put("ape", new YamlMap()
            .put("giraffe", "hyena"));

    assertYaml("bear:%n"
        + "  cheetah: dingo%n"
        + "  elephant: fox%n"
        + "ape:%n"
        + "  giraffe: hyena%n", yaml.sort(false));
  }

  @Test
  public void shouldVisitMap() throws Exception {
    yaml.put("aardvark", Arrays.asList(
        new YamlMap()
            .put("bee", "cobra")
            .put("dog", new YamlMap()
                .put("emu", new YamlMap()
                    .put("falcon", false))),
        new YamlMap()
            .put("gazelle", "hamster"),
        new YamlMap()
            .put("ibis", "jackal")));

    Collection<String> visitedPaths = new ArrayList<>();
    yaml.visit(new Visitor() {
      @Override
      public int maxNesting() {
        return 3;
      }

      @Override
      public boolean test(Visit visit) {
        return !visit.getMap().containsKey("ibis");
      }

      @Override
      public void accept(Visit visit) {
        visitedPaths.add(visit.getPath());
      }

      @Override
      public void afterVisit(Visit visit) {
        visitedPaths.add("@" + visit.getPath());
      }
    });

    TestUtil.assertEquals("Visited paths", Arrays.asList("/", "/aardvark/0", "/aardvark/0/dog", "@/aardvark/0/dog",
        "@/aardvark/0", "/aardvark/1", "@/aardvark/1", "@/"), visitedPaths);
  }

  @Test
  public void shouldStripEndingWhitespace() {
    yaml.put("mongoose", "narwhal  ");

    assertEquals("Text", "narwhal", yaml.get("mongoose").toString());
  }

  @Test
  public void shouldStripWhitespaceAfterLineBreaks() {
    yaml.put("okapi", "panda  \nquail");

    assertEquals("Text", "panda\nquail", yaml.get("okapi").toString());
  }

  @Test
  public void shouldMaintainOrderWhenReplacingEntries() {
    yaml.put("rabbit", "scorpion")
        .put("tapir", "uakari")
        .replace("rabbit", "vulture", "warthog");

    assertYaml("vulture: warthog%ntapir: uakari%n", yaml);
  }

}
