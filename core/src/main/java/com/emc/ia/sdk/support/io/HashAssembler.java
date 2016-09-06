/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Assemble {@linkplain EncodedHash}es from binary data. The hash assembly process consists of the following stages:
 * <ol>
 * <li>{@linkplain #initialize() Initialize} the assembly process</li>
 * <li>{@linkplain #add(byte[], int) Add} data to the hash zero or more times</li>
 * <li>{@linkplain #get() Retrieve} the hash</li>
 * </ol>
 */
public interface HashAssembler extends Supplier<Collection<EncodedHash>> {

  /**
   * Initialize a new hash assembly process.
   */
  void initialize();

  /**
   * Add data to be hashed.
   * @param buffer The buffer of data to hash
   * @param length The number of bytes in the given buffer to include in the hash
   */
  void add(byte[] buffer, int length);

  /**
   * Return the number of bytes hashed.
   * @return The number of bytes hashed
   */
  long numBytesHashed();

}
