/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Hashes of zero or more contents extracted from some source.
 * @param <S> The type of source
 */
public class HashedContents<S> {

  private final S source;
  private final Map<String, ContentInfo> contentInfo;

  /**
   * Store the hashes of a given source's contents.
   * @param source The source from which the contents was extracted
   * @param contentInfo The reference information and the hashes of the source's contents
   */
  public HashedContents(S source, Map<String, ContentInfo> contentInfo) {
    this.source = Objects.requireNonNull(source);
    this.contentInfo = Objects.requireNonNull(contentInfo);
  }

  /**
   * Return the source from which the contents was extracted.
   * @return The source from which the contents was extracted
   */
  public S getSource() {
    return source;
  }

  /**
   * Return a hash code value for this object.
   * @return A hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(source, getContentInfo());
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
    return source.equals(other.source) && getContentInfo().equals(other.getContentInfo());
  }

  /**
   * Return a human-readable version of this object.
   * @return A human-readable version of this object
   */
  @Override
  public String toString() {
    return source + " with contents " + getContentInfo().entrySet()
      .stream()
      .map(e -> "- " + e.getKey() + "=" + e.getValue())
      .collect(Collectors.joining(System.lineSeparator()));

  }

  public Map<String, ContentInfo> getContentInfo() {
    return contentInfo;
  }

}
