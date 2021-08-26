/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.Yaml;

import com.opentext.ia.test.TestCase;
import com.opentext.ia.test.TestUtil;

public class WhenWorkingWithYamlInAGenericYetTypeSafeManner extends TestCase {

  private static final String TYPE = "type";
  private static final String EMPTY = "Empty";
  private static final String NAME = "name";
  private static final String SEMICOLON = ": ";
  private static final String SAMPLE_YAML_STRING = String.format(
      "root:%n- property: value%n  sequence:%n  - one%n  - two%n  nested:%n    foo: bar%n  key: 'value: with: colons'%n");
  private static final String LAST_MODIFIED_DATE_NAME = "lastModifiedDate";
  private static final String NESTED_ITEM_NAME = "nestedItem";

  private static final String STILL_CONTAINS_REMOVED_VALUE = "Still contains removed value";
  private static final String DOESN_T_CONTAIN_ADDED_VALUE = "Doesn't contain added value";
  public static final String VALUE_TIME = "time";

  @TempDir
  public Path temporaryFolder;
  private final YamlMap yaml = new YamlMap();
  private final String key = 'k' + someValue();
  private final String value = 'v' + someValue();

  private String someValue() {
    return randomString(10);
  }

  @Test
  public void shouldStartEmpty() {
    assertTrue(yaml.isEmpty(), "Not empty");
    assertEquals(0, yaml.size(), "Size");
    assertTrue(yaml.get(someValue()).isEmpty(), "Value found");
  }

  @Test
  public void shouldBeAbleToAddItems() {
    yaml.put(key, value);

    assertFalse(yaml.isEmpty(), EMPTY);
    assertEquals(1, yaml.size(), "Size");
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
      assertEquals(expected, actual, "Value");
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
    assertFalse(yaml.containsKey(key), "Contains non-added value");

    yaml.put(key, value);
    assertTrue(yaml.containsKey(key), DOESN_T_CONTAIN_ADDED_VALUE);

    yaml.remove(key);
    assertFalse(yaml.containsKey(key), STILL_CONTAINS_REMOVED_VALUE);
  }

  @Test
  public void shouldRemoveValueRecursively() {
    assertFalse(yaml.containsKey(LAST_MODIFIED_DATE_NAME), "Contains non-added value");

    Map<String, Object> child = new HashMap<>();
    child.put(LAST_MODIFIED_DATE_NAME, VALUE_TIME);

    yaml.put(NESTED_ITEM_NAME, child);
    yaml.put(LAST_MODIFIED_DATE_NAME, VALUE_TIME);

    assertTrue(yaml.containsKey(NESTED_ITEM_NAME), DOESN_T_CONTAIN_ADDED_VALUE);
    assertTrue(yaml.containsKey(LAST_MODIFIED_DATE_NAME), DOESN_T_CONTAIN_ADDED_VALUE);
    assertTrue(yaml.get(NESTED_ITEM_NAME).toMap().containsKey(LAST_MODIFIED_DATE_NAME),
        DOESN_T_CONTAIN_ADDED_VALUE);

    yaml.removeRecursively(LAST_MODIFIED_DATE_NAME);
    assertFalse(yaml.containsKey(LAST_MODIFIED_DATE_NAME), STILL_CONTAINS_REMOVED_VALUE);
    assertTrue(yaml.get(NESTED_ITEM_NAME).toMap().containsKey(LAST_MODIFIED_DATE_NAME),
        DOESN_T_CONTAIN_ADDED_VALUE);

    yaml.removeRecursively("nestedItem");
    assertFalse(yaml.containsKey(NESTED_ITEM_NAME), STILL_CONTAINS_REMOVED_VALUE);
  }

  @Test
  public void shouldRemoveValueRecursivelyOnlyFromParents() {
    assertFalse(yaml.containsKey(LAST_MODIFIED_DATE_NAME), "Contains non-added value");

    final String nestedItem1 = NESTED_ITEM_NAME + "_1";
    final String nestedItem2 = NESTED_ITEM_NAME + "_2";

    Map<String, Object> child1 = new HashMap<>();
    child1.put(LAST_MODIFIED_DATE_NAME, VALUE_TIME);
    yaml.put(nestedItem1, child1);

    Map<String, Object> child2 = new HashMap<>();
    child2.put(LAST_MODIFIED_DATE_NAME, VALUE_TIME);
    yaml.put(nestedItem2, child2);

    yaml.put(LAST_MODIFIED_DATE_NAME, VALUE_TIME);

    assertTrue(yaml.containsKey(nestedItem1), DOESN_T_CONTAIN_ADDED_VALUE);
    assertTrue(yaml.containsKey(nestedItem2), DOESN_T_CONTAIN_ADDED_VALUE);
    assertTrue(yaml.containsKey(LAST_MODIFIED_DATE_NAME), DOESN_T_CONTAIN_ADDED_VALUE);
    assertTrue(yaml.get(nestedItem1).toMap().containsKey(LAST_MODIFIED_DATE_NAME),
        DOESN_T_CONTAIN_ADDED_VALUE);
    assertTrue(yaml.get(nestedItem2).toMap().containsKey(LAST_MODIFIED_DATE_NAME),
        DOESN_T_CONTAIN_ADDED_VALUE);

    yaml.removeRecursively(LAST_MODIFIED_DATE_NAME, Collections.singletonList(nestedItem2));

    assertFalse(yaml.containsKey(LAST_MODIFIED_DATE_NAME), STILL_CONTAINS_REMOVED_VALUE);
    assertFalse(yaml.get(nestedItem2).toMap().containsKey(LAST_MODIFIED_DATE_NAME),
        STILL_CONTAINS_REMOVED_VALUE);
    assertTrue(yaml.get(nestedItem1).toMap().containsKey(LAST_MODIFIED_DATE_NAME),
        DOESN_T_CONTAIN_ADDED_VALUE);

    yaml.removeRecursively(nestedItem2);
    assertFalse(yaml.containsKey(nestedItem2), STILL_CONTAINS_REMOVED_VALUE);
  }

  @Test
  public void shouldRetrieveNestedValues() {
    String outerKey = someValue();
    YamlMap map =
        new YamlMap(Collections.singletonMap(outerKey, Collections.singletonMap(key, value)));

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

    assertEquals(key2 + ',' + key1,
        yaml.entries().sorted().map(Entry::getKey).collect(Collectors.joining(",")), "Keys");
    assertEquals(value2 + ',' + value1, yaml.entries().sorted().map(Entry::getValue)
        .map(Value::toString).collect(Collectors.joining(",")), "Values");
  }

  @Test
  public void shouldExtractNamedNestedObject() {
    String name = someValue();
    yaml.put(name, new YamlMap().put(key, value));

    YamlMap nestedObject = yaml.entries().map(Entry::toMap).findFirst().get();

    assertEquals(name, nestedObject.get(NAME).toString(), "Name");
    assertEquals(value, nestedObject.get(key).toString(), "Property");
  }

  @Test
  public void shouldCheckWhetherValueExists() {
    yaml.put(key, null);
    assertTrue(yaml.get(key).isEmpty(), "Null value");

    yaml.put(key, value);
    assertFalse(yaml.get(key).isEmpty(), "Still null value");
  }

  @Test
  public void shouldConvertValueToMap() {
    yaml.put(key, value);
    assertFalse(yaml.get(key).isMap(), "String is a map");

    yaml.put(key, new YamlMap().put(key, value));
    Value v = yaml.get(key);
    assertTrue(v.isMap(), "Map is not a map");
    YamlMap map = v.toMap();
    assertValue(value, map);

    String value2 = someValue();
    map.put(key, value2);
    assertValue(value2, yaml.get(key, key));
  }

  @Test
  public void shouldConvertValueToList() {
    assertTrue(yaml.get(key).toList().isEmpty(), "Empty by default");
    yaml.put(key, value);
    assertFalse(yaml.get(key).isList(), "String is a list");

    String value2 = someValue();
    yaml.put(key, Arrays.asList(value, value2));
    Value v = yaml.get(key);
    assertTrue(v.isList(), "List is not a list");
    List<Value> values = v.toList();
    assertValue(value, values.get(0));
    assertValue(value2, values.get(1));
  }

  @Test
  public void shouldConvertValueToBoolean() {
    assertFalse(yaml.get(key).toBoolean(), EMPTY);

    yaml.put(key, Boolean.TRUE);
    assertTrue(yaml.get(key).toBoolean(), "Boolean");

    yaml.put(key, Boolean.toString(true));
    assertTrue(yaml.get(key).toBoolean(), "Boolean string");
  }

  @Test
  public void shouldConvertValueToInt() {
    assertEquals(0, yaml.get(key).toInt(), EMPTY);

    yaml.put(key, 42);
    assertEquals(42, yaml.get(key).toInt(), "Int");

    yaml.put(key, Integer.toString(313));
    assertEquals(313, yaml.get(key).toInt(), "Integer string");
  }

  @Test
  public void shouldConvertValueToDouble() {
    assertEquals(0.0, yaml.get(key).toDouble(), 1e-6, EMPTY);

    yaml.put(key, Math.PI);
    assertEquals(Math.PI, yaml.get(key).toDouble(), 1e-6, "Double");

    yaml.put(key, Double.toString(Math.E));
    assertEquals(Math.E, yaml.get(key).toDouble(), 1e-6, "Double string");
  }

  @Test
  public void shouldTestForScalarValue() {
    assertFalse(new Value(null).isScalar(), EMPTY);
    assertFalse(new Value(Collections.singletonList(randomString())).isScalar(), "List");
    assertFalse(new Value(Collections.singletonMap(randomString(), randomString())).isScalar(),
        "Map");

    assertTrue(new Value(Boolean.TRUE).isScalar(), "Boolean");
    assertTrue(new Value(randomString()).isScalar(), "String");
    assertTrue(new Value(313).isScalar(), "Int");
    assertTrue(new Value(Math.PI).isScalar(), "Double");
  }

  @Test
  public void shouldSerializeToAndDeserializeFromString() {
    assertToString(YamlMap.from(SAMPLE_YAML_STRING), SAMPLE_YAML_STRING, "toString");
  }

  private void assertToString(YamlMap actual, String format, String message, Object... args) {
    assertAsString(message, actual, String.format(format, args));
  }

  private void assertAsString(String message, YamlMap actual, String expected) {
    String actualStr = actual.toString();
    assertEquals(expected, actualStr, message);
    assertEquals(expected, YamlMap.from(actualStr).toString(), message + " - parsed");
  }

  private void assertToString(String message, YamlMap map) {
    DumperOptions prettyFlowBlockOptions = new DumperOptions();
    prettyFlowBlockOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
    prettyFlowBlockOptions.setPrettyFlow(true);
    prettyFlowBlockOptions.setLineBreak(LineBreak.getPlatformLineBreak());
    assertAsString(message, map,
        new Yaml(prettyFlowBlockOptions).dump(map.getRawData()).replace("|-", "|"));
  }

  @Test
  public void shouldSerializeSameAsSnakeYaml() {
    assertToString("Null",
        new YamlMap().put("gnu", new YamlMap().put("ape", "bear").put("cheetah", null)));
    assertToString("Empty string", new YamlMap().put("foobar", ""));
    assertToString("Start with double quote", new YamlMap().put("foo", "\"bar"));
    assertToString("Containing quote", new YamlMap().put("fo'o", "ba\"r"));
    assertToString("Containing :", new YamlMap().put("quuux", "ba:r"));
    assertToString("Start with %", new YamlMap().put("gnugnat", "%qux"));
    assertToString("Start with @", new YamlMap().put("quux", "@q"));
    assertToString("Start with ,", new YamlMap().put("quuuux", ",q"));
    assertToString("Map in map in list", new YamlMap().put("pdiSchemas",
        Collections.singletonList(new YamlMap().put("content", new YamlMap().put("format", "xml")))));
    assertToString("Containing #",
        new YamlMap().put("noComment", "This #text# doesn't contain a comment"));
  }

  @Test
  public void shouldSerializeBetterThanSnakeYaml() {
    assertToString(new YamlMap().put("zuul", Collections.emptyList()), "zuul: [ ]%n",
        "Empty collection");
    assertToString(new YamlMap(), "{ }%n", "Empty map");
    assertToString(new YamlMap().put("foo", "'bar"), "foo: \"'bar\"%n", "Starting with quote");
    assertToString(new YamlMap().put("qq", "'qwe\"rty"),
        "qq: \"'qwe\\\"rty\"%n", "Starting with quote, containing double quote");
    assertToString(new YamlMap().put("tab", "b\tar"), "tab: b  ar%n", "Containing tab");
    assertToString(new YamlMap().put("gnat",
        new YamlMap().put("spam", "ham\neggs").put("spam2",
            Arrays.asList("ham2\reggs2", "yuck\n\rpuck"))),
        "gnat:%n  spam: |%n    ham%n    eggs%n  spam2:%n  - |%n    ham2%n    eggs2%n  - |%n    yuck%n    puck%n",
        "New lines");
    assertToString(new YamlMap().put("databases",
        Collections.singletonList(new YamlMap().put(NAME, "db").put("metadata",
            Collections.singletonList(new YamlMap().put("text", "<foo>\n  <bar/>\n</foo>\n"))))),
        "databases:%n- name: db%n  metadata:%n  - text: |%n      <foo>%n        <bar/>%n      </foo>%n",
        "Nested maps and sequences with long text");
    // Want to be as good as snakeyaml 1.18, but 1.17 is different
    assertToString(new YamlMap().put("qbf",
        "Ex qui quidam postulant. Diam delicatissimi ut ius, eu quo autem putent conclusionemque, te volutpat "
            + "democritum sea. Ad est amet integre adipisci, quo id quis vituperata. In modo labitur disputationi sit. Eu "
            + "quo dolores pertinax theophrastus, usu quidam feugiat adipiscing ei. Usu graece gloriatur at, quo brute "
            + "altera gloriatur in, mea elitr primis invidunt ut."), "qbf: Ex qui quidam postulant. Diam delicatissimi ut ius, eu quo autem putent conclusionemque,%n"
        + "  te volutpat democritum sea. Ad est amet integre adipisci, quo id quis vituperata.%n"
        + "  In modo labitur disputationi sit. Eu quo dolores pertinax theophrastus, usu quidam%n"
        + "  feugiat adipiscing ei. Usu graece gloriatur at, quo brute altera gloriatur in, mea%n"
        + "  elitr primis invidunt ut.%n",
        "Long text");
  }

  @Test
  public void shouldLoadYamlFromFile() throws IOException {
    File yamlFile = newFile(temporaryFolder);
    try (PrintWriter writer = new PrintWriter(yamlFile, StandardCharsets.UTF_8.name())) {
      writer.print(SAMPLE_YAML_STRING);
    }

    assertEquals(SAMPLE_YAML_STRING, YamlMap.from(yamlFile).toString(), "YAML from file");
  }

  @Test
  public void shouldReturnEmptyMapWhenLoadingFromNonExistingFile() throws IOException {
    File nonExistingFile = newFile(temporaryFolder);

    if (!nonExistingFile.delete()) {
      throw new IllegalStateException("Could not delete " + nonExistingFile.getAbsolutePath());
    }

    assertTrue(YamlMap.from(nonExistingFile).isEmpty(),
        "YAML loaded from non-existing file is not empty");
  }

  @Test
  public void shouldSerializeToStream() throws IOException {
    assertEquals(SAMPLE_YAML_STRING,
        IOUtils.toString(YamlMap.from(SAMPLE_YAML_STRING).toStream(), StandardCharsets.UTF_8),
        "YAML from stream");
  }

  @Test
  public void shouldDeleteListItem() {
    yaml.put(key, Collections.singletonList(value));
    List<Value> values = yaml.get(key).toList();

    values.remove(0);

    assertTrue(yaml.get(key).toList().isEmpty(),
        "Sequence should be empty after removing only item");
  }

  @Test
  public void shouldIterateList() {
    String value2 = '2' + someValue();
    yaml.put(key, Arrays.asList(value, value2));

    ListIterator<Value> iterator = yaml.get(key).toList().listIterator();
    assertEquals(value, iterator.next().toString(), "Item #1");

    iterator.remove();
    assertEquals(value2, iterator.next().toString(), "Item after remove");

    assertEquals(value2, iterator.previous().toString(), "Previous item");

    iterator.remove();
    assertTrue(yaml.get(key).toList().isEmpty(), "List should be empty after removing all values");
  }

  @Test
  public void shouldSortWithDefaultComparator() {
    yaml.put("cheetah", "dingo");
    yaml.put("ape", "bear");

    assertSorted("ape: bear%ncheetah: dingo%n");
  }

  private void assertSorted(String expected) {
    assertYaml(expected, yaml.sort());
  }

  private void assertYaml(String expected, YamlMap actual) {
    assertEquals(String.format(expected), actual.toString(), "YAML");
  }

  @Test
  public void shouldSortWithProvidedComparator() {
    yaml.put("elephant", "fox");
    yaml.put("giraffe", "hyena");

    assertYaml("giraffe: hyena%nelephant: fox%n", yaml.sort((a, b) -> b.compareTo(a)));
  }

  @Test
  public void shouldSortRecursively() {
    yaml.put("iguana",
        new YamlMap()
            .put("leopard",
                Collections.singletonList(new YamlMap().put("opossum", "parrot").put("mule", "nightingale")))
            .put("jaguar", "koala").put("rhino", Arrays.asList("tiger", "snake")));

    assertSorted("iguana:%n  jaguar: koala%n  leopard:%n  - mule: nightingale"
        + "%n    opossum: parrot%n  rhino:%n  - snake%n  - tiger%n");
  }

  @Test
  public void shouldSortSequencesByName() {
    yaml.put("unicorn",
        Arrays.asList(new YamlMap().put(NAME, "whale").put(TYPE, "a"),
            new YamlMap().put(NAME, "velociraptor").put(TYPE, "e"),
            new YamlMap().put(NAME, "velociraptor").put(TYPE, "b").put("s", "c"),
            new YamlMap().put(NAME, "velociraptor").put(TYPE, "d")));

    assertSorted(
        "unicorn:%n" + "- name: velociraptor%n  type: d%n" + "- name: velociraptor%n  type: e%n"
            + "- name: velociraptor%n  s: c%n  type: b%n" + "- name: whale%n  type: a%n");
  }

  @Test
  public void shouldSortAtSingleLevel() {
    yaml.put("bear", new YamlMap().put("elephant", "fox").put("cheetah", "dingo")).put("ape",
        new YamlMap().put("giraffe", "hyena"));

    assertYaml(
        "ape:%n" + "  giraffe: hyena%n%n" + "bear:%n" + "  elephant: fox%n" + "  cheetah: dingo%n",
        yaml.sort(false));
  }

  @Test
  public void shouldLeaveSomeEntriesUnsorted() {
    yaml.put("B", new YamlMap().put("b", "b").put("a", "a")).put("A", Arrays.asList("z", "y"))
        .put("C", new YamlMap().put("d", "d").put("c", "c"));

    assertYaml("A:%n" + "- z%n" + "- y%n%n" + "B:%n" + "  a: a%n" + "  b: b%n%n" + "C:%n"
        + "  d: d%n" + "  c: c%n", yaml.sort(property -> "B".equals(property)));
  }

  @Test
  public void shouldSortAnyMap() {
    Map<String, String> wrapped = new TreeMap<>((a, b) -> b.compareTo(a));
    wrapped.put("F", "G");
    wrapped.put("D", "E");
    YamlMap map = new YamlMap(wrapped);

    // Should update the underlying map even though it is not a LinkedHashMap
    map.sort();

    assertYaml("D: E%nF: G%n", map);
  }

  @Test
  public void shouldVisitMap() {
    yaml.put("aardvark",
        Arrays.asList(
            new YamlMap().put("bee", "cobra").put("dog",
                new YamlMap().put("emu", new YamlMap().put("falcon", Boolean.FALSE))),
            new YamlMap().put("gazelle", "hamster"), new YamlMap().put("ibis", "jackal")));

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

    TestUtil.assertEquals(Arrays.asList("/", "/aardvark/0", "/aardvark/0/dog", "@/aardvark/0/dog",
        "@/aardvark/0", "/aardvark/1", "@/aardvark/1", "@/"), visitedPaths, "Visited paths");
  }

  @Test
  public void shouldStripEndingWhitespace() {
    yaml.put("mongoose", "narwhal  ");

    assertValue("narwhal", yaml.get("mongoose"));
  }

  private void assertValue(String expected, Value actual) {
    assertEquals(expected, actual.toString(), "Value");
  }

  @Test
  public void shouldStripWhitespaceAfterLineBreaks() {
    yaml.put("okapi", "panda  \nquail");

    assertValue(String.format("panda%nquail"), yaml.get("okapi"));
  }

  @Test
  public void shouldMaintainOrderWhenReplacingEntries() {
    yaml.put("rabbit", "scorpion").put("tapir", "uakari").replace("rabbit", "vulture", "warthog");

    assertYaml("vulture: warthog%ntapir: uakari%n", yaml);
  }

  @Test
  public void shouldReplaceNestedMaps() {
    String oldKey = "xenops";
    yaml.put(oldKey, new YamlMap().put("yak", "zebra"));

    yaml.replace(oldKey, "alligator", yaml.get(oldKey));

    assertYaml("alligator:%n  yak: zebra%n", yaml);
  }

  @Test
  public void shouldSortSequence() {
    yaml.put("Q", Arrays.asList("N", "M"))
        .put("O",
            Arrays.asList(new YamlMap().put("I", "J").put(NAME, "R"),
                new YamlMap().put("K", "L").put(NAME, "P")))
        .put("S", Arrays.asList(new YamlMap().put("X", "W").put("T", "V"),
            new YamlMap().put("Y", "W").put("T", "U")));

    yaml.entries().map(Entry::getValue).map(Value::toList).forEach(YamlSequence::sort);

    assertYaml(
        "Q:%n- M%n- N%n%nO:%n- K: L%n  name: P%n- I: J%n  name: R%n%nS:%n- Y: W%n  T: U%n- X: W%n  T: V%n",
        yaml);
  }

  @Test
  public void shouldResolveOffsetDateTime() {
    assertStringField("odt", "2002-05-30T12:00:00Z");
    assertStringField("ms", "2017-10-06T10:59:11.477+02:00");
  }

  private void assertStringField(String fieldName, String fieldValue) {
    assertEquals(String.class,
        YamlMap.from(fieldName + SEMICOLON + fieldValue).getRawData().get(fieldName).getClass(),
        fieldValue);
  }

  @Test
  public void shouldGetProperFieldTypeAfterDeserialization() {
    assertFieldTypeAfterDeserialization(String.class, "0000");
    assertFieldTypeAfterDeserialization(String.class, "01234");
    assertFieldTypeAfterDeserialization(String.class, "0123s");
    assertFieldTypeAfterDeserialization(String.class, "1234 1234 000");
    assertFieldTypeAfterDeserialization(String.class, "0 0 0 0");
    assertFieldTypeAfterDeserialization(Integer.class, "1234");
  }

  private void assertFieldTypeAfterDeserialization(Class<?> fieldType, String fieldValue) {
    String serializedYaml = new YamlMap().put(NAME, fieldValue).toString();
    assertEquals(fieldType, YamlMap.from(serializedYaml).getRawData().get(NAME).getClass(),
        fieldValue);
  }

  @Test
  public void shouldAddQuotesToFieldStartingWithZeroAndConsistingOfDigits() {
    assertEquals(expectedYamlNameValue("'0000'"), actualYamlNameValue("0000"));
    assertEquals(expectedYamlNameValue("'01234'"), actualYamlNameValue("01234"));
    assertEquals(expectedYamlNameValue("1234"), actualYamlNameValue("1234"));
    assertEquals(expectedYamlNameValue("0123s"), actualYamlNameValue("0123s"));
    assertEquals(expectedYamlNameValue("1234 1234 000"), actualYamlNameValue("1234 1234 000"));
    assertEquals(expectedYamlNameValue("0 0 0 0"), actualYamlNameValue("0 0 0 0"));
  }

  private String expectedYamlNameValue(String fieldValue) {
    return NAME + SEMICOLON + fieldValue;
  }

  private String actualYamlNameValue(String fieldValue) {
    return new YamlMap().put(NAME, fieldValue).toString().trim();
  }

}
