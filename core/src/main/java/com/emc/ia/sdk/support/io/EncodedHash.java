/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;


/**
 * Result of a {@linkplain HashFunction hash function} applied to some  binary data, {@linkplain Encoding encoded} in
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

}
