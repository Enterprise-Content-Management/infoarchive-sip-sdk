/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.opentext.ia.yaml.core.YamlMap;


/**
 * Builder for {@linkplain ZipCustomization}s.
 * @since 10.1.0
 */
public class ZipCustomizationBuilder {

  private final Collection<ZipEntryCustomization> entryCustomizations = new ArrayList<>();
  private final Collection<Supplier<ExtraZipEntry>> extraZipEntrySuppliers = new ArrayList<>();
  private BiConsumer<Collection<String>, Function<String, InputStream>> initializer = (names, contentSupplier) -> { };

  public ZipCustomizationBuilder init(
      BiConsumer<Collection<String>, Function<String, InputStream>> initializeCustomization) {
    this.initializer = initializeCustomization;
    return this;
  }

  public ZipCustomizationBuilder properties(BiConsumer<String, Properties> propertiesCustomizer) {
    return with(new PropertiesZipEntryCustomization(propertiesCustomizer));
  }

  public ZipCustomizationBuilder with(ZipEntryCustomization zipEntryCustomization) {
    entryCustomizations.add(zipEntryCustomization);
    return this;
  }

  public ZipCustomizationBuilder yaml(BiConsumer<String, YamlMap> yamlCustomizer) {
    return with(new YamlZipEntryCustomization(yamlCustomizer));
  }

  public ZipCustomizationBuilder extra(Supplier<ExtraZipEntry> extraZipEntrySupplier) {
    extraZipEntrySuppliers.add(extraZipEntrySupplier);
    return this;
  }

  public ZipCustomization build() {
    return new ZipCustomization() {
      @Override
      public void init(Collection<String> names, Function<String, InputStream> contentSupplier) {
        initializer.accept(names, contentSupplier);
      }

      @Override
      public InputStream customize(String name, InputStream content) throws IOException {
        Optional<ZipEntryCustomization> entryCustomization = entryCustomizations.stream()
            .filter(entry -> entry.matches(name))
            .findFirst();
        return entryCustomization.isPresent()
            ? entryCustomization.get().customize(content)
                : content;
      }

      @Override
      public Collection<ExtraZipEntry> extraEntries() {
        return extraZipEntrySuppliers.stream()
            .map(Supplier::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      }
    };
  }

}
