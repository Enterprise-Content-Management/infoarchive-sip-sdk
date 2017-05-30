/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

public class FileSize {

  private final int number;

  public static FileSize of(int number) {
    return new FileSize(number);
  }

  FileSize(int number) {
    this.number = number;
  }

  public long bytes() {
    return kiloPower(0);
  }

  private long kiloPower(int power) {
    long result = number;
    for (int n = 0; n < power; n++) {
      result *= 1024;
    }
    return result;
  }

  public long kiloBytes() {
    return kiloPower(1);
  }

  public long megaBytes() {
    return kiloPower(2);
  }

  public long gigaBytes() {
    return kiloPower(3);
  }

  public long teraBytes() {
    return kiloPower(4);
  }

}
