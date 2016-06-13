/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import java.util.Locale;
import java.util.function.Function;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;


/**
 * Mechanism for storing binary data in ASCII form.
 */
public enum Encoding {

  BASE64(Base64::encodeBase64String), HEX(Hex::encodeHexString);

  private final Function<byte[], String> encoder;

  Encoding(Function<byte[], String> encoder) {
    this.encoder = encoder;
  }

  /**
   * Convert the given bytes to ASCII form.
   * @param bytes The bytes to convert
   * @return The ASCII form of the bytes
   */
  public String encode(byte[] bytes) {
    return encoder.apply(bytes);
  }

  /**
   * Return a human-readable version of the encoding.
   * @return A human-readable version of the encoding
   */
  @Override
  public String toString() {
    return super.toString().toLowerCase(Locale.ENGLISH);
  }

}
