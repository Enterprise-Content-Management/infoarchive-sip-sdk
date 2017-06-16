/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.atteo.evo.inflector.English;
import org.yaml.snakeyaml.Yaml;

import com.opentext.ia.sdk.support.NewInstance;
import com.opentext.ia.sdk.support.io.StringStream;
import com.opentext.ia.sdk.support.resource.ResourceResolver;
import com.opentext.ia.sdk.support.yaml.Entry;
import com.opentext.ia.sdk.support.yaml.Value;
import com.opentext.ia.sdk.support.yaml.Visitor;
import com.opentext.ia.sdk.support.yaml.YamlMap;


/**
 * InfoArchive server configuration in YAML format.
 * @since 6.0.0
 */
@SuppressWarnings("unchecked")
public class YamlConfiguration {

  private static final Collection<Class<? extends Visitor>> YAML_NORMALIZATION_CLASSES = Arrays.asList(
      EnsureVersion.class,
      ConvertTopLevelSingularObjectsToSequences.class,
      ConvertEnumValue.class
  );
  private static final String NAME = "name";
  private static final String DEFAULT = "default";

  private YamlMap yaml = new YamlMap();

  /**
   * Load configuration from a file in YAML format.
   * @param yaml The file to load from
   * @throws IOException When an I/O error occurs
   */
  public YamlConfiguration(File yaml) throws IOException {
    this(new FileInputStream(yaml), ResourceResolver.fromFile(yaml));
  }

  /**
   * Load configuration from a stream in YAML format.
   * @param yaml The stream to load from
   * @param resolver How to resolve resources to text
   */
  public YamlConfiguration(InputStream yaml, ResourceResolver resolver) {
    this(parse(yaml), resolver);
  }

  private static YamlMap parse(InputStream yaml) {
    YamlMap result = new YamlMap();
    for (Object data : new Yaml().loadAll(yaml)) {
      result.putAll(new YamlMap(data));
    }
    return result;
  }

  /**
   * Load configuration from a string in YAML format.
   * @param yaml The string to load from
   */
  public YamlConfiguration(String yaml) {
    this(new StringStream(yaml), ResourceResolver.none());
  }

  YamlConfiguration(YamlMap yaml, ResourceResolver resolver) {
    this.yaml = yaml;
    normalizations(resolver).forEach(normalizer -> this.yaml.visit(normalizer));
  }

  private Stream<Visitor> normalizations(ResourceResolver resolver) {
    return Stream.concat(
        Stream.of(new InlineExternalContent(resolver)),
        YAML_NORMALIZATION_CLASSES.stream()
            .map(type -> NewInstance.of(type.getName(), null).as(Visitor.class)));
  }

  YamlMap getMap() {
    return yaml;
  }

  /**
   * Returns the name of the single or default item of the given type.
   * @param type The type of object to search
   * @return the name of the single or default item of the given type, or <code>null</code> if there is none
   */
  public String nameOfSingle(String type) {
    return singleInstanceOf(type).get(NAME).toString();
  }

  /**
   * Returns the single or default item of the given type.
   * @param type The type of object to search
   * @return the single or default item of the given type, or <code>null</code> if there is none
   */
  public YamlMap singleInstanceOf(String type) {
    Value instance = yaml.get(type);
    if (instance.isMap()) {
      return instance.toMap();
    }
    Value instances = yaml.get(English.plural(type));
    if (instances.isList()) {
      return singleInstanceIn(instances.toList());
    } else if (instances.isMap()) {
      return singleInstanceIn(instances.toMap());
    }
    return new YamlMap();
  }

  private YamlMap singleInstanceIn(List<Value> items) {
    if (items.size() == 1) {
      return items.get(0).toMap();
    }
    return items.stream()
        .filter(this::isDefault)
        .findAny()
        .map(value -> value.toMap())
        .orElseGet(YamlMap::new);
  }

  private boolean isDefault(Value value) {
    return isDefault(value.toMap());
  }

  private boolean isDefault(YamlMap map) {
    return map.get(DEFAULT).toBoolean();
  }

  private YamlMap singleInstanceIn(YamlMap items) {
    if (items.size() == 1) {
      return items.entries().findFirst().get().toMap();
    }
    return items.entries()
        .filter(entry -> isDefault(entry.getValue()))
        .map(entry -> entry.toMap())
        .findAny()
        .orElseGet(YamlMap::new);
  }

  /**
   * Returns the name of the application.
   * @return the name of the application
   */
  public String getApplicationName() {
    return nameOfSingle("application");
  }

  /**
   * Returns the name of the holding.
   * @return the name of the holding
   */
  public String getHoldingName() {
    return nameOfSingle("holding");
  }

  /**
   * Returns the namespace of the PDI.
   * @return the namespace of the PDI
   */
  public String getPdiSchemaName() {
    return lookup("namespace", "prefix", pdiSchema().get("namespace"), "uri");
  }

  private YamlMap pdiSchema() {
    return singleInstanceOf("pdiSchema");
  }

  private String lookup(String type, String lookupProperty, Value lookupValue, String resultProperty) {
    return allInstancesOf(type)
        .filter(instance -> lookupValue.isEmpty() ? isDefault(instance) : lookupValue.equals(instance.get(lookupProperty)))
        .map(instance -> instance.get(resultProperty).toString())
        .findAny()
        .orElse("");
  }

  private Stream<YamlMap> allInstancesOf(String type) {
    Value instances = yaml.get(English.plural(type));
    return Stream.concat(Stream.concat(
        onlyInstanceOf(type),
        listOf(instances)),
        mapsOf(instances));
  }

  private Stream<YamlMap> onlyInstanceOf(String type) {
    Value value = yaml.get(type);
    return value.isMap() ? Stream.of(value.toMap()) : Stream.empty();
  }

  private Stream<YamlMap> listOf(Value instances) {
    return Stream.of(instances)
        .filter(Value::isList)
        .flatMap(item -> item.toList().stream())
        .map(Value::toMap);
  }

  private Stream<YamlMap> mapsOf(Value instances) {
    return Stream.of(instances)
        .filter(Value::isMap)
        .map(Value::toMap)
        .flatMap(YamlMap::entries)
        .map(Entry::toMap);
  }

  /**
   * Returns the contents of the XML Schema for the PDI.
   * @return the contents of the XML Schema for the PDI
   */
  public String getPdiSchema() {
    return pdiSchema().get("content", "text").toString();
  }

  /**
   * Returns the YAML structure.
   */
  @Override
  public String toString() {
    return new Yaml().dump(yaml.getRawData());
  }

}
