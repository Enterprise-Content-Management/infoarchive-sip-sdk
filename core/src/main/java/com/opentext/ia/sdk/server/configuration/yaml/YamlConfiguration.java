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
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.atteo.evo.inflector.English;
import org.yaml.snakeyaml.Yaml;

import com.opentext.ia.sdk.support.NewInstance;
import com.opentext.ia.sdk.support.io.StringStream;
import com.opentext.ia.sdk.support.resource.ResourceResolver;
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
      ConvertTopLevelMapOfMapsToSequences.class,
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
   * Returns the name of the application.
   * @return the name of the application
   */
  public String getApplicationName() {
    return nameOfSingle("application");
  }

  private String nameOfSingle(String type) {
    return singleInstanceOf(type).get(NAME).toString();
  }

  private YamlMap singleInstanceOf(String type) {
    List<Value> instances = yaml.get(English.plural(type)).toList();
    if (instances.size() != 1) {
      throw new IllegalArgumentException("Expected 1 " + type + ", but got " + instances.size());
    }
    return instances.get(0).toMap();
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
    Predicate<YamlMap> lookup = lookupValue.isEmpty() ? this::isDefault
        : instance -> lookupValue.equals(instance.get(lookupProperty));
    return allInstancesOf(type)
        .filter(lookup)
        .map(instance -> instance.get(resultProperty).toString())
        .findAny()
        .orElse("");
  }

  private boolean isDefault(YamlMap map) {
    return map.get(DEFAULT).toBoolean();
  }

  private Stream<YamlMap> allInstancesOf(String type) {
    return yaml.get(English.plural(type)).toList().stream()
        .map(Value::toMap);
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
