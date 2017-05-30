/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

/**
 * Well-known <a href="https://en.wikipedia.org/wiki/Hash_function">hash function</a>.
 */
public enum HashFunction {

  MD2("MD2"),
  MD5("MD5"),
  SHA1("SHA-1"),
  SHA256("SHA-256"),
  SHA384("SHA-384"),
  SHA512("SHA-512");

  private final String name;

  HashFunction(String name) {
    this.name = name;
  }

  /**
   * Return the name of the hash function.
   * @return The name of the hash function
   */
  @Override
  public String toString() {
    return name;
  }

}
