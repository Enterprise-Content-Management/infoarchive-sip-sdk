/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Builder for named configuration objects.
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <P> The type of the parent builder
 * @param <S> The type of this builder
 * @param <C> The type of configuration to build
 */
public class NamedObjectBuilder<P extends BaseBuilder<?, C>, S extends NamedObjectBuilder<?, ?, C>,
    C extends Configuration<?>> extends BaseBuilder<P, C> {

  private static final Collection<String> COMMON_TYPE_PREFIXES = Arrays.asList(
      "rdb", "space", "search", "root", "query");

  protected NamedObjectBuilder(P parent, String type) {
    super(parent, type);
    named(typePrefix(removeCommonTypePrefixes(type)) + someName());
  }

  private String removeCommonTypePrefixes(String type) {
    StringBuilder result = new StringBuilder(type);
    int len = result.length();
    Optional<String> typePrefix = getCommonTypePrefix(result, len);
    while (typePrefix.isPresent()) {
      int prefixLength = typePrefix.get().length();
      result.delete(0, prefixLength);
      len -= prefixLength;
      result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
      typePrefix = getCommonTypePrefix(result, len);
    }
    return result.toString();
  }

  private Optional<String> getCommonTypePrefix(StringBuilder type, int len) {
    return COMMON_TYPE_PREFIXES.stream()
        .filter(prefix -> {
          int prefixLength = prefix.length();
          return len > prefixLength && prefix.equals(type.substring(0, prefixLength));
        })
        .findFirst();
  }

  private String typePrefix(String type) {
    return type.substring(0, Math.min(type.length(), 3)) + '_';
  }

  /**
   * Set the name of the object.
   * @param name The name of the object
   * @return This builder
   */
  @SuppressWarnings("unchecked")
  public final S named(String name) {
    setProperty("name", name);
    return (S)this;
  }

  /**
   * Set the configure property.
   * @param configure The new value of the configure property
   * @return This builder
   * @since 9.8.0
   */
  @SuppressWarnings("unchecked")
  public S configure(String configure) {
    setProperty("configure", configure);
    return (S)this;
  }

}
