/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.atteo.evo.inflector.English;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;


/**
 * InfoArchive server configuration in YAML format.
 * @since 6.0.0
 */
@SuppressWarnings("unchecked")
public class YamlConfiguration {

  private static final Collection<Class<? extends Visitor>> YAML_NORMALIZATION_CLASSES = Arrays.asList(
      EnsureVersion.class,
      ConvertTopLevelSingularObjectsToSequences.class,
      InsertDefaultReferences.class,
      ConvertSingularReferenceToSequenceForCollectionReferences.class,
      ReplacePdiSchemaNamespaceWithName.class,
      ConvertTopLevelMapOfMapsToSequences.class,
      ConvertNestedMapOfMapsToSequences.class,
      EnsureHoldingCryptoMembers.class,
      // Do this again, since the structure may be changed so that additional references are detected
      InsertDefaultReferences.class,
      ConvertPdiIndexes.class,
      ConvertIngestIndexes.class,
      ConvertIngestProcessors.class,
      EnsureEnumConstant.class,
      InsertDefaultValues.class,
      InsertDefaultParentForFileSystemFolder.class,
      AddNamespaceDeclarationsToQueries.class,
      ReplacePdiIndexNamespaceWithUri.class,
      ReplaceIngestIndexNamespaceWithUri.class,
      ReplacePdiResultSchemaNamespaceWithUri.class,
      ReplacePdiYamlWithXml.class,
      ReplaceResultConfigurationHelperYamlWithXml.class,
      ReplaceIngestYamlWithXml.class,
      ExpandResultMasterNamespaces.class
  );
  private static final String NAME = "name";

  private final YamlMap yaml;

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
    this(YamlMap.from(yaml), resolver);
    IOUtils.closeQuietly(yaml);
  }

  /**
   * Load configuration from a string in YAML format.
   * @param yaml The string to load from
   */
  public YamlConfiguration(String yaml) {
    this(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)), ResourceResolver.none());
  }

  YamlConfiguration(YamlMap yaml, ResourceResolver resolver) {
    this.yaml = yaml;
    normalizations(resolver).forEach(this.yaml::visit);
  }

  private Stream<Visitor> normalizations(ResourceResolver resolver) {
    return Stream.concat(
        Stream.of(new InlineExternalContent(resolver)),
        YAML_NORMALIZATION_CLASSES.stream().map(this::newVisitor));
  }

  private Visitor newVisitor(Class<? extends Visitor> type) {
    try {
      Constructor<? extends Visitor> constructor = type.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to create instance of " + type, e);
    }
  }

  /**
   * Returns the underlying {@linkplain YamlMap}.
   * @return the underlying <code>YamlMap</code>
   */
  public YamlMap getMap() {
    return yaml;
  }

  /**
   * Returns the name of the single application.
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
      throw new IllegalArgumentException(String.format("Expected 1 %s, but got %d in:%n%s", type, instances.size(),
          yaml));
    }
    return instances.get(0).toMap();
  }

  /**
   * Returns the name of the single holding.
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
    return pdiSchema().get("name").toString();
  }

  private YamlMap pdiSchema() {
    return singleInstanceOf("pdiSchema");
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
    return yaml.toString();
  }

}
