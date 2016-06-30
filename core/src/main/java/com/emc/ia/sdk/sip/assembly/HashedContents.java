/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.emc.ia.sdk.support.io.EncodedHash;


/**
 * Hashes of zero or more contents extracted from some source.
 * @param <S> The type of source
 */
public class HashedContents<S> {

  private final S source;
  private final Map<String, Collection<EncodedHash>> contentHashes = new TreeMap<>();

  /**
   * Store the hashes of a given source's contents.
   * @param source The source from which the contents was extracted
   * @param contentHashes The hashes of the source's contents
   */
  public HashedContents(S source, Map<String, Collection<EncodedHash>> contentHashes) {
    this.source = Objects.requireNonNull(source);
    this.contentHashes.putAll(Objects.requireNonNull(contentHashes));
  }

  /**
   * Return the source from which the contents was extracted.
   * @return The source from which the contents was extracted
   */
  public S getSource() {
    return source;
  }

  /**
   * Return the hashes of the contents extracted from the source.
   * @return The hashes of the contents extracted from the source
   */
  public Map<String, Collection<EncodedHash>> getContentHashes() {
    return Collections.unmodifiableMap(contentHashes);
  }

  /**
   * Return a hash code value for this object.
   * @return A hash code value for this object
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + source.hashCode();
    result = prime * result + contentHashes.hashCode();
    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * @param obj The reference object with which to compare
   * @return <code>true</code> if this object is the same as the reference object; <code>false</code> otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    @SuppressWarnings("unchecked")
    HashedContents<S> other = (HashedContents<S>)obj;
    return source.equals(other.source) && contentHashes.equals(other.contentHashes);
  }

  /**
   * Return a human-readable version of this object.
   * @return A human-readable version of this object
   */
  @Override
  public String toString() {
    return source + " with content hashes\n" + contentHashes.entrySet().stream()
        .map(e -> "- " + e.getKey() + "=" + e.getValue())
        .collect(Collectors.joining("\n"));
  }

}
