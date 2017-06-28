/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opentext.ia.test.TestCase;


public class WhenProvidingFileSizes extends TestCase {

  private static final long KILO = 1024;

  @Test
  public void shouldUseHumanReadableNames() {
    int bytes = random();
    int kilo = random();
    int mega = random();
    int giga = random();
    int tera = random();

    assertEquals("bytes", bytes, FileSize.of(bytes).bytes());
    assertEquals("kilo", kilo * KILO, FileSize.of(kilo).kiloBytes());
    assertEquals("mega", mega * KILO * KILO, FileSize.of(mega).megaBytes());
    assertEquals("giga", giga * KILO * KILO * KILO, FileSize.of(giga).gigaBytes());
    assertEquals("tera", tera * KILO * KILO * KILO * KILO, FileSize.of(tera).teraBytes());
  }

  private int random() {
    return randomInt(1, (int)KILO - 1);
  }

}
