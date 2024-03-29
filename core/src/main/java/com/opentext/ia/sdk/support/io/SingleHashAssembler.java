/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Assemble a single hash.
 */
@NotThreadSafe
public class SingleHashAssembler extends NoHashAssembler {

  private final Encoding encoding;
  private final MessageDigest digester; // NOPMD not thread safe

  /**
   * Assemble hashes using the default hash function and encoding.
   */
  public SingleHashAssembler() {
    this(HashFunction.SHA256, Encoding.BASE64);
  }

  /**
   * Assemble hashes using the given hash function and encoding.
   * @param hashFunction The hash function used to assemble the hash
   * @param encoding The encoding used to encode the assembled hash
   */
  public SingleHashAssembler(HashFunction hashFunction, Encoding encoding) {
    this.encoding = encoding;
    try {
      digester = MessageDigest.getInstance(hashFunction.toString());
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Missing message digest " + hashFunction, e);
    }
  }

  @Override
  public void initialize() {
    super.initialize();
    digester.reset();
  }

  @Override
  public void add(byte[] buffer, int length) {
    super.add(buffer, length);
    digester.update(buffer, 0, length);
  }

  @Override
  public Collection<EncodedHash> get() {
    return Collections
      .singletonList(new EncodedHash(digester.getAlgorithm(), encoding.toString(), encoding.encode(digester.digest())));
  }

}
