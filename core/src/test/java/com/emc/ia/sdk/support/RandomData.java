/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support;

import java.security.SecureRandom;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;


public final class RandomData {

  private static final int MAX_STRING_LENGTH = 64;
  private static final int MAX_BYTES = 64;
  private static final int MAX_PROPERTIES = 10;
  private static final int MIN_INTEGER = 0;
  private static final int MAX_INTEGER = 1000;
  private static final double MIN_DOUBLE = 0;
  private static final double MAX_DOUBLE = 1000;
  private static final short MIN_SHORT = 0;
  private static final short MAX_SHORT = Short.MAX_VALUE;

  private final Random random;

  public RandomData() {
    this(new SecureRandom());
  }

  public RandomData(Random random) {
    this.random = random;
  }

  public String string() {
    return string(integer(3, MAX_STRING_LENGTH));
  }

  public String string(int length) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < length; i++) {
      result.append(lowercaseLetter());
    }
    return result.toString();
  }

  public char lowercaseLetter() {
    return (char)('a' + integer('z' - 'a' + 1));
  }

  public int integer() {
    return integer(MIN_INTEGER, MAX_INTEGER);
  }

  public int integer(int max) {
    return integer(MIN_INTEGER, max);
  }

  public int integer(int min, int max) {
    if (min >= max) {
      throw new IllegalArgumentException(String.format("Min (%d) must be less than max (%d)", min, max));
    }
    return min + random.nextInt(max - min);
  }

  public short shortInteger() {
    return shortInteger(MIN_SHORT, MAX_SHORT);
  }

  public short shortInteger(int max) {
    return shortInteger(MIN_SHORT, max);
  }

  public short shortInteger(int min, int max) {
    return (short)integer(min, max);
  }

  public byte[] bytes() {
    byte[] result = new byte[integer(1, MAX_BYTES)];
    random.nextBytes(result);
    return result;
  }

  public double floatingPoint() {
    return floatingPoint(MIN_DOUBLE, MAX_DOUBLE);
  }

  public double floatingPoint(double max) {
    return floatingPoint(MIN_DOUBLE, max);
  }

  public double floatingPoint(double min, double max) {
    return min + random.nextDouble() * (max - min);
  }

  public boolean logical() {
    return logical(50);
  }

  public boolean logical(int percent) {
    return integer(100) < percent;
  }

  public Dictionary<String, String> emptyProperties() {
    return new Hashtable<>();
  }

  public Dictionary<String, String> properties() {
    Dictionary<String, String> result = emptyProperties();
    int len = integer(MAX_PROPERTIES);
    for (int i = 0; i < len; i++) {
      result.put(string(), string());
    }
    return result;
  }

  public Dictionary<String, Object> objectProperties() {
    Dictionary<String, Object> result = new Hashtable<>();
    int len = integer(MAX_PROPERTIES);
    for (int i = 0; i < len; i++) {
      result.put(string(), string());
    }
    return result;
  }

}
