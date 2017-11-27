/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.builder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.atteo.evo.inflector.English;

import com.opentext.ia.configuration.Configuration;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;


/**
 * An InfoArchive configuration in YAML.
 * @author Ray Sinnema
 * @since 9.4.0
 */
public class YamlMapConfiguration implements Configuration<YamlMap> {

  private final YamlMap yaml;

  public YamlMapConfiguration(YamlMap yaml) {
    this.yaml = yaml;
  }

  /**
   * Returns the YAML representation of the configuration.
   * @return The YAML representation of the configuration
   */
  public YamlMap getYaml() {
    return yaml;
  }

  @Override
  public String toString() {
    return yaml.toString();
  }

  @Override
  public List<YamlMap> getTenants() {
    return toList(streamOfType("tenant"));
  }

  private List<YamlMap> toList(Stream<YamlMap> stream) {
    return stream.collect(Collectors.toList());
  }

  private Stream<YamlMap> streamOfType(String type) {
    String collection = English.plural(type);
    if (yaml.containsKey(collection)) {
      return yaml.get(collection).toList().stream().map(Value::toMap);
    }
    if (yaml.containsKey(type)) {
      return Stream.of(yaml.get(type).toMap());
    }
    return Stream.empty();
  }

  @Override
  public List<YamlMap> getApplications(YamlMap tenant) {
    return childList(tenant, "tenant", "application");
  }

  private List<YamlMap> childList(YamlMap parent, String parentType, String type) {
    return toList(streamOfType(type)
        .filter(application -> parent.get("name").equals(application.get(parentType))));
  }

  @Override
  public List<YamlMap> getSearches(YamlMap application) {
    return childList(application, "application", "search");
  }

  @Override
  public List<YamlMap> getFileSystemRoots() {
    return toList(streamOfType("fileSystemRoot"));
  }

  @Override
  public List<YamlMap> getSpaces(YamlMap application) {
    return childList(application, "application", "space");
  }

  @Override
  public List<YamlMap> getSpaceRootFolders(YamlMap space) {
    return childList(space, "space", "spaceRootFolder");
  }

  @Override
  public List<YamlMap> getSpaceRootXdbLibraries(YamlMap space) {
    return childList(space, "space", "spaceRootXdbLibrary");
  }

  @Override
  public List<YamlMap> getXdbLibraries(YamlMap spaceRootXdbLibrary) {
    return childList(spaceRootXdbLibrary, "spaceRootXdbLibrary", "xdbLibrary");
  }

  @Override
  public List<YamlMap> getPdiSchemas(YamlMap application) {
    return childList(application, "application", "pdiSchema");
  }

  @Override
  public List<YamlMap> getHoldings(YamlMap application) {
    return childList(application, "application", "holding");
  }

}
