/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.util.Objects;

/**
 * Result of a {@linkplain HashFunction hash function} applied to some binary data, {@linkplain Encoding encoded} in
 * ASCII form. An encoded hash can serve as a
 * <a href="http://public.ccsds.org/publications/archive/650x0m2.pdf">Transformational Information Property</a>.
 */
public class EncodedHash {

  private final String hashFunction;
  private final String encoding;
  private final String value;

  /**
   * Create an instance of an encoded hash.
   * @param hashFunction The name of the {@linkplain HashFunction} used to compute the hash
   * @param encoding The name of the {@linkplain Encoding} used to convert the hash to ASCII form
   * @param encodedHash The ASCII form of the computed hash
   */
  public EncodedHash(String hashFunction, String encoding, String encodedHash) {
    this.hashFunction = hashFunction;
    this.encoding = encoding;
    this.value = encodedHash;
  }

  /**
   * Return the name of the hash function used to compute the hash.
   * @return The name of the hash function
   */
  public String getHashFunction() {
    return hashFunction;
  }

  /**
   * Return the name of the encoding used to convert the hash to ASCII form.
   * @return The name of the encoding
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Return the encoded hash.
   * @return The encoded hash
   */
  public String getValue() {
    return value;
  }

  /**
   * Return a human-friendly version of the encoded hash.
   * @return A human-friendly version of the encoded hash
   */
  @Override
  public String toString() {
    return encoding + "(" + hashFunction + "(...)) = " + value;
  }

  /**
   * Return a hash code value for this object.
   * @return A hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(hashFunction, encoding, value);
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
    EncodedHash other = (EncodedHash)obj;
    return value.equals(other.value) && hashFunction.equals(other.hashFunction) && encoding.equals(other.encoding);
  }

}
